load data local infile 'GenreInMoviesRecordData.txt'
into table genres_in_movies
fields terminated by '|' optionally enclosed by '"' escaped by '"'
lines terminated by '[]'(genreId,movieId);

