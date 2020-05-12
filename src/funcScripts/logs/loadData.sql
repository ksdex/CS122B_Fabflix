load data local infile 'MovieRecordData.txt'
into table movies
fields terminated by '|' optionally enclosed by '"' escaped by '"'
lines terminated by '\r\n'
(id,@title,@year,@director)
set
title = nullif(@title,""),
year = nullif(@year,""),
director = nullif(@director,"");

