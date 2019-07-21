Brewer = Brewer || {};

Brewer.Autocomplete = (function() {
	
	function Autocomplete() {
		/* Pega o input com o texto digitado pelo usuário (Sku ou nome) */
		this.skuOuNomeInput = $('.js-sku-nome-cerveja-input');
		/* Pega o html do template do handlebars que será usado para renderizar as
		 * linhas do EasyAutoComplete. */
		var htmlTemplateAutocomplete = $('#template-autocomplete-cerveja').html();
		/* Compila o template do handlebars e deixa pronto pra uso. */
		this.template = Handlebars.compile(htmlTemplateAutocomplete);
	}
	
	Autocomplete.prototype.iniciar = function() {
		var options = {
			/* Na url eu posso passar uma String ou então uma funcion. No caso da function,
			 * o easyAutoComplete passa por parâmetro o valor digitado no campo e eu posso
			 * retornar uma string com a url da pesquisa. Ai ele vai chamar essa function
			 * para pegar a url na hora de fazer a requisição. */
			url: function(skuOuNome) {
				return '/brewer/cervejas?skuOuNome=' + skuOuNome;
			},
			/* Campo que deve aparecer. Nesse caso, pega o campo nome da requisição Ajax,
			 * que é o nome da cerveja. */
			getValue: 'nome',
			/* Somente faz a busca se tiver digitado no minimo 3 caracteres. */
			minCharNumber: 3,
			/* Espera 300ms depois do usuário terminar de digitar, para fazer a requisição pro
			 * servidor. */
			requestDelay: 300,
			/* Passa o contentType para usar o método certo do controller, que é o que
			 * consome o json. Se não passar o contentType, ele cai no get normal, e retorna
			 * a view da pesquisa de cervejas, ou seja, retorna html ao invés de json. */
			ajaxSettings: {
				contentType: 'application/json'
			},
			/* Customizo o template que será usado para renderizar o resultado. */
			template: {
				/* Digo que é um template customizado. */
				type: 'custom',
				/* A function recebe por parâmetro o value que eu defini no campo getValue
				 * (nesse caso, o nome da cerveja) e no segunda parâmetro o objeto retornado
				 * do banco de dados (nesse caso, os dados da cerveja).
				 * Ele executa essa função para cada linha que será renderizada.
				 * Essa função já deve retornar o html que quero renderizar. */
				method: function(nome, cerveja) {
					cerveja.valorFormatado = Brewer.formatarMoeda(cerveja.valor);
					/* Processa o template, passando o objeto cerveja por parâmetro. */
					return this.template(cerveja);
				}.bind(this)
			}
		};
		
		/* Inicia o EasyAutoComplete no input. */
		this.skuOuNomeInput.easyAutocomplete(options);
	}
	
	return Autocomplete
	
}());

$(function() {
	
	var autocomplete = new Brewer.Autocomplete();
	autocomplete.iniciar();
	
})