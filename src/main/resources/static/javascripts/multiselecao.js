Brewer = Brewer || {};

Brewer.MultiSelecao = (function() {

	function MultiSelecao() {
		/* Recupera os botões Ativar/Desativar */
		this.statusBtn = $('.js-status-btn');
		/* Recupera os checkbox dos usuários. */
		this.selecaoCheckbox = $('.js-selecao');
		/* Pega o checkbox do cabeçalho, que seleciona todos os outros checkbox. */
		this.selecaoTodosCheckbox = $('.js-selecao-todos');
	}

	MultiSelecao.prototype.iniciar = function() {
		this.statusBtn.on('click', onStatusBtnClicado.bind(this));
		this.selecaoTodosCheckbox.on('click', onSelecaoTodosClicado.bind(this));
		/*
		 * Quando clicar em qualquer ums dos checkbox da lista, precisa tratar
		 * para marcar ou desmarcar o checkbox do cabeçalho.
		 */
		this.selecaoCheckbox.on('click', onSelecaoClicado.bind(this));
	}

	function onStatusBtnClicado(event) {
		/* Assim eu sei qual foi o botão que foi clicado. */
		var botaoClicado = $(event.currentTarget);
		/*
		 * Pega o status do botão que foi clicado, pra saber se deve ativar ou
		 * desativar e passar isso para o controller.
		 */
		var status = botaoClicado.data('status');
		/* Pega a url que foi colocada no botão, por causa do context path. */
		var url = botaoClicado.data('url');

		/* Filtra somente pelos selecionados. */
		var checkBoxSelecionados = this.selecaoCheckbox.filter(':checked');
		/*
		 * Pega somente o código dos checkboxs selecionados. A função map
		 * executa a função para cada um dos checkboxs selecionados e pega o
		 * codigo de cada elemento e adiciona na lista de codigos de usuários.
		 */
		var codigos = $.map(checkBoxSelecionados, function(c) {
			return $(c).data('codigo');
		});

		if (codigos.length > 0) {
			$.ajax({
				url : url,
				method : 'PUT',
				data : {
					codigos : codigos,
					status : status
				},
				success : function() {
					window.location.reload();
				}
			});

		}
	}

	function onSelecaoTodosClicado() {
		/* Pega o valor do checkbox, pra saber se está marcado ou não. */
		var status = this.selecaoTodosCheckbox.prop('checked');
		/*
		 * Coloca a propriedade checked para o valor do checkbox do cabecalho.
		 * Faz isso para todos os checkox, pois essa variavel tem todos eles.
		 */
		this.selecaoCheckbox.prop('checked', status);
		/*
		 * Habilita ou desabilita os botões, baseado no status do checkbox do
		 * cabecalho, que ativa ou desativa todos os outros.
		 */
		statusBotaoAcao.call(this, status);
	}

	function onSelecaoClicado() {
		/* Pega, da lista de checkbox, somente os que estão checkados. */
		var selecaoCheckboxChecados = this.selecaoCheckbox.filter(':checked');
		/*
		 * Se a quantidade de checkbox marcados na lista de checkbox for maior
		 * ou igual a quantidade total de checkbox na tela, marca, senão,
		 * desmarca o checkbox do cabeçalho.
		 */
		this.selecaoTodosCheckbox.prop('checked',
				selecaoCheckboxChecados.length >= this.selecaoCheckbox.length);
		/*
		 * Habilita ou desabilita os botões de Ativar ou Desativar os usuários,
		 * passando o length dos checkbox selecionados. Se não tiver nenhum
		 * selecionado, o valor será 0, que é a mesma coisa que false no
		 * javascript.
		 */
		statusBotaoAcao.call(this, selecaoCheckboxChecados.length);
	}

	/*
	 * Função para habilitar ou desabilitar os botões de Ativar e Desativar os
	 * usuários.
	 */
	function statusBotaoAcao(ativar) {
		ativar ? this.statusBtn.removeClass('disabled') : this.statusBtn
				.addClass('disabled');
	}

	return MultiSelecao;

}());

$(function() {
	var multiSelecao = new Brewer.MultiSelecao();
	multiSelecao.iniciar();
});