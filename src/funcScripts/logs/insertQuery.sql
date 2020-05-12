load data infile 'C:\ProgramData\MySQL\MySQL Server 8.0\Uploads\MovieRecordData.txt'
into table movies
fields terminated by ',' optionally enclosed by '"' escaped by '"'
lines terminated by '\r\n'
(id,@title,@year,@director)
set
title = nullif(@title,""),
year = nullif(@year,""),
director = nullif(@director,"");