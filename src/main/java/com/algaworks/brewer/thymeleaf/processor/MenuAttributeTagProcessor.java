package com.algaworks.brewer.thymeleaf.processor;

import javax.servlet.http.HttpServletRequest;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

/* Cria o processor para o nosso atributo classforerror. */
public class MenuAttributeTagProcessor extends AbstractAttributeTagProcessor {

	/* Nome do atributo que será usado no HTML */
	private static final String NOME_ATRIBUTO = "menu";

	/*
	 * Precedência que o atributo deve ser executado. Usado quando criamos mais
	 * elementos e queremos definir a ordem de processamento deles.
	 */
	private static final int PRECEDENCIA = 1000;

	public MenuAttributeTagProcessor(String dialectPrefix) {
		/*
		 * Estamos usando o modo HTML. Prefixo da tag (nesse caso, brewer). Passado null
		 * e false pq não estamos criando elementos. Estamos passando o nome do atributo
		 * e true pq estamos criando um atributo. Estamos passando a precedencia do
		 * atributo. E o último parametro, se for true, ele apaga o atributo do HTML, se
		 * for false, ele mantém o atributo lá.
		 */
		super(TemplateMode.HTML, dialectPrefix, null, false, NOME_ATRIBUTO, true, PRECEDENCIA, true);
	}

	/* Método que faz o processamento do nosso atributo. */
	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
			String attributeValue, IElementTagStructureHandler structureHandler) {

		/*
		 * No attributeValue recebo o valor que foi informado como parâmetro do atributo
		 * lá no HTML. Como no atributo eu passo somente a url, sem o contexto, a url e
		 * a uri serão diferentes. Exemplo: url=/estilos. uri=/brewer/estilos. Por isso,
		 * eu adiciono uma expressão no meu atributo e preciso interpretá-la. Por isso
		 * esse código foi comentado e foi adicionado o código abaixo.
		 */
//		String menu = attributeValue;

		/*
		 * Processo a nossa tah, adicionando o contexto da aplicação. (Pois nós
		 * adicionamos o @{} no html)
		 */
		IEngineConfiguration configuration = context.getConfiguration();
		IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
		IStandardExpression expression = parser.parseExpression(context, attributeValue);
		String menu = (String) expression.execute(context);

		/*
		 * Preciso saber a URL da requisição. Para isso, preciso recuperar a requisição,
		 * através do método abaixo. Aí depois consigo pegar a URI dele.
		 */
		HttpServletRequest request = ((IWebContext) context).getRequest();

		/*
		 * Pega a uri da requisição, para comparar com o menu e saber se estamos no item
		 * selecionado no menu.
		 */
		String uri = request.getRequestURI();

		/*
		 * Usa o startsWith pois posso ter suburls, como /estilos/novo. O menu tbm
		 * precisa ficar ativo nesses casos.
		 *
		 * Porém, como tem casos em que quero a pesquisa e o cadastro no mesmo menu e
		 * casos que quero em menus separados, mudei para o matches, assim, eu passo um
		 * expressão regular lá na tag.
		 */
		if (uri.matches(menu)) {
			/* Pega as classes que já existem dentro do atributo class do elemento. */
			String classesExistentes = tag.getAttributeValue("class");
			/* Adiciona no atributo class as classes existentes mais a class has-error */
			structureHandler.setAttribute("class", classesExistentes + " is-active");
		}
	}
}
