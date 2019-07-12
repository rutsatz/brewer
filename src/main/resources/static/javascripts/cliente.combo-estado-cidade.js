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
	}

	ComboCidade.prototype.iniciar = function() {
		/*
		 * Esse "on" é o objeto que foi definido dentro do ComboEstado. Como
		 * recebo por parâmetro, posso escutar as mudanças dentro do
		 * ComboCidade.
		 */
		this.comboEstado.on('alterado', onEstadoAlterado.bind(this));
	}

	/* Sempre que o outro combo mudar de estado, atualiza a lista de cidades. */
	function onEstadoAlterado(evento, codigoEstado) {
//		var resposta = $.ajax({
//			url: 
//		});
	}

	return ComboCidade;

}());

$(function() {

	var comboEstado = new Brewer.ComboEstado();
	comboEstado.iniciar();

	var comboCidade = new Brewer.ComboCidade(comboEstado);
	comboCidade.iniciar();

});