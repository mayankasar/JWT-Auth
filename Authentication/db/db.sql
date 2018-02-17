-- 1) Login Table

create sequence login_seq start 1 increment 1 NO MAXVALUE CACHE 1;
ALTER TABLE login_seq OWNER TO postgres;

create table login
(
    loginid integer not null DEFAULT nextval('login_seq'),
    username varchar(500) not null,
    password varchar(500) not null,
    userid varchar(500) not null,
    CONSTRAINT login_id PRIMARY KEY (loginid),
    CONSTRAINT login_username_unique UNIQUE (username),
    CONSTRAINT login_userid_unique UNIQUE (userid)
);

ALTER TABLE login OWNER TO postgres;

-- 2) Refresh Tokens
create sequence refreshtokens_seq start 1 increment 1 NO MAXVALUE CACHE 1;
ALTER TABLE refreshtokens_seq OWNER TO postgres;

create table refreshtokens
(
    refreshtokensid integer not null DEFAULT nextval('refreshtokens_seq'),
    token varchar(500) not null,
    issuedat timestamp DEFAULT now(),
    lastaccessed timestamp DEFAULT now(),
    userid varchar(500) not null,
    CONSTRAINT refreshtokens_id PRIMARY KEY (refreshtokensid),
    CONSTRAINT refreshtokens_token_unique UNIQUE (token)
);

ALTER TABLE refreshtokens OWNER TO postgres;
