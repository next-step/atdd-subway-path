SET REFERENTIAL_INTEGRITY FALSE; -- 모든 제약조건 비활성화
truncate table station;
truncate table line;
truncate table `section`;
SET REFERENTIAL_INTEGRITY TRUE; -- 모든 제약조건 활성화
insert into station (id, name) values (1, '지하철역');
insert into station (id, name) values (2, '새로운지하철역');
insert into station (id, name) values (3, '또다른지하철역');
ALTER TABLE station ALTER COLUMN id RESTART WITH 4;
