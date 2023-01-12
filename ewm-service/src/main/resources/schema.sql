CREATE TABLE IF NOT EXISTS categories (
      category_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
      name VARCHAR(255),
      CONSTRAINT pk_category PRIMARY KEY (category_id),
      CONSTRAINT uq_category_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS users (
        user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
        name VARCHAR(255) NOT NULL,
        email VARCHAR(512) NOT NULL,
        CONSTRAINT pk_user PRIMARY KEY (user_id),
        CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS events (
     event_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
     title VARCHAR(120),
     annotation VARCHAR(2000),
     description VARCHAR(7000),
     event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
     participant_limit BIGINT,
     paid BOOLEAN NOT NULL,
     request_moderation BOOLEAN,
     confirmed_requests BIGINT,
     created_on TIMESTAMP WITHOUT TIME ZONE,
     published_on TIMESTAMP WITHOUT TIME ZONE,
     views BIGINT,
     lat FLOAT NOT NULL,
     lon FLOAT NOT NULL,
     category_id BIGINT NOT NULL,
     initiator_id BIGINT NOT NULL,
     state VARCHAR(50),
     CONSTRAINT pk_events PRIMARY KEY (event_id),
     CONSTRAINT fk_event_on_category FOREIGN KEY (category_id)
         REFERENCES categories (category_id),
     CONSTRAINT fi_event_on_initiator FOREIGN KEY (initiator_id)
         REFERENCES users (user_id),
     CONSTRAINT uq_initiator_event_name UNIQUE(initiator_id, title)
);

CREATE TABLE IF NOT EXISTS compilations (
      compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
      title VARCHAR(255),
      pinned BOOLEAN,
      CONSTRAINT pk_compilations PRIMARY KEY (compilation_id),
      CONSTRAINT uq_compilation_title UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS compilations_events (
    compilation_id BIGINT NOT NULL,
    event_id       BIGINT NOT NULL,
    CONSTRAINT pk_compilations_events PRIMARY KEY (compilation_id, event_id),
    CONSTRAINT fk_compilations_events_compilation_id FOREIGN KEY (compilation_id)
        REFERENCES compilations (compilation_id),
    CONSTRAINT compilations_events_event_id_fk FOREIGN KEY (event_id)
        REFERENCES events (event_id)
);

CREATE TABLE IF NOT EXISTS requests (
    request_id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id     BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE,
    status       VARCHAR(50),
    CONSTRAINT fk_requests_requester_id FOREIGN KEY (requester_id) REFERENCES users (user_id),
    CONSTRAINT fk_requests_event_id FOREIGN KEY (event_id) REFERENCES events (event_id)
);

CREATE TABLE IF NOT EXISTS comments (
    comment_id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    comment        VARCHAR(7000),
    event_id       BIGINT NOT NULL,
    commentator_id BIGINT NOT NULL,
    created        TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_comments_commentator_id FOREIGN KEY (commentator_id) REFERENCES users (user_id),
    CONSTRAINT fk_comments_event_id FOREIGN KEY (event_id) REFERENCES events (event_id)
);