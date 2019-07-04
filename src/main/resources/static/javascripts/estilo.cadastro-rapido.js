$(function() {

	var modal = $('#modalCadastroRapidoEstilo');
	var botaoSalvar = modal.find('.js-modal-cadastro-estilo-salvar-btn');
	
	var form = modal.find('form');
//	Evita o redirect dentro do modal se o usuário pressionar enter.
	form.on('submit', function(event) { event.preventDefault() });

// Pega a url do formulário.
	var url = form.attr('action');

	var inputNomeEstilo = $('#nomeEstilo');
	var containerMensagemErro = $('.js-mensagem-cadastro-rapido-estilo');
	
	// Evento lançado pelo bootstrap quando o modal é totalmente carregado e
	// exibido.
	modal.on('shown.bs.modal', onModalShow);

	// Evento lançado pelo bootstrap quando o modal é fechado.
	modal.on('hide.bs.modal', onModalClose);

	botaoSalvar.on('click', onBotaoSalvarClick);

	function onModalShow() {
		// Força o foco no campo ao carregar o modal. (O autofocus perdia o
		// focos, talvez por causa de algo com o bootstrap)
		inputNomeEstilo.focus();
	}

	function onModalClose() {
		// Limpa o campo input. (Caso usuário digitou algo, cancelou e abriu
		// novamente o modal.
		inputNomeEstilo.val('');

		// Esconde novamente a mensagem de erro, se usuário tentou cadastrar,
		// deu erro, fechou a modal e depois abriu novamente.
		containerMensagemErro.addClass('hidden');

		// Também remove o vermelho dos campos.
		form.find('.form-group').removeClass('has-error');
	}

	function onBotaoSalvarClick() {
		// Recupera o nome informado pelo usuário
		var nomeEstilo = inputNomeEstilo.val().trim();

		$.ajax({
			url : url,
			method : 'POST',
			contentType : 'application/json',
			data : JSON.stringify({
				nome : nomeEstilo
			}),
			error : onErroSalvandoEstilo,
			success : onEstiloSalvo
		});
	}

	function onErroSalvandoEstilo(obj) {
		var mensagemErro = obj.responseText;
		containerMensagemErro.removeClass('hidden');
		containerMensagemErro.html('<span>' + mensagemErro + '</span>');
		form.find('.form-group').addClass('has-error');
	}

	// O primeiro argumento é um objeto, que é o objeto retornado lá pelo
	// controller, que nesse caso é o estilo que foi salvo no banco.
	function onEstiloSalvo(estilo) {
		var comboEstilo = $('#estilo');
		// Adiciona no combo a opção recém cadastrada.
		comboEstilo.append('<option value=' + estilo.codigo + '>' + estilo.nome	+ '</option>');
		// Seleciona automaticamente o novo estilo salvo,
		comboEstilo.val(estilo.codigo);
		// Fecha o modal.
		modal.modal('hide');
	}

});