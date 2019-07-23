package com.algaworks.brewer.session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;

/**
 * Removido o public, pois ela somente pode ser acessada através da sessão do
 * usuário.
 */
class TabelaItensVenda {

	/*
	 * Identifica para a sessão do usuário, qual tela está aberta, caso tenha mais
	 * de uma aba aberta.
	 */
	private String uuid;

	private List<ItemVenda> itens = new ArrayList<>();

	/** Somente consigo criar se passar o id. */
	public TabelaItensVenda(String uuid) {
		this.uuid = uuid;
	}

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

		Optional<ItemVenda> itemVendaOptional = buscarItemPorCerveja(cerveja);

		ItemVenda itemVenda = null;
		/* Assim não preciso ficar verificando se é != null e talz. */
		if (itemVendaOptional.isPresent()) {
			itemVenda = itemVendaOptional.get();
			itemVenda.setQuantidade(itemVenda.getQuantidade() + quantidade);
		} else {
			itemVenda = new ItemVenda();
			itemVenda.setCerveja(cerveja);
			itemVenda.setQuantidade(quantidade);
			itemVenda.setValorUnitario(cerveja.getValor());

			/*
			 * Adiciono no inicio da lista, para a última cerveja sempre ficar no topo da
			 * minha lista lá na tela.
			 */
			itens.add(0, itemVenda);
		}

	}

	public void alterarQuantidadeItens(Cerveja cerveja, Integer quantidade) {
		/*
		 * Como estou editando a quantidade, então é pq a cerveja já existe. Não preciso
		 * fazer os if lá do Optional.
		 */
		ItemVenda itemVenda = buscarItemPorCerveja(cerveja).get();
		itemVenda.setQuantidade(quantidade);

	}

	public void excluirItem(Cerveja cerveja) {
		/*
		 * Preciso remover o item do carrinho de compras. Mas a lista do carrinho é de
		 * ItemVenda e não de Cerveja. Por isso, preciso descobrir qual o indice da
		 * lista de ItemVenda em que está essa Cerveja. Para isso, uso o IntStream para
		 * gerar um range de valores e procurar na lista. Se minha lista tiver 2 itens,
		 * ele gera um range de 0 a 1.
		 */
		int indice = IntStream.range(0, itens.size())
				/*
				 * Ai filtro da lista qual ItemVenda que tem a mesma cerveja que deve ser
				 * excluída.
				 */
				.filter(i -> itens.get(i).getCerveja().equals(cerveja))
				/*
				 * Como somente temos uma cerveja com o mesmo código para a venda (incrementamos
				 * a quantidade), podemos chamar o findAny() e o getAsInt() para pegar o valor
				 * do índice.
				 */
				.findAny().getAsInt();

		/* Remove o ItemVenda do carrinho. */
		itens.remove(indice);
	}

	public int total() {
		return itens.size();
	}

	public List<ItemVenda> getItens() {
		return this.itens;
	}

	private Optional<ItemVenda> buscarItemPorCerveja(Cerveja cerveja) {
		/* Percorro o meu carrinho de compras. */
		return itens.stream()
				/*
				 * Filtro para ver se o item que estou adicionando já existe no carrinho. Ele
				 * retorna um stream somente com as cervejas filtradas, ou seja, que já existem.
				 */
				.filter(i -> i.getCerveja().equals(cerveja))
				/*
				 * Uso o findAny somente para saber se já existe a cerveja ou não. Ele me
				 * retorna um Optional.
				 */
				.findAny();
	}

	public String getUuid() {
		return uuid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
		TabelaItensVenda other = (TabelaItensVenda) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

}