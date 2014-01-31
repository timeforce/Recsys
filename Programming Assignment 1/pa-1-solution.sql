--users who have rated movie 107
SEL COUNT(DISTINCT user_id) FROM P_Support_CN_T.user_movie_rating
WHERE movie_id = 11

--find no of  users (ratings) for each movie that is rated by users who have also rated movie 11
SEL a.movie_id, COUNT(a.user_id) FROM P_Support_CN_T.user_movie_rating a
JOIN 
(SELECT user_id FROM P_Support_CN_T.user_movie_rating WHERE movie_id = 11 GROUP BY 1) t -- get users who have rated movie 11
ON a.user_id = t.user_id
GROUP BY 1
ORDER BY 2 DESC -- order by no of users descending 


--users who have NOT rated movie 107
SEL COUNT(DISTINCT user_id) FROM P_Support_CN_T.user_movie_rating 
WHERE user_id NOT IN(SELECT user_id FROM P_Support_CN_T.user_movie_rating WHERE movie_id = 107)

--get (x and y) / x


SEL a.movie_id, CAST(COUNT(a.user_id) AS DECIMAL(10, 5)) / 2343 AS rate1 FROM P_Support_CN_T.user_movie_rating a
JOIN 
(SELECT user_id FROM P_Support_CN_T.user_movie_rating WHERE movie_id = 107 GROUP BY 1) t -- get users who have rated movie 11
ON a.user_id = t.user_id
WHERE a.movie_id NOT IN(107)
GROUP BY 1
ORDER BY 1

--get (!x and y) / !x
SEL a.movie_id, CAST(COUNT(a.user_id) AS DECIMAL(10,5))/3221 AS rate2 FROM P_Support_CN_T.user_movie_rating a
JOIN 
(SEL user_id FROM P_Support_CN_T.user_movie_rating 
WHERE user_id NOT IN(SELECT user_id FROM P_Support_CN_T.user_movie_rating WHERE movie_id = 107)
GROUP BY 1) t -- get users who have NOT rated movie 11
ON a.user_id = t.user_id
GROUP BY 1
ORDER BY 1



CREATE VOLATILE TABLE rate1 AS (
SEL a.movie_id, CAST(COUNT(a.user_id) AS DECIMAL(10, 5)) / 2343 AS rate1 FROM P_Support_CN_T.user_movie_rating a
JOIN 
(SELECT user_id FROM P_Support_CN_T.user_movie_rating WHERE movie_id = 107 GROUP BY 1) t -- get users who have rated movie 11
ON a.user_id = t.user_id
WHERE a.movie_id NOT IN(107)
GROUP BY 1
)
WITH DATA
PRIMARY INDEX(movie_id)
ON COMMIT PRESERVE ROWS


CREATE VOLATILE TABLE rate2 AS(
SEL a.movie_id, CAST(COUNT(a.user_id) AS DECIMAL(10,5))/3221 AS rate2 FROM P_Support_CN_T.user_movie_rating a
JOIN 
(SEL user_id FROM P_Support_CN_T.user_movie_rating 
WHERE user_id NOT IN(SELECT user_id FROM P_Support_CN_T.user_movie_rating WHERE movie_id = 107)
GROUP BY 1) t -- get users who have NOT rated movie 11
ON a.user_id = t.user_id
GROUP BY 1)
WITH DATA
PRIMARY INDEX(movie_id)
ON COMMIT PRESERVE ROWS


SEL a.movie_id, a.rate1/b.rate2 AS res FROM rate1 a JOIN rate2 b
ON a.movie_id = b.movie_id
GROUP BY 1,2
ORDER BY 2 DESC

 