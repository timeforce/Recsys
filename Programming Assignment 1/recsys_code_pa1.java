import java.io.*;
import java.util.ArrayList;
import function.*;


class NonPersRecommender {

    public static void main(String[] args) {
	// Movies array contains the movie IDs of the top 5 movies.
    // 22
    // 329
    // 2501
	/*String simple_22[] = new String[5];
	String complex_22[] = new String[5];
	String simple_329[]= new String[5];
	String complex_329[] = new String[5];
	String simple_2501[]= new String[5];
	String complex_2501[] = new String[5];*/
	
	//Initialize the array
	NonPersRecommender test = new NonPersRecommender();
	  String[][] array = test.parse();

	/*for (int i = 0; i < 338355; i++) {
	   for (int j = 0; j < 3; j++) {
	    System.out.println("array["+i+"]["+j+"]"+array[i][j]);
	   }
	
	}*/
	
	//simple_22=
	
	  //test.Simple("11",array);
	  //test.Simple("121",array);
	  //test.Simple("8587",array);
	  //test.Simple("22",array);
	  //test.Simple("2501",array);
	  //test.Simple("329",array);
		test.Complex("11", array);	
	
	/*simple_329=test.Simple("329",array);
	
	simple_2501=test.Simple("2501",array);
	
    complex_22=test.Complex("22",array);
	
	complex_329=test.Complex("329",array);
	
	complex_2501=test.Complex("2501",array);*/
	
	//test.FindUser("22", array);
	
	// Write the top 5 movies, one per line, to a text file.
//	try {
//	    PrintWriter writer = new PrintWriter("pa1-result.txt","UTF-8");
//       
//	    for (int movieId : movies) {
//		writer.println(movieId);
//	    }
//
//	    writer.close();
//	    
//	} catch (Exception e) {
//	    System.out.println(e.getMessage());
//	}
    }
    
    
    private String[][] parse() {
    	String[][] result = new String[338355][3];
    	  
    	  try {
    		  
    		  BufferedReader br = new BufferedReader(new FileReader("C:/Users/yinaliu/Test2/Homework1/input/recsys_data_ratings.csv")); 	    		
       		  //BufferedReader br = new BufferedReader(new FileReader("C:/Users/yinaliu/Test2/Homework1/input/smaller_test_sample.csv")); 	    		   	    
    		int cnt = 0;
    		
    		String line;
    		while (null != (line = br.readLine())) {
    			
    			result[cnt] = parseLine(line);
    		cnt++;
    		
    		}
    	  } catch (FileNotFoundException e) {
    	   e.printStackTrace();
    	  } catch (IOException e) {
    	   e.printStackTrace();
    	  }
    	  return result;
    	 }

    
    private String[] parseLine(String line) {
		  String[] nums = line.split(",");
		  return nums;
		 }
   
    private void Simple(String x, String[][] MovieList){
    	  //String[] Top_5_Movie= new String[5];
    	  
    	  //get user list who has seen the movie x
    	  ArrayList<String> user = new ArrayList();
  		  
          int cnt=0;
      	  for (int i = 0; i < 338355; i++) {
         // for (int i = 0; i < 364; i++) {
          if (MovieList[i][1].matches(x))
      		{
      			user.add(MovieList[i][0]);
      			//System.out.println(user.get(cnt));
                  cnt++;
      		}  	
      	  }
      	  
      	  
      	//get user's other movie list
      	int movie_cnt=0;
      	ArrayList<String> movies = new ArrayList();
      	  for (int user_cnt=0;user_cnt<user.size()-1;user_cnt++){
      	  for (int j = 0; j < 338355; j++) {
      		int user_name = Integer.valueOf(MovieList[j][0]).intValue();	
      		int user_name2 = Integer.valueOf(user.get(user_cnt)).intValue();
      		  
  			if (MovieList[j][0].matches(user.get(user_cnt))&&!MovieList[j][1].matches(x))
      		{
      			movies.add(MovieList[j][1]);
      			//System.out.println(MovieList[j][1]); 			
      		}  
  			else if(user_name>user_name2)
  				break;  				
      	  }
      	  }
      	//System.out.println("Size"+movies.size()); 
      	  
      	//check the frequency of the movies
      	ArrayList<String> unique_movies = new ArrayList(); 
      	ArrayList<Integer> movies_frequency = new ArrayList(); 
      	for (int k = 0; k < movies.size(); k++) {
  		   
      		if (!unique_movies.contains(movies.get(k)))
      		{
      			unique_movies.add(movies.get(k));
      		    
      		}
      	  }
      	
      	//System.out.println(unique_movies.size());
    	 for (int t = 0; t < unique_movies.size();t++)
    	 {    
    		 int movie_frequency_cnt=0;
    		 for (int l = 0; l < movies.size(); l++){ 
    			// System.out.println("l:"+l);
    			 if(movies.get(l).matches(unique_movies.get(t)))    				 
    				 {
    				 movie_frequency_cnt++;    				 
    				 }
    			
    	 }
    		 movies_frequency.add(movie_frequency_cnt);
    		 
    		 
    	 }
    	 
    	 
    	 
    	 //Sort the movie frequency
    	 InsertSorter sort= new InsertSorter();
    	 
    	 //System.out.println(unique_movies); 
    	 //System.out.println(movies_frequency); 
    	 
    	 sort.sort(movies_frequency,unique_movies);
    	 
    	     	 
    	 System.out.println(unique_movies); 
    	 System.out.println(movies_frequency); 
    	 //System.out.println(unique_movies.size());
    	 //System.out.println(movies_frequency.size());
    	 System.out.println(user.size());
    	 
    	 double[] rate= new double[movies_frequency.size()];
    	 
    	 for (int t = 0; t < 5;t++)
    	 {    
    		rate[t]=(double)(movies_frequency.get(t))/(double)(user.size());
    		System.out.println(rate[t]);
    	 }
    	 //System.out.println(rate);
    	 // return Top_5_Movie;
    }
    
    private void Complex(String x, String[][] MovieList){
  	  //String[] Top_5_Movie= new String[5];
  	
  	//get the user who didn't see movie x  
    	
    	WhoSeeMovie name= new WhoSeeMovie();   
  	  ArrayList<String> user_not_see = name.whonotseemoive(x, MovieList);
  	
	  //seeing movie x users
	  ArrayList<String> user_see_movie = name.whoseemoive(x, MovieList);
  	  
  	  
  	//get user's other movie list
  	
  	ArrayList<String> seeing_movies = name.Unique_Movie_List(x, user_see_movie, MovieList);
  	 
	//get not seeing movie x user's list
   
  	ArrayList<String> user_not_seeing_movies = name.Unique_Movie_List(user_not_see, MovieList);
    	  
  	ArrayList<String> shared_movies=new ArrayList();
  	
  	for(int i=0;i<seeing_movies.size();i++)
  	{
  		if(user_not_seeing_movies.contains(seeing_movies.get(i))&&!shared_movies.contains(seeing_movies.get(i)))
  			shared_movies.add(seeing_movies.get(i));
  		System.out.println("shared movies:"+seeing_movies.get(i));
  	}
    
  	ArrayList<Integer> movie_frequency=name.Frequency(shared_movies, user_not_seeing_movies) ;
  	System.out.println("movie_frequency:"+movie_frequency);

  	
  	/*double[] rate= new double[movie_frequency.size()];
	 
	 for (int t = 0; t < movie_frequency.size();t++)
	 {    
		rate[t]=(double)(movie_frequency.get(t))/(double)(user_not_see.size());
		System.out.println(rate[t]);
	 }*/
  }
    
}