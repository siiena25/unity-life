ALTER TABLE users
    ADD CONSTRAINT uniqueemail UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT check_gender CHECK (gender IN ('Male', 'Female'));

ALTER TABLE users
    ADD CONSTRAINT check_role CHECK (roleid IN (1, 2));

ALTER TABLE friends
    ADD CONSTRAINT check_status CHECK (status IN (1, 2, 3, 4, 5));

ALTER TABLE membership
    ADD CONSTRAINT memberid_fk
        FOREIGN KEY (userid)
            REFERENCES users (userid)
            ON DELETE CASCADE;

ALTER TABLE friends
    ADD CONSTRAINT userone_fk
        FOREIGN KEY (useroneid)
            REFERENCES users (userid)
            ON DELETE CASCADE;

ALTER TABLE friends
    ADD CONSTRAINT usertwo_fk
        FOREIGN KEY (usertwoid)
            REFERENCES users (userid)
            ON DELETE CASCADE;
