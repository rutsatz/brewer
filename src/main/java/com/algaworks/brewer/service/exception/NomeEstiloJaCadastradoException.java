package com.algaworks.brewer.service.exception;

import java.io.Serializable;

public class NomeEstiloJaCadastradoException extends RuntimeException implements Serializable {

	private static final long serialVersionUID = 1L;

	public NomeEstiloJaCadastradoException(String message) {
		super(message);
	}
	
}
