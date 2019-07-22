/**
 * Aqui não faço o Brewer = Brewer || {}, pois como a tabela de itens depende do
 * item selecionado, quando eu selecionar o item e notificar a tabela, ela já
 * precisa estar inicializada.
 */
Brewer.TabelaItens = (function() {

	/** Como dependo do autocomplete, recebo ele por parâmetro e deixo salvo. */
	function TabelaItens(autocomplete) {
		this.autocomplete = autocomplete;
		this.tabelaCervejasContainer = $('.js-tabela-cervejas-container');
	}

	TabelaItens.prototype.iniciar = function() {
		/**
		 * Registra a função para ficar escutando o evento item-selecionado lá
		 * do autocomplete.
		 */
		this.autocomplete.on('item-selecionado', onItemSelecionado.bind(this));
	}

	/** Recebo do evento capturado o nome do evento e o item. */
	function onItemSelecionado(evento, item) {
		var resposta = $.ajax({
			/**
			 * Essa é uma outra maneira de definiar a url. Assim, ele pega a
			 * última string da url, e substitui pela string que defini. Por
			 * exemplo: Se estou na página /brewer/venda/nova, ele vai trocar o
			 * nova pelo item, ficando /brewer/venda/item.
			 */
			url : 'item',
			method : 'POST',
			data : {
				codigoCerveja : item.codigo
			}
		});

		/** O servidor retorna um html para a página, com os itens. */
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}

	/** Recebe o html do servidor, que contém a lista de itens renderizada. */
	function onItemAtualizadoNoServidor(html) {
		/** Seta o html da lista renderizada no container de cervejas. */
		this.tabelaCervejasContainer.html(html);
		
		/** Adiciona aqui, após receber o html dos itens do servidor, pois é somente depois de receber esse html
		 * que eu tenho acesso ao input da quantidade de itens. */
		$('.js-tabela-cerveja-quantidade-item').on('change', onQuantidadeItemAlterado.bind(this));
	}
	
	/* Recebo o evento para pegar qual o input que sofreu alteração. */
	function onQuantidadeItemAlterado(evento) {
		/* Pego o input que foi alterado. */
		var input = $(evento.target);
		var quantidade = input.val();
		var codigoCerveja = input.data('codigo-cerveja');
		
		var resposta = $.ajax({
			url: 'item/' + codigoCerveja,
			method: 'PUT',
			data: {
				quantidade: quantidade
			}
		});
		
		/* Essa nossa requisição para alterar a quantidade retorna o html renderizado da lista de itens,
		 * então chamo a função que já existe e que já trata isso. */
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}
	
	return TabelaItens;

}());

$(function() {
	/**
	 * Inicia o autocomplete aqui pois preciso passa-lo como dependencia para a
	 * tabela de itens.
	 */
	var autocomplete = new Brewer.Autocomplete();
	autocomplete.iniciar();

	var tabelaItens = new Brewer.TabelaItens(autocomplete);
	tabelaItens.iniciar();

});
