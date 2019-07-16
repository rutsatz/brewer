-- Insere as permissões.
INSERT INTO permissao VALUES (1, 'ROLE_CADASTRAR_CIDADE');
INSERT INTO permissao VALUES (2, 'ROLE_CADASTRAR_USUARIO');

-- Relaciona as permissões a um grupo.
INSERT INTO grupo_permissao (codigo_grupo, codigo_permissao) VALUES (1, 1);
INSERT INTO grupo_permissao (codigo_grupo, codigo_permissao) VALUES (1, 2);

-- Adiciona o grupo ao usuário admin. Assim, ao migrar o sistema, já tem um usuário admin com permissões.
INSERT INTO usuario_grupo (codigo_usuario, codigo_grupo) VALUES (
	(SELECT codigo FROM usuario WHERE email = 'admin@brewer.com'), 1);