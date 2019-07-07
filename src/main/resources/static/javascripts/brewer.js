/* Cria o objeto Brewer (objeto global) para representar o namespace. Coloco tudo dentro dele.
 * Se esse objeto já existir, eu uso ele, caso contrátio, crio um novo. */
var Brewer = Brewer || {};

/*
 * Cria a função MaskMoney. Está com letra maíuscula pq vai usar uma função
 * construtora para iniciar ele. Isso é uma mistura do ModulePattern com
 * ConstructorPattern. Para executar a função, tem que colocar o () no final.
 */
Brewer.MaskMoney = (function() {

	/*
	 * Essa é a função que vai ser executada (função construtora). Ela que será
	 * retornada no return abaixo.
	 */
	function MaskMoney() {
		/*
		 * Inicializa os atributos na função construtora. Agora não é mais var,
		 * é this.
		 */
		this.decimal = $('.js-decimal');
		this.plain = $('.js-plain');
	}

	/*
	 * Adiciona a execução dentro do prototype. Agora referencia com o this. É
	 * coloca dentro do enable, pois eu poderia criar uma função disable, caso
	 * eu quissesse desabilitar em outro lugar.
	 */
	MaskMoney.prototype.enable = function() {
		this.decimal.maskMoney({
			decimal : ',',
			thousands : '.'
		});

		this.plain.maskMoney({
			precision : 0,
			thousands : '.'
		});
	}

	/* Retorna a função construtora */
	return MaskMoney;

}());

/* Função ready do jQuery. */
$(function() {
	var maskMoney = new Brewer.MaskMoney();
	maskMoney.enable();
});