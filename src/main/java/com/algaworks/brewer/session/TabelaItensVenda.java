package com.algaworks.brewer.session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
 */
@SessionScope
@Component
public class TabelaItensVenda {

	private List<ItemVenda> itens = new ArrayList<>();

	public BigDecimal getValorTotal() {
		/* Itera todos os itens da venda. */
		return itens.stream()
				/* Mapeia a lista, chamando o método getValorTotal de cada item. */
				.map(ItemVenda::getValorTotal)
				/*
				 * Reduz o valor total de todos os itens para o valor total da venda. Reduz
				 * como? Somando. Para somar dois BigDecimal, uso o método o add dele. Exemplo:
				 * Quero reduzir o array [10,20,30] aatravés da soma. O resultado é 60.
				 */
				.reduce(BigDecimal::add)
				/*
				 * Se não tiver nenhum item para reduzir, retorna 0. Posso fazer isso pois o
				 * reduce retorna um Optional<BigDecimal>.
				 */
				.orElse(BigDecimal.ZERO);
	}

	public void adicionarItem(Cerveja cerveja, Integer quantidade) {
		ItemVenda itemVenda = new ItemVenda();
		itemVenda.setCerveja(cerveja);
		itemVenda.setQuantidade(quantidade);
		itemVenda.setValorUnitario(cerveja.getValor());

		itens.add(itemVenda);
	}

	public int total() {
		return itens.size();
	}

    public List<ItemVenda> getItens() {
        return this.itens;
    }

}