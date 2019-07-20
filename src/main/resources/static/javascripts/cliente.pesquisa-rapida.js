Brewer = Brewer || {};

Brewer.PesquisaRapidaCliente = (function() {

	function PesquisaRapidaCliente() {
		/* Busca o modal pelo id. */
		this.pesquisaRapidaClientesModal = $('#pesquisaRapidaClientes');
		/* Pega o nome do cliente digitado pelo usuário. */
		this.nomeInput = $('#nomeClienteModal');
		/* Botão pesquisar do modal de pesquisa rápida de clientes. */
		this.pesquisaRapidaBtn = $('.js-pesquisa-rapida-clientes-btn');
		/* Pega o container onde será inserido o conteúdo do handlebars. */
		this.containerTabelaPesquisa = $('#containerTabelaPesquisaRapidaClientes');
		/* Pega o html do template do handlebars, para processarmos. */
		this.htmlTabelaPesquisa = $('#tabela-pesquisa-rapida-cliente').html();
		/* Compila o template do handlebars. */
		this.template = Handlebars.compile(this.htmlTabelaPesquisa);
		this.mensagemErro = $('.js-mensagem-erro');
	}

	PesquisaRapidaCliente.prototype.iniciar = function() {
		this.pesquisaRapidaBtn.on('click', onPesquisaRapidaClicado.bind(this));
	}

	function onPesquisaRapidaClicado(event) {
		/*
		 * Previne o comportamento padrão do botão, que seria fazer o submit do
		 * formulário.
		 */
		event.preventDefault();

		$.ajax({
			/* Pega a url buscando o form do modal e pegando o atributo action. */
			url : this.pesquisaRapidaClientesModal.find('form').attr('action'),
			method : 'GET',
			contentType : 'application/json',
			data : {
				nome : this.nomeInput.val()
			},
			success : onPesquisaConcluida.bind(this),
			error : onErroPesquisa.bind(this)
		});
	}

	function onPesquisaConcluida(resultado) {
		/*
		 * Processa o template do handlebars, passando o array de clientes. Ele
		 * me retorna o html depois de realizar o processamento.
		 */
		var html = this.template(resultado);
		/*
		 * Coloca no container reservado para o handlebars, o html depois de
		 * processado pelo handlebars.
		 */
		this.containerTabelaPesquisa.html(html);
		this.mensagemErro.addClass('hidden');
	}

	function onErroPesquisa() {
		/* Em caso de erro, exibe a mensagem de informar no mínimo 3 letras. */
		this.mensagemErro.removeClass('hidden');
	}

	return PesquisaRapidaCliente;

}());

$(function() {
	var pesquisaRapidaCliente = new Brewer.PesquisaRapidaCliente();
	pesquisaRapidaCliente.iniciar();
});