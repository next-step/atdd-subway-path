create table if not exists STATION
(
    id bigint auto_increment not null,
    name varchar(255) not null,
    primary key(id)
);

create table if not exists LINE
(
    id bigint auto_increment not null,
    name varchar(255) not null,
    start_time time not null,
    last_time time not null,
    time_interval int not null,
    extra_fare int,
    primary key (id)
);

create table if not exists EDGE
(
    id bigint auto_increment not null,
    line_id bigint not null,
    elapsed_time int not null,
    distance decimal(4,2) not null,
    source_station_id bitint not null,
    target_station_id bitint not null,
    primary key(id)
);

