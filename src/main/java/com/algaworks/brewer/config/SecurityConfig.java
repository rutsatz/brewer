package com.algaworks.brewer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("admin").password("admin").roles("CADASTRO_CLIENTE");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/*
		 * Eu quero autorizar requisições. Quais requisições? Qualquer uma eu preciso
		 * estar autenticado (Não preciso de uma permissão especial, como vendedor ou
		 * administrador, somente preciso ter logado com usuário e senha). O and() é pra
		 * eu poder continuar configurando. Ele volta para o objeto anterior.
		 */
		http.authorizeRequests().anyRequest().authenticated().and().formLogin().and().csrf().disable();
	}

	/* Configura o gerador de senhas. Usado para criptografar a senha do usuário. */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
