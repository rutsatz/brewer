package com.algaworks.brewer.dto;

import java.io.Serializable;

/* Classe que é retornada para o javascript depois que a thread de salvar a foto
 * termina de salvar, usando o fotoStorage. */
public class FotoDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String nome;

	private String contentType;

	private String url;

	public FotoDTO(String nome, String contentType, String url) {
		this.nome = nome;
		this.contentType = contentType;
		this.url = url;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
