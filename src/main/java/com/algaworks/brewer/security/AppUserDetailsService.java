package com.algaworks.brewer.security;

import java.util.HashSet;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.Usuarios;

/** Implementa a pesquisa do usuário na hora de fazer o login. */
@Service
public class AppUserDetailsService implements UserDetailsService {

	@Autowired
	private Usuarios usuarios;

	/**
	 * Recebo por parametro o valor do campo input marcado como username lá na tela
	 * de login. No nosso caso, é o email.
	 */
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		Optional<Usuario> usuarioOptional = usuarios.porEmailEAtivo(email);

		Usuario usuario = usuarioOptional
				.orElseThrow(() -> new UsernameNotFoundException("Usuário e/ou senha incorretos."));

		/*
		 * Se encontrou usuário ativo, devolve um novo usuário passando o username, a
		 * senha e uma Collection de permissões.
		 */
		return new User(usuario.getEmail(), usuario.getSenha(), new HashSet<>());
	}

}
