package com.algaworks.brewer.model.validation;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import com.algaworks.brewer.model.Cliente;

/**
 * O Sequence somente passa para o próximo grupo depois que o grupo anterior
 * tiver passado na validação.
 */
public class ClienteGroupSequenceProvider implements DefaultGroupSequenceProvider<Cliente> {

	@Override
	public List<Class<?>> getValidationGroups(Cliente cliente) {

		List<Class<?>> grupos = new ArrayList<>();
		/*
		 * Adiciono a própria classe para dizer pra validar os atributos que não possuem
		 * nenhum grupo de validação, ou seja, não tem o groups informado na anottation.
		 * Quando terminar de validar esses caras, passa para o próximo grupo da lista.
		 */
		grupos.add(Cliente.class);

		if (isPessoaSelecionada(cliente)) {
			/*
			 * O grupo foi adicionado no Enum pra não precisar fazer vários if para saber o
			 * tipo de pessoa.
			 * 
			 * Vai adicionar o grupo do CPF ou do CNPJ.
			 */
			grupos.add(cliente.getTipoPessoa().getGrupo());
		}

		return grupos;
	}

	/** Se o usuário selecionou algum tipo de pessoa. */
	private boolean isPessoaSelecionada(Cliente cliente) {
		return cliente != null && cliente.getTipoPessoa() != null;
	}

}
