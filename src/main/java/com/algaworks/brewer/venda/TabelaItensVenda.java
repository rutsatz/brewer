package com.algaworks.brewer.venda;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;

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

}