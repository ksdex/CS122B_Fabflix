load data local infile 'MovieRecordData.txt'
into table movies
fields terminated by '|' optionally enclosed by '"' escaped by '"'
lines terminated by '[]'(id,@title,@year,@director)
set
title = nullif(@title,""),
year = nullif(@year,""),
director = nullif(@director,"");

load data local infile 'RatingRecordData.txt'
into table ratings
fields terminated by '|' optionally enclosed by '"' escaped by '"'
lines terminated by '[]'(movieId,rating,numVotes);

