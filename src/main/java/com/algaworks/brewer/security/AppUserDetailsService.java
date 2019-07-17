package com.algaworks.brewer.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
//		return new User(usuario.getEmail(), usuario.getSenha(), getPermissoes(usuario));
		/*
		 * Retorno o meu objeto, que estende do User do Spring, mas que adicionei dados
		 * extras para exibir na tela.
		 */
		return new UsuarioSistema(usuario, getPermissoes(usuario));
	}

	private Collection<? extends GrantedAuthority> getPermissoes(Usuario usuario) {
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();

		// Busca a lista de permissões.
		List<String> permissoes = usuarios.permissoes(usuario);
		permissoes.forEach(p -> authorities.add(new SimpleGrantedAuthority(p.toUpperCase())));

		return authorities;
	}

}
