create table estilo (
	codigo serial primary key,
	nome varchar(50) not null
);

create table cerveja (
	codigo serial primary key,
	sku varchar(50) not null,
	nome varchar(80) not null,
	descricao varchar(50) not null,
	valor decimal(10,2) not null,
	teor_alcoolico decimal(10,2) not null,
	comissao decimal(10,2) not null,
	sabor varchar(50) not null,
	origem varchar(50) not null,
	codigo_estilo bigint not null,
	foreign key (codigo_estilo) references estilo(codigo)
);

insert into estilo (codigo, nome) values (default, 'Amber Lager');
insert into estilo (codigo, nome) values (default, 'Dark Lager');
insert into estilo (codigo, nome) values (default, 'Pale Lager');
insert into estilo (codigo, nome) values (default, 'Pilsner');