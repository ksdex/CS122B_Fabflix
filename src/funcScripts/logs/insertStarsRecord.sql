load data local infile 'StarsInMoviesRecordData.txt'
into table stars_in_movies
fields terminated by '|' optionally enclosed by '"' escaped by '"'
lines terminated by '\r\n'
(starId,movieId);

load data local infile 'StarsInMoviesRecordData.txt'
into table stars_in_movies
fields terminated by '|' optionally enclosed by '"' escaped by '"'
lines terminated by '\r\n'
(starId,movieId);

