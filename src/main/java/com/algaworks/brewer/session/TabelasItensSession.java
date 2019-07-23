package com.algaworks.brewer.session;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;

/**
 * O escopo default dos beans do Spring é escopo da aplicação. Porém, para os
 * itens da venda, eu quero o escopo da sessão do usuário. Se eu deixar como
 * escopo de aplicação, vai existir somente um item dessa classe para todos os
 * usuários, e a lista de vendas vai ser para todo mundo. Com o escopo de
 * sessão, crio um objeto para cada usuário.
 *
 * E essa classe foi criada para além de ter esse objeto para cada usuário,
 * podermos separar em telas diferentes, para os casos em que o usuário abre
 * mais de uma aba no browser.
 */
@SessionScope
@Component
public class TabelasItensSession {

	/* É usado o Set pois ele não deixa ter repetições. */
	private Set<TabelaItensVenda> tabelas = new HashSet<>();

	public void adicionarItem(String uuid, Cerveja cerveja, int quantidade) {
		TabelaItensVenda tabela = buscarTabelaPorUuid(uuid);
		tabela.adicionarItem(cerveja, quantidade);
		tabelas.add(tabela);
	}

	public void alterarQuantidadeItens(String uuid, Cerveja cerveja, Integer quantidade) {
		TabelaItensVenda tabela = buscarTabelaPorUuid(uuid);
		tabela.alterarQuantidadeItens(cerveja, quantidade);
	}

	public void excluirItem(String uuid, Cerveja cerveja) {
		TabelaItensVenda tabela = buscarTabelaPorUuid(uuid);
		tabela.excluirItem(cerveja);
	}

	public List<ItemVenda> getItens(String uuid) {
		return buscarTabelaPorUuid(uuid).getItens();
	}

    public Object getValorTotal(String uuid) {
        return buscarTabelaPorUuid(uuid).getValorTotal();
    }

	
	private TabelaItensVenda buscarTabelaPorUuid(String uuid) {
		/* Percore a lista de tabelas de itens da sessão do usuário. */
		TabelaItensVenda tabela = tabelas.stream()
				/* Filtra somente pelas listas da tela que enviou a requisição. */
				.filter(t -> t.getUuid().equals(uuid))
				/* Se já existir uma lista para essa tela, retorna ela. */
				.findAny()
				/* Se ainda não existir, cria uma nova para essa tela. */
				.orElse(new TabelaItensVenda(uuid));
		return tabela;
	}

}
