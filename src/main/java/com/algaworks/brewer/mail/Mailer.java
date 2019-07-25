package com.algaworks.brewer.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.algaworks.brewer.model.Venda;

/* Classe responsável por enviar os email. */
@Component
public class Mailer {

	@Autowired
	private JavaMailSender mailSender;

	/*
	 * Com o @Async, digo que a chamada ao método agora é assíncrona. Para isso,
	 * preciso adiconar o @EnableAsync lá no WebConfig para habilitar isso.
	 */
	@Async
	public void enviar(Venda venda) {

		SimpleMailMessage mensagem = new SimpleMailMessage();
		mensagem.setFrom("rutsatz@hotmail.com.br");
		mensagem.setTo(venda.getCliente().getEmail());
		mensagem.setSubject("Venda Efetuada");
		mensagem.setText("Obrigado por comprar comnosotros.");

		mailSender.send(mensagem);
	}

}
