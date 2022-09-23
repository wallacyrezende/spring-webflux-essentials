create database anime;

create schema anime;

create table anime.anime(
	id serial not null,
	name varchar
);

create unique index anime_id_uindex on anime.anime (id);

alter table anime.anime add constraint anime_pk primary key (id);

create table anime.user_login(
	id serial not null,
	name varchar(255) not null,
	username varchar(100) not null,
	password varchar(150) not null,
	authorities varchar(150) not null
);

insert into anime.user_login (name, username, password, authorities) values('wallacy', 'wall', '{bcrypt}$2a$10$FToxnhlpn58TXi55Atms.uBdLGWn4SvxzGd5GhceFUyBRp.39Mw4W', 'ROLE_ADMIN,ROLE_USER');
insert into anime.user_login (name, username, password, authorities) values('theo', 'theo', '{bcrypt}$2a$10$FToxnhlpn58TXi55Atms.uBdLGWn4SvxzGd5GhceFUyBRp.39Mw4W', 'ROLE_USER');