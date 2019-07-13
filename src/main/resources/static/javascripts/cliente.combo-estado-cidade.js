var Brewer = Brewer || {};

/* Tudo relacionado ao combo de estado. */
Brewer.ComboEstado = (function() {

	function ComboEstado() {
		this.combo = $('#estado');
		/* Objeto para lançar eventos. */
		this.emitter = $({});
		/*
		 * Esse objeto on é usado no combo cidade para adicionar um listener e
		 * ficar escutando as mudanças.
		 */
		this.on = this.emitter.on.bind(this.emitter);
	}

	ComboEstado.prototype.iniciar = function() {
		this.combo.on('change', onEstadoAlterado.bind(this));
	}

	function onEstadoAlterado() {
		/*
		 * Emite o evento alterado, que será lançado sempre que o valor mudar no
		 * combo de estado.
		 */
		this.emitter.trigger('alterado', this.combo.val());
	}

	return ComboEstado;

}());

/* Tudo relacionado ao combo de cidade. */
Brewer.ComboCidade = (function() {

	/* O combo da cidade depende do combo do estado. */
	function ComboCidade(comboEstado) {
		this.comboEstado = comboEstado;
		this.combo = $('#cidade');
		this.imgLoading = $('.js-img-loading');
		this.inputHiddenCidadeSelecionada = $('#inputHiddenCidadeSelecionada');
	}

	ComboCidade.prototype.iniciar = function() {
		reset.call(this);
		/*
		 * Esse "on" é o objeto que foi definido dentro do ComboEstado. Como
		 * recebo por parâmetro, posso escutar as mudanças dentro do
		 * ComboCidade.
		 */
		this.comboEstado.on('alterado', onEstadoAlterado.bind(this));

		/* Pegar o valor lá do outro combo, o combo do estado. */
		var codigoEstado = this.comboEstado.combo.val();

		inicializarCidades.call(this, codigoEstado);

	}

	/* Sempre que o outro combo mudar de estado, atualiza a lista de cidades. */
	function onEstadoAlterado(evento, codigoEstado) {
		this.inputHiddenCidadeSelecionada.val('');
		inicializarCidades.call(this, codigoEstado);
	}

	function inicializarCidades(codigoEstado) {
		/*
		 * Se já tiver o código do estado, chamo o método do emmiter
		 * manualmente.
		 */
		if (codigoEstado) {
			var resposta = $.ajax({
				url : this.combo.data('url'),
				method : 'GET',
				contentType : 'application/json',
				data : {
					'estado' : codigoEstado
				},
				/* Antes de começar a requisição. */
				beforeSend : iniciarRequisicao.bind(this),
				/* Quando a requisição estiver completa. */
				complete : finalizarRequisicao.bind(this)
			});

			/* Chama a função quando tiver completado a requisição. */
			resposta.done(onBuscarCidadesFinalizado.bind(this));

		} else {
			reset.call(this);
		}

	}

	/* Recebe por parâmetro o que foi retornado lá da função ajax. */
	function onBuscarCidadesFinalizado(cidades) {
		var options = [];
		cidades.forEach(function(cidade) {
			/* Adiciona no array, separando por vírgula. */
			options.push('<option value="' + cidade.codigo + '">' + cidade.nome
					+ '</option>');
		});
		/* Junta os elementos do array, separando por nada. */
		this.combo.html(options.join(''));
		this.combo.removeAttr('disabled');

		/*
		 * Busca o código que foi colocado no campo escondido, para poder
		 * selecionar a cidade correta.
		 */
		var codigoCidadeSelecionada = this.inputHiddenCidadeSelecionada.val();
		if (codigoCidadeSelecionada) {
			/*
			 * Seleciona a cidade anterior, que foi submetida ao server mas que
			 * retornou por algum motivo, como algum erro de validação nos
			 * campos.
			 */
			this.combo.val(codigoCidadeSelecionada);
		}
	}

	function reset() {
		this.combo.html('<option value="">Selecione a cidade</option>');
		this.combo.val('');
		this.combo.attr('disabled', 'disabled');
	}

	function iniciarRequisicao() {
		reset.call(this);
		this.imgLoading.show();
	}

	function finalizarRequisicao() {
		this.imgLoading.hide();
	}

	return ComboCidade;

}());

$(function() {

	var comboEstado = new Brewer.ComboEstado();
	comboEstado.iniciar();

	var comboCidade = new Brewer.ComboCidade(comboEstado);
	comboCidade.iniciar();

});