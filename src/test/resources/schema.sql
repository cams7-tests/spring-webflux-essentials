CREATE TABLE tb_anime ( 
    id_anime IDENTITY NOT NULL PRIMARY KEY,    
    name VARCHAR NOT NULL UNIQUE,
    publication_year SMALLINT NOT NULL
);
CREATE TABLE tb_user ( 
    id_user IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL,
	username VARCHAR NOT NULL UNIQUE,
	password VARCHAR NOT NULL,
	authorities VARCHAR NOT NULL
);