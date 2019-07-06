package com.algaworks.brewer.controller.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.algaworks.brewer.service.exception.NomeEstiloJaCadastradoException;

/*Fica monitorando os controllers*/
@ControllerAdvice
public class ControllerAdviceExceptionHandler {

	/*
	 * Se algum controller lançar essa exceção e ela não tiver sido tratada, esse
	 * método irá trata-lá
	 */
	@ExceptionHandler(NomeEstiloJaCadastradoException.class)
	public ResponseEntity<String> handleNomeEstiloJaCadastradoException(NomeEstiloJaCadastradoException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}

}
