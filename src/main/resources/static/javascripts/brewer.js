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

Brewer.MaskPhoneNumber = (function() {

	function MaskPhoneNumber() {
		this.inputPhoneNumber = $('.js-phone-number');
	}

	MaskPhoneNumber.prototype.enable = function() {
		var maskBehavior = function(val) {
			return val.replace(/\D/g, '').length === 11 ? '(00) 00000-0000'
					: '(00) 0000-00009';
		};

		var options = {
			onKeyPress : function(val, e, field, options) {
				field.mask(maskBehavior.apply({}, arguments), options);
			}
		};

		this.inputPhoneNumber.mask(maskBehavior, options);
	}

	return MaskPhoneNumber;

}());

Brewer.MaskCep = (function() {

	function MaskCep() {
		this.inputCep = $('.js-cep');
	}

	MaskCep.prototype.enable = function() {
		this.inputCep.mask('00.000-000');
	}

	return MaskCep;

}());

Brewer.MaskDate = (function() {

	function MaskDate() {
		this.inputDate = $('.js-date');
		this.inputDate.datepicker({
			orientation : 'bottom',
			language : 'pt-BR',
			autoclose : true
		});
	}

	MaskDate.prototype.enable = function() {
		this.inputDate.mask('00/00/0000');
	}

	return MaskDate;

}());

/* Função ready do jQuery. */
$(function() {
	var maskMoney = new Brewer.MaskMoney();
	maskMoney.enable();

	var maskPhoneNumber = new Brewer.MaskPhoneNumber();
	maskPhoneNumber.enable();

	var maskCep = new Brewer.MaskCep();
	maskCep.enable();

	var maskDate = new Brewer.MaskDate();
	maskDate.enable();

});