var Brewer = Brewer || {};

Brewer.MascaraCpfCnpj = (function() {

	function MascaraCpfCnpj() {
		this.radioTipoPessoa = $('.js-radio-tipo-pessoa');
		this.labelCpfCnpj = $('[for=cpfOuCnpj]');
		this.inputCpfCnpj = $('#cpfOuCnpj');
	}

	MascaraCpfCnpj.prototype.iniciar = function() {
		this.radioTipoPessoa.on('change', onTipoPessoaAlterado.bind(this));
		/*
		 * Filtra os radio selecionados. Como só pode ter um, pega o primeiro.
		 * Se não tiver nenhum, ele retorna undefined.
		 */
		var tipoPessoaSelecionada = this.radioTipoPessoa.filter(':checked')[0];
		if (tipoPessoaSelecionada) {
			/*
			 * O filter retorna um input normal. Mas como usamos funções do
			 * jQuery dentro do método, temos que converte-lô num objeto jQuery,
			 * passando ele por parâmetro para o método $();
			 */
			aplicarMascara.call(this, $(tipoPessoaSelecionada));
		}
	}

	/* Evento change dos radio button */
	function onTipoPessoaAlterado(evento) {
		var tipoPessoaSelecionada = $(evento.currentTarget);
		aplicarMascara.call(this, tipoPessoaSelecionada);
		/*
		 * Somente limpa o valor digitado quando selecionar outro tipo de
		 * pessoa.
		 */
		this.inputCpfCnpj.val('');
	}

	function aplicarMascara(tipoPessoaSelecionada) {
		this.labelCpfCnpj.text(tipoPessoaSelecionada.data('documento'));
		this.inputCpfCnpj.mask(tipoPessoaSelecionada.data('mascara'));
		this.inputCpfCnpj.removeAttr('disabled');
	}

	return MascaraCpfCnpj;

}());

$(function() {
	var mascaraCpfCnpj = new Brewer.MascaraCpfCnpj();
	mascaraCpfCnpj.iniciar();
});