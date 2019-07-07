/* Brewer ou um novo objeto */
var Brewer = Brewer || {};

Brewer.EstiloCadastroRapido = (function() {

	/* Construtor. Contém as inicializações. */
	function EstiloCadastroRapido() {
		this.modal = $('#modalCadastroRapidoEstilo');
		this.botaoSalvar = this.modal
				.find('.js-modal-cadastro-estilo-salvar-btn');
		this.form = this.modal.find('form');
		// Pega a url do formulário.
		this.url = this.form.attr('action');
		this.inputNomeEstilo = $('#nomeEstilo');
		this.containerMensagemErro = $('.js-mensagem-cadastro-rapido-estilo');
	}

	/*
	 * Inicializa as depencendias externas. Por isso, está num método separado,
	 * pois posso passar dependencias pelo construtor.
	 */
	EstiloCadastroRapido.prototype.iniciar = function() {
		// Evita o redirect dentro do modal se o usuário pressionar enter.
		this.form.on('submit', function(event) {
			event.preventDefault()
		});
		/*
		 * Evento lançado pelo bootstrap quando o modal é totalmente carregado e
		 * exibido. O bind força o modal a executar dentro do contexto do
		 * construtor, pois é nele que é definido o objeto inputEstilo. Então se
		 * executar fora desse contexto, esse objeto não será encontrado.
		 */
		this.modal.on('shown.bs.modal', onModalShow.bind(this));
		// Evento lançado pelo bootstrap quando o modal é fechado.
		this.modal.on('hide.bs.modal', onModalClose.bind(this));
		this.botaoSalvar.on('click', onBotaoSalvarClick.bind(this));
	}

	function onModalShow() {
		// Força o foco no campo ao carregar o modal. (O autofocus perdia o
		// focos, talvez por causa de algo com o bootstrap)
		this.inputNomeEstilo.focus();
	}

	function onModalClose() {
		// Limpa o campo input. (Caso usuário digitou algo, cancelou e abriu
		// novamente o modal.
		this.inputNomeEstilo.val('');

		// Esconde novamente a mensagem de erro, se usuário tentou cadastrar,
		// deu erro, fechou a modal e depois abriu novamente.
		this.containerMensagemErro.addClass('hidden');

		// Também remove o vermelho dos campos.
		this.form.find('.form-group').removeClass('has-error');
	}

	function onBotaoSalvarClick() {
		// Recupera o nome informado pelo usuário
		var nomeEstilo = this.inputNomeEstilo.val().trim();

		$.ajax({
			url : this.url,
			method : 'POST',
			contentType : 'application/json',
			data : JSON.stringify({
				nome : nomeEstilo
			}),
			error : onErroSalvandoEstilo.bind(this),
			success : onEstiloSalvo.bind(this)
		});
	}

	function onErroSalvandoEstilo(obj) {
		var mensagemErro = obj.responseText;
		this.containerMensagemErro.removeClass('hidden');
		this.containerMensagemErro.html('<span>' + mensagemErro + '</span>');
		this.form.find('.form-group').addClass('has-error');
	}

	// O primeiro argumento é um objeto, que é o objeto retornado lá pelo
	// controller, que nesse caso é o estilo que foi salvo no banco.
	function onEstiloSalvo(estilo) {
		var comboEstilo = $('#estilo');
		// Adiciona no combo a opção recém cadastrada.
		comboEstilo.append('<option value=' + estilo.codigo + '>' + estilo.nome
				+ '</option>');
		// Seleciona automaticamente o novo estilo salvo,
		comboEstilo.val(estilo.codigo);
		// Fecha o modal.
		this.modal.modal('hide');
	}

	return EstiloCadastroRapido;

}());

$(function() {

	var estiloCadastroRapido = new Brewer.EstiloCadastroRapido();
	estiloCadastroRapido.iniciar();

});