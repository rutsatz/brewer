package com.algaworks.brewer.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SegurancaController {

	/**
	 * Com o @AuthenticationPrincipal posso receber, em qualquer controller, o
	 * usuário que está logado.
	 */
	@GetMapping("/login")
	public String login(@AuthenticationPrincipal User user) {
		/*
		 * Se eu tenho um usuário logado, então mando pra página principal. Se já estou
		 * logado, não posso ver a página de login novamente.
		 */
		if (user != null) {
			return "redirect:/cervejas";
		}
		return "Login";
	}

	/** Retorna a página de erro, qque configuramos para o SprinSecurity retornar quando ele tiver uma exception de acesso
	 * negado. */
	@GetMapping
	public String acessoNegado() {
	    return "403";
	}

}
