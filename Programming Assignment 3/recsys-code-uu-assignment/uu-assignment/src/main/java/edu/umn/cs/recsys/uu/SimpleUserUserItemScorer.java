package edu.umn.cs.recsys.uu;

import java.util.List;

import it.unimi.dsi.fastutil.longs.LongSet;

import org.grouplens.lenskit.basic.AbstractItemScorer;
import org.grouplens.lenskit.data.dao.ItemEventDAO;
import org.grouplens.lenskit.data.dao.UserEventDAO;
import org.grouplens.lenskit.data.event.Rating;
import org.grouplens.lenskit.data.history.History;
import org.grouplens.lenskit.data.history.RatingVectorUserHistorySummarizer;
import org.grouplens.lenskit.data.history.UserHistory;
import org.grouplens.lenskit.scored.ScoredId;
import org.grouplens.lenskit.util.TopNScoredItemAccumulator;
import org.grouplens.lenskit.vectors.MutableSparseVector;
import org.grouplens.lenskit.vectors.SparseVector;
import org.grouplens.lenskit.vectors.VectorEntry;
import org.grouplens.lenskit.vectors.similarity.CosineVectorSimilarity;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * User-user item scorer.
 * @author <a href="http://www.grouplens.org">GroupLens Research</a>
 */
public class SimpleUserUserItemScorer extends AbstractItemScorer {
    private final UserEventDAO userDao;
    private final ItemEventDAO itemDao;

    @Inject
    public SimpleUserUserItemScorer(UserEventDAO udao, ItemEventDAO idao) {
        userDao = udao;
        itemDao = idao;
    }

    @Override
    public void score(long user, @Nonnull MutableSparseVector scores) { 
        SparseVector userVector = getUserRatingVector(user);
        //get mean centered rating for user u
        MutableSparseVector meanUserRating = userVector.mutableCopy();
		meanUserRating.add(-meanUserRating.mean());
        // TODO Score items for this user using user-user collaborative filtering

        // This is the loop structure to iterate over items to score
        for (VectorEntry e: scores.fast(VectorEntry.State.EITHER)) {// scores is a movie-predict vector
        	//get users who have rated that movie

        	LongSet possibleNeighbors = itemDao.getUsersForItem(e.getKey()); // e.getKey() retrieves movie id
        	//get neighbors which excludes user u
        	possibleNeighbors.remove(user);
        	
        	//create a TopNScoredItemAccumulator object
        	TopNScoredItemAccumulator acc = new TopNScoredItemAccumulator(30);
        	
        	
        	for(long neighbor: possibleNeighbors){
        		//get mean centered rating for neighbors
        		SparseVector neighborVector = getUserRatingVector(neighbor);
        		MutableSparseVector meanNeighborRating = neighborVector.mutableCopy();
        		meanNeighborRating.add(-meanNeighborRating.mean());
        		
        		//calculate similarity using mean cetered rating from user u and neighbors
        		double similarities = new CosineVectorSimilarity().similarity(meanUserRating,meanNeighborRating);
        		//push neighbor and related similarity
        		acc.put(neighbor, similarities);
        		
        		
        	}
        	
        	//fetch top 30 neighbor(user id) with similarity(score)
        	List<ScoredId> top30Users = acc.finish();
        	
        	//initialize numerator/denominator
        	double numerator = 0.0;
        	double denominator = 0.0;
        	
        	
        	for(ScoredId score : top30Users){
        		
        		long neighbor30 = score.getId();
        		//get mean centered rating for top 30 neighbors
        		SparseVector neighborVector30 = getUserRatingVector(neighbor30);
        		MutableSparseVector meanNeighborRating30 = neighborVector30.mutableCopy();
        		meanNeighborRating30.add(-meanNeighborRating30.mean());
        		
        		
        		//calculate numerator/denominator for each neighbor
            	numerator += meanNeighborRating30.get(e.getKey()) * score.getScore();
            	denominator += Math.abs(score.getScore());
        	}
        	//get predict
        	double predict = userVector.mean() + numerator / denominator;
        	//populate scores vector
        	scores.set(e.getKey(), predict);
        }
    }

    /**
     * Get a user's rating vector.
     * @param user The user ID.
     * @return The rating vector.
     */
    private SparseVector getUserRatingVector(long user) {
        UserHistory<Rating> history = userDao.getEventsForUser(user, Rating.class);
        if (history == null) {
            history = History.forUser(user);
        }
        return RatingVectorUserHistorySummarizer.makeRatingVector(history);
    }
}
