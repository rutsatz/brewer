package com.algaworks.brewer.mail;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/* Classe responsável por enviar os email. */
@Component
public class Mailer {

	/*
	 * Com o @Async, digo que a chamada ao método agora é assíncrona. Para isso,
	 * preciso adiconar o @EnableAsync lá no WebConfig para habilitar isso.
	 */
	@Async
	public void enviar() {

	}

}
