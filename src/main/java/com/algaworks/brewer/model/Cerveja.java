package com.algaworks.brewer.model;

import javax.validation.constraints.NotBlank;

public class Cerveja {

	@NotBlank
	private String sku;

	private String nome;

	private String descricao;
	
	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
