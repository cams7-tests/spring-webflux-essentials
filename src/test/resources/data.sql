INSERT INTO tb_anime(name, publication_year) VALUES('Naruto', 1999);
INSERT INTO tb_anime(name, publication_year) VALUES('One Piece', 1997);
INSERT INTO tb_anime(name, publication_year) VALUES('Sword Art Online', 2009);
INSERT INTO tb_anime(name, publication_year) VALUES('Neon Genesis Evangelion', 1994);
INSERT INTO tb_anime(name, publication_year) VALUES('Aggretsuko', 2020);
INSERT INTO tb_anime(name, publication_year) VALUES('Fate/Apocrypha', 2012);
INSERT INTO tb_anime(name, publication_year) VALUES('Fate/Extra Last Encore', 2018);
INSERT INTO tb_anime(name, publication_year) VALUES('Bayonetta: Destino Sangrento', 2013);
INSERT INTO tb_anime(name, publication_year) VALUES('Madoka Magica', 2011);
INSERT INTO tb_anime(name, publication_year) VALUES('Castlevania', 2010);
INSERT INTO tb_anime(name, publication_year) VALUES('Record of Ragnarok', 2018);
INSERT INTO tb_anime(name, publication_year) VALUES('Kengan Ashura', 2012);
INSERT INTO tb_anime(name, publication_year) VALUES('The Daily Life of the Immortal King', 2017);
INSERT INTO tb_anime(name, publication_year) VALUES('Hunter X Hunter: A Última Missão', 2013);
INSERT INTO tb_anime(name, publication_year) VALUES('Kakegurui', 2014);
INSERT INTO tb_anime(name, publication_year) VALUES('Demon Slayer: Kimetsu no Yaiba', 2016);
INSERT INTO tb_anime(name, publication_year) VALUES('Children of the Whales', 2013);
--Password: abc12345
INSERT INTO tb_user(name, username, password, authorities) VALUES('Some User', 'user', '{bcrypt}$2a$10$8RRftD6fhCnp0Psyl.8VGeRb6wEOuUX8bqsVqnWzTZXw2x.C0cepO', 'ROLE_USER');
INSERT INTO tb_user(name, username, password, authorities) VALUES('Administrator', 'admin', '{bcrypt}$2a$10$NOcV6G.vaf5cPc7ZLSoSfO495Z/mWUibm11hpFkS6lr2fP48.e1.u', 'ROLE_USER,ROLE_ADMIN');