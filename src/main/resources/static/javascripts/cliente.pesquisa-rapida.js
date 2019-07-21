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
		/* Evento do bootstrapt ao exibir o modal. */
		this.pesquisaRapidaClientesModal.on('shown.bs.modal', onModalShow
				.bind(this));
	}

	function onModalShow() {
		/* Coloca o foco no input do nome. */
		this.nomeInput.focus();
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
		this.mensagemErro.addClass('hidden');
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

		/*
		 * Cria um objeto separado para trabalhar com a tabela do resultado da
		 * pesquisa. Isso porque a cada pesquisa, essa tabela é renderizada
		 * novamente pelo handlebars.
		 */
		var tabelaClientePesquisaRapida = new Brewer.TabelaClientePesquisaRapida(
				this.pesquisaRapidaClientesModal);
		tabelaClientePesquisaRapida.iniciar();
	}

	function onErroPesquisa() {
		/* Em caso de erro, exibe a mensagem de informar no mínimo 3 letras. */
		this.mensagemErro.removeClass('hidden');
	}

	return PesquisaRapidaCliente;

}());

Brewer.TabelaClientePesquisaRapida = (function() {

	function TabelaClientePesquisaRapida(modal) {
		/*
		 * Recebe o modal do cliente por parâmetro para poder fechá-lo quando o
		 * usuário selecionar.
		 */
		this.modalCliente = modal;
		/* Recupera as rows da tabela de clientes. */
		this.cliente = $('.js-cliente-pesquisa-rapida');
	}

	TabelaClientePesquisaRapida.prototype.iniciar = function() {
		/*
		 * Adiciona os eventos de click nas linhas da tabela, para capturar o
		 * cliente selecionado.
		 */
		this.cliente.on('click', onClienteSelecionado.bind(this));
	}

	function onClienteSelecionado(evento) {
		/* Esconde o modal. */
		this.modalCliente.modal('hide');

		/* Pega a linha selecionada. */
		var clienteSelecionado = $(evento.currentTarget);
		/*
		 * Pega os dados da linha selecionada e já seta lá nos inputs da tela da
		 * venda.
		 */
		$('#nomeCliente').val(clienteSelecionado.data('nome'));
		$('#codigoCliente').val(clienteSelecionado.data('codigo'));
	}

	return TabelaClientePesquisaRapida;

}());

$(function() {
	var pesquisaRapidaCliente = new Brewer.PesquisaRapidaCliente();
	pesquisaRapidaCliente.iniciar();
});