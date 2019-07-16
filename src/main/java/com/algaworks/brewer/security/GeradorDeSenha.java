package com.algaworks.brewer.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Gerador de senha temporário, para permitir gerar a senha do usuário padrão
 * Admin e colocar na migração.
 */
public class GeradorDeSenha {

	public static void main(String[] args) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println(encoder.encode("admin"));

	}
}
