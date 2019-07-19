Brewer = Brewer || {};

Brewer.MultiSelecao = (function() {
	
	function MultiSelecao() {
		/* Recupera os botões Ativar/Desativar */
		this.statusBtn = $('.js-status-btn');
		/* Recupera os checkbox dos usuários. */
		this.selecaoCheckbox = $('.js-selecao');
	}
	
	MultiSelecao.prototype.iniciar = function() {
		this.statusBtn.on('click', onStatusBtnClicado.bind(this));
	}
	
	function onStatusBtnClicado(event) {
		/* Assim eu sei qual foi o botão que foi clicado. */
		var botaoClicado = $(event.currentTarget);
		
		/* Pega o status do botão que foi clicado, pra saber se deve ativar ou desativar
		 * e passar isso para o controller. */
		var status = botaoClicado.data('status');
		
		/* Filtra somente pelos selecionados. */
		var checkBoxSelecionados = this.selecaoCheckbox.filter(':checked');
		/* Pega somente o código dos checkboxs selecionados.
		 * A função map executa a função para cada um dos checkboxs selecionados e pega o codigo
		 * de cada elemento e adiciona na lista de codigos de usuários. */
		var codigos = $.map(checkBoxSelecionados, function(c) {
			return $(c).data('codigo');
		});
		
		if (codigos.length > 0) {
			$.ajax({
				url: '/brewer/usuarios/status',
				method: 'PUT',
				data: {
					codigos: codigos,
					status: status
				}, 
				success: function() {
					window.location.reload();
				}
			});
			
		}
	}
	
	return MultiSelecao;
	
}());

$(function() {
	var multiSelecao = new Brewer.MultiSelecao();
	multiSelecao.iniciar();
});