package com.algaworks.brewer.dto;

import java.io.Serializable;

public class FotoDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String nome;

	private String contentType;

	public FotoDTO(String nome, String contentType) {
		this.nome = nome;
		this.contentType = contentType;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
