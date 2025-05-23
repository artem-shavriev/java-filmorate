DROP TABLE IF EXISTS USER_ CASCADE;
DROP TABLE IF EXISTS FRIENDS_IDS CASCADE;
DROP TABLE IF EXISTS FILM CASCADE;
DROP TABLE IF EXISTS MPA CASCADE;
DROP TABLE IF EXISTS GENRE CASCADE;
DROP TABLE IF EXISTS LIKES_FROM_USERS CASCADE;
DROP TABLE IF EXISTS FILM_GENRE CASCADE;
DROP TABLE IF EXISTS REVIEWS CASCADE;
DROP TABLE IF EXISTS REVIEWS_MARK CASCADE;
DROP TABLE IF EXISTS FILM_DIRECTOR CASCADE;
DROP TABLE IF EXISTS DIRECTORS CASCADE;
DROP TABLE IF EXISTS FEED CASCADE;

CREATE TABLE IF NOT EXISTS MPA (
MPA_ID INTEGER NOT NULL AUTO_INCREMENT,
MPA_NAME VARCHAR_IGNORECASE,
CONSTRAINT MPA_ID_PK PRIMARY KEY (MPA_ID)
);

CREATE TABLE IF NOT EXISTS FILM (
	FILM_ID INTEGER NOT NULL AUTO_INCREMENT,
	NAME VARCHAR_IGNORECASE NOT NULL,
	DESCRIPTION VARCHAR_IGNORECASE NOT NULL,
	RELEASE_DATE TIMESTAMP,
	DURATION INTEGER NOT NULL,
	MPA_ID INTEGER,
	CONSTRAINT FILM_PK PRIMARY KEY (FILM_ID),
	CONSTRAINT MPA_FK FOREIGN KEY (MPA_ID) REFERENCES PUBLIC.MPA(MPA_ID)
);


CREATE TABLE IF NOT EXISTS GENRE (
	GENRE_ID INTEGER NOT NULL AUTO_INCREMENT,
	NAME VARCHAR_IGNORECASE NOT NULL,
	CONSTRAINT GENRE_PK PRIMARY KEY (GENRE_ID)
);

CREATE TABLE IF NOT EXISTS FILM_GENRE (
	FILM_ID INTEGER NOT NULL,
	GENRE_ID INTEGER NOT NULL,
	CONSTRAINT FILM_GENRE_PK PRIMARY KEY (FILM_ID,GENRE_ID),
	CONSTRAINT GENRE_ID_FK FOREIGN KEY (GENRE_ID) REFERENCES PUBLIC.GENRE(GENRE_ID),
	CONSTRAINT FILM_ID_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILM(FILM_ID)
);

CREATE TABLE IF NOT EXISTS USER_ (
	USER_ID INTEGER NOT NULL AUTO_INCREMENT,
	EMAIL VARCHAR_IGNORECASE NOT NULL,
	LOGIN VARCHAR_IGNORECASE,
	NAME VARCHAR_IGNORECASE NOT NULL,
    BIRTHDAY TIMESTAMP,
	CONSTRAINT USER_PK PRIMARY KEY (USER_ID)
);

CREATE TABLE IF NOT EXISTS LIKES_FROM_USERS (
	FILM_ID INTEGER NOT NULL,
	USER_ID INTEGER NOT NULL,
	CONSTRAINT LIKES_FROM_USERS_PK PRIMARY KEY (FILM_ID, USER_ID),
	CONSTRAINT LIKES_FROM_USERS_USER_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USER_(USER_ID),
	CONSTRAINT LIKES_FROM_USERS_FILM_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILM(FILM_ID)
);

CREATE TABLE IF NOT EXISTS FRIENDS_IDS (
	USER_ID INTEGER NOT NULL,
	FRIEND_ID INTEGER,
	CONSTRAINT FRIENDS_IDS_PK PRIMARY KEY (USER_ID, FRIEND_ID),
	CONSTRAINT USER_ID_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USER_(USER_ID) ON DELETE CASCADE,
	CONSTRAINT FRIEND_ID_FK FOREIGN KEY (FRIEND_ID) REFERENCES PUBLIC.USER_(USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS FEED (
    event_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id INTEGER,
    timestamp BIGINT NOT NULL,
    event_type VARCHAR(20) NOT NULL,
    operation VARCHAR(20) NOT NULL,
    entity_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES USER_ (USER_ID) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS REVIEWS (
    REVIEWS_ID INTEGER NOT NULL AUTO_INCREMENT,
    CONTENT VARCHAR_IGNORECASE,
    IS_POSITIVE BOOLEAN,
    USER_ID INTEGER NOT NULL,
    FILM_ID INTEGER NOT NULL,
    CONSTRAINT REVIEWS_PK PRIMARY KEY (REVIEWS_ID),
    CONSTRAINT USER_ID_REVIEWS_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USER_(USER_ID) ON DELETE CASCADE,
    CONSTRAINT FILM_ID_REVIEWS_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILM(FILM_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REVIEWS_MARK (
    REVIEWS_ID INTEGER NOT NULL,
    USER_ID INTEGER NOT NULL,
    MARK VARCHAR_IGNORECASE,
    CONSTRAINT REVIEWS_MARK_PK PRIMARY KEY (REVIEWS_ID, USER_ID),
    CONSTRAINT REVIEWS_ID_FK FOREIGN KEY (REVIEWS_ID) REFERENCES PUBLIC.REVIEWS(REVIEWS_ID) ON DELETE CASCADE,
    CONSTRAINT USER_ID_REVIEWS_MARK_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USER_(USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS DIRECTORS (
	DIRECTOR_ID INTEGER NOT NULL AUTO_INCREMENT,
	NAME VARCHAR_IGNORECASE NOT NULL,
	CONSTRAINT DIRECTOR_PK PRIMARY KEY (DIRECTOR_ID)
);

CREATE TABLE IF NOT EXISTS FILM_DIRECTOR (
	FILM_ID INTEGER NOT NULL,
	DIRECTOR_ID INTEGER NOT NULL,
	CONSTRAINT FILM_DIRECTOR_PK PRIMARY KEY (FILM_ID,DIRECTOR_ID),
	CONSTRAINT DIRECTOR_ID_FK FOREIGN KEY (DIRECTOR_ID) REFERENCES PUBLIC.DIRECTORS(DIRECTOR_ID) ON DELETE CASCADE,
	CONSTRAINT FILM_DIRECTOR_FILM_ID_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILM(FILM_ID)
);