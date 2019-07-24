Brewer = Brewer || {};

Brewer.BotaoSubmit = (function() {
	
	function BotaoSubmit() {
		/* Busca os 3 botões de submit lá da venda. */
		this.submitBtn = $('.js-submit-btn');
		this.formulario = $('.js-formulario-principal');
	}
	
	BotaoSubmit.prototype.iniciar = function() {
		this.submitBtn.on('click', onSubmit.bind(this));
	}
	
	function onSubmit(evento) {
		evento.preventDefault();
		
		/* Pega o botão que foi clicado. */
		var botaoClicado = $(evento.target);
	
		/* Pega qual dos botões que foi clicado. */
		var acao = botaoClicado.data('acao');
		
		/* Cria um input para ser enviado ao server e podermos saber qual botão foi clicado. */
		var acaoInput = $('<input>');
		acaoInput.attr('name', acao);
		
		/* Adiciona o input no formulario. */
		this.formulario.append(acaoInput);
		/* Submete o formulário. */
		this.formulario.submit();
	}
	
	return BotaoSubmit;
	
}());

$(function() {
	
	var botaoSubmit = new Brewer.BotaoSubmit();
	botaoSubmit.iniciar();
	
});