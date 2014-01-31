package edu.umn.cs.recsys.ii;

import org.grouplens.lenskit.basic.AbstractItemScorer;
import org.grouplens.lenskit.data.dao.UserEventDAO;
import org.grouplens.lenskit.data.event.Rating;
import org.grouplens.lenskit.data.history.History;
import org.grouplens.lenskit.data.history.RatingVectorUserHistorySummarizer;
import org.grouplens.lenskit.data.history.UserHistory;
import org.grouplens.lenskit.knn.NeighborhoodSize;
import org.grouplens.lenskit.scored.ScoredId;
import org.grouplens.lenskit.vectors.MutableSparseVector;
import org.grouplens.lenskit.vectors.SparseVector;
import org.grouplens.lenskit.vectors.VectorEntry;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

/**
 * @author <a href="http://www.grouplens.org">GroupLens Research</a>
 */
public class SimpleItemItemScorer extends AbstractItemScorer {
    private final SimpleItemItemModel model;
    private final UserEventDAO userEvents;
    private final int neighborhoodSize;

    @Inject
    public SimpleItemItemScorer(SimpleItemItemModel m, UserEventDAO dao,
                                @NeighborhoodSize int nnbrs) {
        model = m;
        userEvents = dao;
        neighborhoodSize = nnbrs;
    }

    /**
     * Score items for a user.
     * @param user The user ID.
     * @param scores The score vector.  Its key domain is the items to score, and the scores
     *               (rating predictions) should be written back to this vector.
     */
    @Override
    public void score(long user, @Nonnull MutableSparseVector scores) {
        SparseVector ratings = getUserRatingVector(user);
        
        //work is MutableSparseVector whose key domain is ratings.keySet(). 
        //It is used as a temporary working vector to store similarity values for the current item being scored.
        MutableSparseVector work = MutableSparseVector.create(ratings.keySet());
        //set initial value to 0
        work.fill(0);
       
        for (VectorEntry e: scores.fast(VectorEntry.State.EITHER)) {
            long item = e.getKey();
           
           
            List<ScoredId> neighbors = model.getNeighbors(item);
            // TODO Score this item and save the score into scores
            // Compute sum of neighbors and weighted score for each item rated by the user            
            double neighborsSum = 0.0; 
            //we create a counter to limit the neighborhood size no larger than neighborhoodSize
            int neighbourCount = 0;
            //nbr is item id to score mapping
            for(ScoredId nbr : neighbors) {
            	
            	//we stop if it exceeds neighborhoodSize
            	if(neighbourCount == neighborhoodSize){
            		break;
            	}
            	/*
            	if(!ratings.containsKey(score.getId())) 
            		continue;
            		*/
            	//make sure you are rating the neighbor's movie id
            	if(ratings.containsKey(nbr.getId()) && nbr.getId() != item){
            		double similarity = nbr.getScore();                
            		double userRating = ratings.get(nbr.getId());                
            		double weightedScore = similarity * userRating;
            		neighborsSum += similarity; 
            		work.set(nbr.getId(), weightedScore);  
            		neighbourCount++;
            	}         
           }
           double weightedSum = work.sum();           
           scores.set(item, weightedSum / neighborsSum);   
        }
    }

    /**
     * Get a user's ratings.
     * @param user The user ID.
     * @return The ratings to retrieve.
     */
    private SparseVector getUserRatingVector(long user) {
        UserHistory<Rating> history = userEvents.getEventsForUser(user, Rating.class);
        if (history == null) {
            history = History.forUser(user);
        }

        return RatingVectorUserHistorySummarizer.makeRatingVector(history);
    }
}
