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
		this.uuid = $('#uuid').val();
		this.emitter = $({});
		this.on = this.emitter.on.bind(this.emitter);
	}

	TabelaItens.prototype.iniciar = function() {
		/**
		 * Registra a função para ficar escutando o evento item-selecionado lá
		 * do autocomplete.
		 */
		this.autocomplete.on('item-selecionado', onItemSelecionado.bind(this));
		
		bindQuantidade.call(this);
		bindTabelaItem.call(this);
	}

	/* Coloca no prototype para deixar acessível de fora. Ai, como o thymeleaf coloca esse item
	 * que vem lá do server, aí assim consigo acessar lá do venda.js. */
	TabelaItens.prototype.valorTotal = function() {
		return this.tabelaCervejasContainer.data('valor');
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
				codigoCerveja : item.codigo,
				uuid: this.uuid
			}
		});

		/** O servidor retorna um html para a página, com os itens. */
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}

	/** Recebe o html do servidor, que contém a lista de itens renderizada. */
	function onItemAtualizadoNoServidor(html) {
		/** Seta o html da lista renderizada no container de cervejas. */
		this.tabelaCervejasContainer.html(html);

		/** As inicializações foram movidas para esse método, pois quando a página for carregada,
		 * ele também precisa faze-las, pois pode ser que eu tenha enviado para o server e tenha
		 * dado algum erro de validação. Então preciso fazer o bind novamente. */
		bindQuantidade.call(this);
		
		var tabelaItem = bindTabelaItem.call(this);
		
		/* Ao atualizar os itens, notifica a venda com o novo valor total. Recupera o valor total através de um data,
		 * que foi atribuido lá pelo servidor. */
		this.emitter.trigger('tabela-itens-atualizada', tabelaItem.data('valor-total'));
	}

	/* Recebo o evento para pegar qual o input que sofreu alteração. */
	function onQuantidadeItemAlterado(evento) {
		/* Pego o input que foi alterado. */
		var input = $(evento.target);
		var quantidade = input.val();
		
		if (quantidade <= 0) {
			input.val(1);
			quantidade = 1;
		}
		
		var codigoCerveja = input.data('codigo-cerveja');

		var resposta = $.ajax({
			url : 'item/' + codigoCerveja,
			method : 'PUT',
			data : {
				quantidade : quantidade,
				uuid: this.uuid
			}
		});

		/*
		 * Essa nossa requisição para alterar a quantidade retorna o html
		 * renderizado da lista de itens, então chamo a função que já existe e
		 * que já trata isso.
		 */
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}

	function onDoubleClick(evento) {
		/**
		 * O target é o componente que eu cliquei. Se eu clicar na imagem, o
		 * targer vai ser o <img>. Por isso eu não posso usar o
		 * $(evento.target). Eu preciso pegar a div externa, que é o container.
		 * Então eu pego o elemento que escutou o evento, pois é ela que tem o
		 * evento de double click. Então eu pego o currentTarget, que é quem
		 * escutou o evento.
		 */
		// var item = $(evento.currentTarget);
		// item.toggleClass('solicitando-exclusao');
		/*
		 * Eu posso fazer um atalho para o currentTarget. Quem escutou o evento
		 * foi o this, pois o this é o currentTarget. Dessa forma, posso fazer
		 * direto.
		 * 
		 * Ai adicionamos essa classe marcadora para saber qual item vai ser
		 * excluido e exibir a mensagem de confirmação.
		 */
		$(this).toggleClass('solicitando-exclusao');

	}

	function onExclusaoItemClick(evento) {
		var codigoCerveja = $(evento.target).data('codigo-cerveja');
		var resposta = $.ajax({
			url : 'item/' + this.uuid + '/' + codigoCerveja,
			method : 'DELETE'
		});

		/*
		 * Essa nossa requisição para alterar a quantidade retorna o html
		 * renderizado da lista de itens, então chamo a função que já existe e
		 * que já trata isso.
		 */
		resposta.done(onItemAtualizadoNoServidor.bind(this));
	}

	/* Preciso fazer o bind toda vez que o servidor for atualizado via javascript ou quando a tela
	 * for carregada.  */
	function bindQuantidade() {
		/**
		 * Adiciona aqui, após receber o html dos itens do servidor, pois é
		 * somente depois de receber esse html que eu tenho acesso ao input da
		 * quantidade de itens.
		 */
		var quantidadeItemInput = $('.js-tabela-cerveja-quantidade-item');
		quantidadeItemInput.on('change', onQuantidadeItemAlterado.bind(this));
		/* Não deixa informar letras na quantidade. */
		quantidadeItemInput.maskMoney({ precision: 0, thousands: '' });
	}
	
	function bindTabelaItem() {
		/**
		 * Pego a div container de cada item e adiciono um evento de double
		 * click para mostrar a opção de excluir o item.
		 */
		var tabelaItem = $('.js-tabela-item');
		tabelaItem.on('dblclick', onDoubleClick);

		/** Botão de confirmar a exclusão do item. */
		$('.js-exclusao-item-btn').on('click', onExclusaoItemClick.bind(this));
		
		return tabelaItem;
	}
	
	return TabelaItens;

}());

