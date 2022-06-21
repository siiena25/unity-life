CREATE TABLE if not exists FRIENDS (
  pairid serial NOT NULL PRIMARY KEY,
  useroneid INT NOT NULL,
  usertwoid INT NOT NULL,
  status INT NOT NULL);

CREATE TABLE if not exists MEMBERSHIP (
  memberid serial NOT NULL PRIMARY KEY,
  groupid INT NOT NULL,
  userid INT NOT NULL,
  status INT NOT NULL);

CREATE TABLE if not exists GROUPS (
  groupid int NOT NULL PRIMARY KEY,
  groupname VARCHAR(255) NOT NULL,
  groupadminid VARCHAR(255) NOT NULL);

CREATE TABLE if not exists posts (
  id serial NOT NULL PRIMARY KEY,
  authorfirst VARCHAR(255) NOT NULL,
  authorlast VARCHAR(255) NOT NULL,
  content VARCHAR(500) NOT NULL,
  likes INT NOT NULL,
  time INT NOT NULL,
  visibility INT NOT NULL);

CREATE TABLE if not exists EVENTS (
    eventId serial NOT NULL PRIMARY KEY,
    authorId INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    categoryTitle VARCHAR(255) NOT NULL,
    eventAvatar VARCHAR(2048) NULL,
    address TEXT NOT NULL,
    description TEXT NULL,
    timeStart TIMESTAMP NOT NULL,
    timeEnd TIMESTAMP NOT NULL,
    latitude FLOAT NOT NULL,
    longitude FLOAT NOT NULL);

CREATE TABLE if not exists USERS (
  userid serial NOT NULL PRIMARY KEY,
  firstname VARCHAR(255) NOT NULL,
  lastname VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  age INT NOT NULL CHECK (age > 0),
  gender VARCHAR(255) NOT NULL,
  country VARCHAR(255) NOT NULL,
  city VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  roleid INT NOT NULL);

CREATE TABLE if not exists ROLE (
  role VARCHAR(255) NOT NULL,
  roleid INT NOT NULL);

CREATE TABLE if not exists LOG_INFO (
  userid INT NOT NULL PRIMARY KEY,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  token VARCHAR(255) NOT NULL);

