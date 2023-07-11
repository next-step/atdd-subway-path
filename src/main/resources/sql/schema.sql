-- truncate table line;
-- truncate table section;
-- truncate table station;

-- delete from line where id >0;
-- delete from station where id >0;
-- delete from section where id >0;

drop table if exists line;
drop table if exists station;
drop table if exists section;

create table station (
                      ID int not null AUTO_INCREMENT,
                      NAME varchar(20) not null,
                      PRIMARY KEY ( ID )
);

create table line (
                         ID int not null AUTO_INCREMENT,
                         NAME varchar(20) not null,
                         COLOR varchar(20) not null,
                         up_station_id int not null,
                         down_station_id int not null,
                         PRIMARY KEY ( ID )
);

create table section (
                      ID int not null AUTO_INCREMENT,
                      line_id int not null,
                      up_station_id int not null,
                      down_station_id int not null,
                      distance int not null,
                      PRIMARY KEY ( ID )
);