package com.algaworks.brewer.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring4.util.FieldUtils;
import org.thymeleaf.templatemode.TemplateMode;

/* Cria o processor para o nosso atributo classforerror. */
public class ClassForErrorAttributeTagProcessor extends AbstractAttributeTagProcessor {

	/* Nome do atributo que será usado no HTML */
	private static final String NOME_ATRIBUTO = "classforerror";

	/*
	 * Precedência que o atributo deve ser executado. Usado quando criamos mais
	 * elementos e queremos definir a ordem de processamento deles.
	 */
	private static final int PRECEDENCIA = 1000;

	public ClassForErrorAttributeTagProcessor(String dialectPrefix) {
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
		 * No attributeValue vem o valor do meu atributo. Por exemplo:
		 * brewer:classforerro="sku". Nesse caso, seria sku.
		 */
		boolean temErro = FieldUtils.hasErrors(context, attributeValue);

		if (temErro) {
			/* Pega as classes que já existem dentro do atributo class do elemento. */
			String classesExistentes = tag.getAttributeValue("class");
			/* Adiciona no atributo class as classes existentes mais a class has-error */
			structureHandler.setAttribute("class", classesExistentes + " has-error");
		}
	}
}
