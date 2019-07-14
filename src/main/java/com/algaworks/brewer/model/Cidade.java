package com.algaworks.brewer.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "cidade")
public class Cidade implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;

	@NotBlank(message = "Nome é obrigatório")
	private String nome;

	/*
	 * Adicionado o JsonIgnore pois quando é feito a requisição Ajax de seleção de
	 * estado, ele busca as cidades. Aí o Jackson quando vai serializar para Json,
	 * ele serializa o objeto inteiro, mandando sempre o estado repetido para cada
	 * cidade lá para a tela. Para evitar esse envio desnecessário para a tela, é
	 * colocado o JsonIgnore. E para evitar uma consulta extra na tabela estado toda
	 * vez que buscar a cidade, é adicionado o Lazy, pois no ManyToOne o default é
	 * EAGER.
	 * 
	 * Se colocar o LAZY sem o JsonIgnore vai dar erro, pois o Jackson não vai
	 * conseguir serializar o objeto estado. Tem que analisar cada caso com atenção.
	 * 
	 * Se colocar o JsonIgnore sem o Lazy, não vai mandar pra tela, mas a JPA vai
	 * ficar fazendo a consulta mesmo assim.
	 */
	@NotNull(message = "O estado é obrigatório")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo_estado")
	@JsonIgnore
	private Estado estado;

	public Cidade() {
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public boolean temEstado() {
		return estado != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cidade other = (Cidade) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

}
