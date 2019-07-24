Brewer.Venda = (function() {

	function Venda(tabelaItens) {
		this.tabelaItens = tabelaItens;
		/* Box que contém o valor total da venda. */
		this.valorTotalBox = $('.js-valor-total-box');
		this.valorFreteInput = $('#valorFrete');
		this.valorDescontoInput = $('#valorDesconto');

		this.valorTotalBoxContainer = $('.js-valor-total-box-container');

		/* Variáveis da tela. */
		this.valorTotalItens = this.tabelaItens.valorTotal();
		this.valorFrete = this.valorFreteInput.data('valor');
		this.valorDesconto = this.valorDescontoInput.data('valor');
	}

	Venda.prototype.iniciar = function() {
		/*
		 * Adiciona o listener para quando mudar um item na tabela de vendas,
		 * podermos recuperar o valor total.
		 */
		this.tabelaItens.on('tabela-itens-atualizada', onTabelaItensAtualizada
				.bind(this));
		/*
		 * Coloca um evento ao terminar de digitar no input do frete, para ir
		 * atualizando o valor do frete.
		 */
		this.valorFreteInput.on('keyup', onValorFreteAlterado.bind(this));
		this.valorDescontoInput.on('keyup', onValorDescontoAlterado.bind(this));

		/*
		 * Adiciona um listener nesses mesmos eventos, para também chamar o
		 * método para atualizar os valores na tela, quando esse evento ocorrer.
		 */
		this.tabelaItens.on('tabela-itens-atualizada', onValoresAlterados
				.bind(this));
		this.valorFreteInput.on('keyup', onValoresAlterados.bind(this));
		this.valorDescontoInput.on('keyup', onValoresAlterados.bind(this));


		/* Quando carregar, já efetua os calculos, caso as validações não tenham passado lá no server. */
		onValoresAlterados.call(this);
	}

	/* valorTotalItens é recebido lá do servidor. */
	function onTabelaItensAtualizada(evento, valorTotalItens) {
		/*
		 * Quando removo todos os itens, ele vem undefined. Posso testar com o
		 * null, que ele também verifica o undefined.
		 */
		this.valorTotalItens = valorTotalItens == null ? 0 : valorTotalItens;

	}

	function onValorFreteAlterado(evento) {
		this.valorFrete = Brewer.recuperarValor($(evento.target).val());
	}

	function onValorDescontoAlterado(evento) {
		this.valorDesconto = Brewer.recuperarValor($(evento.target).val());
	}

	function onValoresAlterados() {
		var valorTotal = numeral(this.valorTotalItens) + numeral(this.valorFrete) - numeral(this.valorDesconto);
		/* Atualiza o valor total na tela. */
		this.valorTotalBox.html(Brewer.formatarMoeda(valorTotal));
		
		this.valorTotalBoxContainer.toggleClass('negativo', valorTotal < 0);
	}

	return Venda;

}());

$(function() {
	/**
	 * Inicia o autocomplete aqui pois preciso passa-lo como dependencia para a
	 * tabela de itens.
	 */
	var autocomplete = new Brewer.Autocomplete();
	autocomplete.iniciar();

	/**
	 * É inicialiado aqui, pois a venda depende da tabela de itens. A venda vai
	 * ficar escutando alterações na tabela de itens.
	 */
	var tabelaItens = new Brewer.TabelaItens(autocomplete);
	tabelaItens.iniciar();

	var venda = new Brewer.Venda(tabelaItens);
	venda.iniciar();

});