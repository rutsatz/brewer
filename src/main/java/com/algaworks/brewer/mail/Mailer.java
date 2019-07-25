package com.algaworks.brewer.mail;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.storage.FotoStorage;

/* Classe responsável por enviar os email. */
@Component
public class Mailer {

	private static Logger logger = LoggerFactory.getLogger(Mailer.class);

	@Autowired
	private JavaMailSender mailSender;

	/*
	 * Injeta o processador de templates do thymeleaf, para processar o html que
	 * será enviado por email. Pra ter ele, eu preciso estar no contexto web.
	 */
	@Autowired
	private TemplateEngine thymeleaf;

	@Autowired
	private FotoStorage fotoStorage;

	/*
	 * Com o @Async, digo que a chamada ao método agora é assíncrona. Para isso,
	 * preciso adiconar o @EnableAsync lá no WebConfig para habilitar isso.
	 */
	@Async
	public void enviar(Venda venda) {

		Context context = new Context();
		/* Adiciono as variáveis que são usadas pelo template. */
		context.setVariable("venda", venda);
		/* Adiciona a variável da imagem de logo. */
		context.setVariable("logo", "logo");

		Map<String, String> fotos = new HashMap<>();
		boolean adicionarMockCerveja = false;
		/*
		 * Preciso passar para o thymeleaf antes de chamar o process. Por isso não posso
		 * colocar lá junto com o helper.
		 */
		for (ItemVenda item : venda.getItens()) {
			Cerveja cerveja = item.getCerveja();

			/* Se tiver foto. */
			if (cerveja.temFoto()) {
				/*
				 * Gero um id para cada foto, que depois vou usar para colocar a foto como
				 * inline dentro do email e poder recuperar ela através desse id.
				 */
				String cid = "foto-" + cerveja.getCodigo();
				context.setVariable(cid, cid);

				/* Salva os dados para poder recuperar lá embaixo, depois do process. */
				fotos.put(cid, cerveja.getFoto() + "|" + cerveja.getContentType());

			} else {
				/* Se não tiver foto. Aí adiciona o mock, para ficar disponível inline. */
				adicionarMockCerveja = true;
				context.setVariable("mockCerveja", "mockCerveja");

			}

		}

		try {
			/*
			 * Passo o arquivo e o contexto, para o thymeleaf compilar o template. Ele
			 * retorna o html processado.
			 */
			String email = thymeleaf.process("mail/ResumoVenda", context);

			/* Crio uma mensagem que pode ter HTML. */
			MimeMessage mimeMessage = mailSender.createMimeMessage();

			/*
			 * Helper para ajudar a construir a mensagem. O primeiro parâmetro é a
			 * mimeMessage, que contém o html, o segundo é o multipart, em que dizemos se
			 * vamos adicionar imagens na mensagem. E por fim, o encoding.
			 */
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			helper.setFrom("rutsatz@hotmail.com.br");
			helper.setTo(venda.getCliente().getEmail());
			helper.setSubject("Brewer - Venda realizada!");
			/*
			 * Passamos a string do corpo, e também passamos true, dizendo que contém html.
			 */
			helper.setText(email, true);

			/*
			 * Adiciono a imagem ao corpo do email, passando o cid, que é usado para
			 * recuperar a imagem lá no html.
			 */
			helper.addInline("logo", new ClassPathResource("static/images/logo-gray.png"));

			if (adicionarMockCerveja) {
				helper.addInline("mockCerveja", new ClassPathResource("static/images/cerveja-mock.png"));
			}

			for (String cid : fotos.keySet()) {
				String[] fotoContentType = fotos.get(cid).split("\\|");
				String foto = fotoContentType[0];
				String contentType = fotoContentType[1];

				byte[] arrayFoto = fotoStorage.recuperarThumbnail(foto);
				helper.addInline(cid, new ByteArrayResource(arrayFoto), contentType);

			}

			mailSender.send(mimeMessage);

		} catch (MessagingException e) {
			logger.error("Erro enviando e-mail", e);

			/*
			 * #### Tratar a exception ####
			 *
			 * Aqui não foi tratada. Mas num sistema real, devo tratar, e salvar no banco
			 * por exemplo, para saber se o e-mail foi enviado ou não. Algo informando que
			 * não conseguiu mandar os e-mails.
			 */

		}

		/* Mandando texto puro. */
//		SimpleMailMessage mensagem = new SimpleMailMessage();
//		mensagem.setFrom("rutsatz@hotmail.com.br");
//		mensagem.setTo(venda.getCliente().getEmail());
//		mensagem.setSubject("Venda Efetuada");
//		mensagem.setText("Obrigado por comprar comnosotros.");
//		mailSender.send(mensagem);
	}

}
