/* Brewer ou um novo objeto. */
var Brewer = Brewer || {};

Brewer.UploadFoto = (function() {

	/* Construtor. */
	function UploadFoto() {

		this.inputNomeFoto = $('input[name=foto]'); 
        this.inputContentType = $('input[name=contentType]'); 

        /* Processador de template Handlebarsjs */
        this.htmlFotoCervejaTemplate = $("#foto-cerveja").html();
        this.template = Handlebars.compile(this.htmlFotoCervejaTemplate);

        /* Contém o drag-and-drop e o template com a imagem carregada. */
        this.containerFotoCerveja = $('.js-container-foto-cerveja');

        this.uploadDrop = $('.js-upload');
	}

	/* Inicia, podendo receber dependências externas. */
	UploadFoto.prototype.iniciar = function() {
		var settings = {
			type: 'json',
			filelimit: 1,
			allow: '*.(jpg|jpeg|png)',
			url: this.containerFotoCerveja.data('url-fotos'),
			complete: onUploadCompleto.bind(this),
			beforeSend: adicionarCsrfToken
		}

		/* Inicializa o componente. */
		UIkit.upload(this.uploadDrop, settings);

		/* Pega o ProgessBar */
	    var bar = $('#js-progressbar');

	    /* Tratamento para caso o usuário tenha clicado em salvar mas deu algum erro na validação.
	       Então, deve recarregar novamente a foto. Para isso, basta verificar se o inputNomeFoto está
	       com valor, pois quando selecionamos a foto, setamos o value desse campo. */
	    if(this.inputNomeFoto.val()){
	    	/* Aí para recarregar, basta chamar novamente a função onUploadCompleto, como se tivessemos
	    	   feito o upload com sucesso. Porém, temos que executar a função no contexto do objeto,
	    	   por isso usamos o método call e passamos o this, indicando que queremos que o método
	    	   seja executado nesse contexto. */
	    	onUploadCompleto.call(this, { response: { nome: this.inputNomeFoto.val(), contentType: this.inputContentType.val() } });
	    }
	}

	function onUploadCompleto(resposta) {
		 this.inputNomeFoto.val(resposta.response.nome);
         this.inputContentType.val(resposta.response.contentType);

         /* Esconde o drag and drop */
         this.uploadDrop.addClass('hidden');

         /* Troca as variáveis dentro do template, conforme o objeto passado por parâmtro. */
         var htmlFotoCerveja = this.template({nomeFoto: resposta.response.nome});
         this.containerFotoCerveja.append(htmlFotoCerveja);

         /* Botão para remover a foto. */
         $('.js-remove-foto').on('click', onRemoverFoto.bind(this));
	}

	function onRemoverFoto() {
		/* Exclui o template da foto. */
    	$('.js-foto-cerveja').remove();	            	
    	this.uploadDrop.removeClass('hidden');
    	this.inputNomeFoto.val('');
    	this.inputContentType.val('');
	}

	/* Recebe o objeto xhr, que é o que eu tenho que adicionar o token, pois esse método é chamado no
	 * beforeSend da requisição Ajax do jQuery. */
	function adicionarCsrfToken(xhr) {
		var token = $('input[name=_csrf]').val();
		var header = $('input[name=_csrf_header]').val();
		xhr.setRequestHeader(header, token);
	}

	return UploadFoto;

})();

$(function() {

	var uploadFoto = new Brewer.UploadFoto();
	uploadFoto.iniciar();

});