insert into FRIENDS (useroneid, usertwoid, status) values (9, 14, 5);
insert into FRIENDS (useroneid, usertwoid, status) values (7, 4, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (31, 14, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (24, 15, 2);
insert into FRIENDS (useroneid, usertwoid, status) values (16, 4, 1);
insert into FRIENDS (useroneid, usertwoid, status) values (3, 5, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (30, 12, 2);
insert into FRIENDS (useroneid, usertwoid, status) values (11, 12, 4);
insert into FRIENDS (useroneid, usertwoid, status) values (8, 5, 5);
insert into FRIENDS (useroneid, usertwoid, status) values (31, 2, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (6, 3, 1);
insert into FRIENDS (useroneid, usertwoid, status) values (31, 22, 5);
insert into FRIENDS (useroneid, usertwoid, status) values (20, 14, 2);
insert into FRIENDS (useroneid, usertwoid, status) values (15, 6, 1);
insert into FRIENDS (useroneid, usertwoid, status) values (10, 11, 5);
insert into FRIENDS (useroneid, usertwoid, status) values (12, 3, 1);
insert into FRIENDS (useroneid, usertwoid, status) values (6, 9, 2);
insert into FRIENDS (useroneid, usertwoid, status) values (30, 10, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (26, 19, 5);
insert into FRIENDS (useroneid, usertwoid, status) values (8, 10, 4);
insert into FRIENDS (useroneid, usertwoid, status) values (3, 13, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (20, 7, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (15, 31, 5);
insert into FRIENDS (useroneid, usertwoid, status) values (24, 1, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (5, 28, 2);
insert into FRIENDS (useroneid, usertwoid, status) values (5, 10, 1);
insert into FRIENDS (useroneid, usertwoid, status) values (13, 21, 4);
insert into FRIENDS (useroneid, usertwoid, status) values (33, 14, 4);
insert into FRIENDS (useroneid, usertwoid, status) values (25, 6, 1);
insert into FRIENDS (useroneid, usertwoid, status) values (15, 32, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (21, 23, 1);
insert into FRIENDS (useroneid, usertwoid, status) values (20, 23, 4);
insert into FRIENDS (useroneid, usertwoid, status) values (18, 31, 5);
insert into FRIENDS (useroneid, usertwoid, status) values (4, 32, 1);
insert into FRIENDS (useroneid, usertwoid, status) values (2, 31, 4);
insert into FRIENDS (useroneid, usertwoid, status) values (12, 4, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (21, 16, 5);
insert into FRIENDS (useroneid, usertwoid, status) values (5, 33, 2);
insert into FRIENDS (useroneid, usertwoid, status) values (4, 15, 5);
insert into FRIENDS (useroneid, usertwoid, status) values (6, 2, 5);
insert into FRIENDS (useroneid, usertwoid, status) values (18, 24, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (7, 31, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (1, 5, 1);
insert into FRIENDS (useroneid, usertwoid, status) values (33, 11, 5);
insert into FRIENDS (useroneid, usertwoid, status) values (8, 24, 4);
insert into FRIENDS (useroneid, usertwoid, status) values (10, 20, 4);
insert into FRIENDS (useroneid, usertwoid, status) values (29, 14, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (18, 2, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (5, 2, 4);
insert into FRIENDS (useroneid, usertwoid, status) values (16, 18, 5);
insert into FRIENDS (useroneid, usertwoid, status) values (24, 30, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (32, 33, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (12, 32, 5);
insert into FRIENDS (useroneid, usertwoid, status) values (8, 23, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (9, 17, 2);
insert into FRIENDS (useroneid, usertwoid, status) values (28, 29, 1);
insert into FRIENDS (useroneid, usertwoid, status) values (14, 11, 4);
insert into FRIENDS (useroneid, usertwoid, status) values (27, 19, 4);
insert into FRIENDS (useroneid, usertwoid, status) values (18, 17, 2);
insert into FRIENDS (useroneid, usertwoid, status) values (16, 15, 5);
insert into FRIENDS (useroneid, usertwoid, status) values (6, 28, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (9, 31, 2);
insert into FRIENDS (useroneid, usertwoid, status) values (3, 16, 4);
insert into FRIENDS (useroneid, usertwoid, status) values (27, 19, 2);
insert into FRIENDS (useroneid, usertwoid, status) values (4, 10, 1);
insert into FRIENDS (useroneid, usertwoid, status) values (6, 8, 5);
insert into FRIENDS (useroneid, usertwoid, status) values (13, 5, 5);
insert into FRIENDS (useroneid, usertwoid, status) values (29, 3, 4);
insert into FRIENDS (useroneid, usertwoid, status) values (24, 8, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (32, 22, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (16, 3, 2);
insert into FRIENDS (useroneid, usertwoid, status) values (30, 3, 2);
insert into FRIENDS (useroneid, usertwoid, status) values (29, 4, 4);
insert into FRIENDS (useroneid, usertwoid, status) values (16, 12, 5);
insert into FRIENDS (useroneid, usertwoid, status) values (10, 1, 2);
insert into FRIENDS (useroneid, usertwoid, status) values (21, 26, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (31, 31, 4);
insert into FRIENDS (useroneid, usertwoid, status) values (22, 7, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (28, 8, 1);
insert into FRIENDS (useroneid, usertwoid, status) values (32, 29, 2);
insert into FRIENDS (useroneid, usertwoid, status) values (7, 22, 4);
insert into FRIENDS (useroneid, usertwoid, status) values (12, 26, 1);
insert into FRIENDS (useroneid, usertwoid, status) values (8, 3, 2);
insert into FRIENDS (useroneid, usertwoid, status) values (27, 11, 2);
insert into FRIENDS (useroneid, usertwoid, status) values (19, 27, 5);
insert into FRIENDS (useroneid, usertwoid, status) values (7, 32, 1);
insert into FRIENDS (useroneid, usertwoid, status) values (5, 4, 4);
insert into FRIENDS (useroneid, usertwoid, status) values (22, 28, 4);
insert into FRIENDS (useroneid, usertwoid, status) values (8, 4, 1);
insert into FRIENDS (useroneid, usertwoid, status) values (28, 24, 2);
insert into FRIENDS (useroneid, usertwoid, status) values (11, 2, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (25, 27, 4);
insert into FRIENDS (useroneid, usertwoid, status) values (22, 3, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (33, 3, 2);
insert into FRIENDS (useroneid, usertwoid, status) values (16, 19, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (25, 32, 5);
insert into FRIENDS (useroneid, usertwoid, status) values (18, 13, 1);
insert into FRIENDS (useroneid, usertwoid, status) values (20, 31, 3);
insert into FRIENDS (useroneid, usertwoid, status) values (3, 26, 5);
insert into FRIENDS (useroneid, usertwoid, status) values (17, 31, 4);

insert into MEMBERSHIP (groupid, memberid, status) values (1, 30, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (7, 16, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (11, 24, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (5, 20, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (6, 29, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (5, 7, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (8, 33, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (10, 15, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (16, 24, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (20, 20, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (1, 27, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (17, 29, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (12, 13, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (8, 16, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (16, 31, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (8, 26, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (8, 12, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (7, 27, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (15, 8, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (11, 5, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (4, 3, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (11, 31, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (16, 6, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (13, 12, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (17, 5, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (8, 27, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (8, 13, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (18, 11, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (1, 16, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (6, 31, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (19, 1, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (11, 21, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (2, 2, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (2, 23, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (15, 24, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (10, 2, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (3, 1, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (18, 7, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (15, 16, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (15, 22, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (13, 26, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (3, 4, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (3, 8, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (1, 19, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (17, 12, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (12, 26, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (14, 17, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (10, 10, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (2, 22, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (8, 15, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (5, 3, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (6, 23, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (15, 32, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (18, 5, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (12, 33, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (3, 19, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (20, 19, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (18, 7, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (5, 22, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (13, 4, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (10, 7, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (13, 18, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (2, 31, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (13, 3, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (7, 32, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (7, 23, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (3, 14, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (20, 18, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (15, 12, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (7, 14, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (13, 20, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (4, 17, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (13, 21, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (4, 31, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (10, 22, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (14, 10, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (15, 27, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (5, 6, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (7, 27, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (11, 12, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (20, 22, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (18, 8, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (20, 25, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (16, 33, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (18, 20, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (8, 20, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (17, 11, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (9, 12, 2);
insert into MEMBERSHIP (groupid, memberid, status) values (16, 32, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (20, 24, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (10, 23, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (20, 9, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (13, 2, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (19, 14, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (10, 12, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (5, 5, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (5, 30, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (15, 22, 3);
insert into MEMBERSHIP (groupid, memberid, status) values (8, 16, 1);
insert into MEMBERSHIP (groupid, memberid, status) values (19, 18, 1);

insert into ROLE (role, roleid) values ('USER', 2);
insert into ROLE (role, roleid) values ('ADMIN', 1);