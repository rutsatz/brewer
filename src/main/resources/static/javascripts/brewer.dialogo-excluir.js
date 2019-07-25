Brewer = Brewer || {};

Brewer.DialogoExcluir = (function() {

	function DialogoExcluir() {
		this.exclusaoBtn = $('.js-exclusao-btn');

	}

	DialogoExcluir.prototype.iniciar = function() {
		this.exclusaoBtn.on('click', onExcluirClicado.bind(this));

		/**
		 * Verifica se tem a String excluido nos parametros. O search são as
		 * querys strings da url.
		 */
		if (window.location.search.indexOf('excluido') > -1) {
			/**
			 * Tem o parâmetro excluido. Mostra o título, a mensagem no corpo e
			 * o símbolo de sucesso.
			 */
			swal('Pronto!', 'Excluído com sucesso!', 'success');
		}
	}

	function onExcluirClicado(evento) {
		evento.preventDefault();
		var botaoClicado = $(evento.currentTarget);
		var url = botaoClicado.data('url');
		var objeto = botaoClicado.data('objeto');

		/**
		 * No primeiro paramêtro são as opções da interface. No segundo, a
		 * função que será executada quando o usuário confirmar.
		 */
		swal({
			title : 'Tem certeza?',
			text : 'Excluir "' + objeto
					+ '"? Você não poderá recuperar depois.',
			showCancelButton : true,
			confirmButtonColor : '#DD6B55',
			confirmButtonText : 'Sim, exclua agora!',
			closeOnConfirm : false
		}, onExcluirConfirmado.bind(this, url));
	}

	/** Posso receber parâmetros tbm, quando chamo com o bind. */
	function onExcluirConfirmado(url) {
		$.ajax({
			url : url,
			method : 'DELETE',
			success : onExcluidoSucesso.bind(this),
			error : onErroExcluir.bind(this)
		});
	}

	function onExcluidoSucesso() {
		// window.location.reload();

		/** Pega a url atual da página. */
		var urlAtual = window.location.href;
		/**
		 * Tratamento para ver se já tem o ? ou não. Se tiver, adiciona um &,
		 * senão, concatena o ?.
		 */
		var separador = urlAtual.indexOf('?') > -1 ? '&' : '?';
		/**
		 * Tratamento para várias exclusões em sequência. Se já tiver o
		 * excluido, eu somente mantenho a utl atual. Se ainda não tiver, eu
		 * concateno a url atual, mais o separador e adiciono o excluido.
		 */
		var novaUrl = urlAtual.indexOf('excluido') > -1 ? urlAtual : urlAtual
				+ separador + 'excluido';

		/* Manda para a nova url. */
		window.location = novaUrl;

	}

	function onErroExcluir(e) {
		/**
		 * Parâmetros: 1. Título. 2. String de erro do objeto do javascript, que
		 * é enviado lá pelo servidor. 3. Indica que deve mostrar o ícone de
		 * erro.
		 */
		swal('Oops!', e.responseText, 'error')
	}

	return DialogoExcluir;

}());

$(function() {

	var dialogo = new Brewer.DialogoExcluir();
	dialogo.iniciar();

});
