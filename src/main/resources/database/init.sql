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