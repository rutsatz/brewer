package com.algaworks.brewer.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

public class OrderElementTagProcessor extends AbstractElementTagProcessor {

    private static final String NOME_TAG = "order";
    private static final int PRECEDENCIA = 1000;

    public OrderElementTagProcessor(String dialectPrefix) {
        /*
         * Modo do template. Prefixo do dialeto. Agora sim é um elemento. true por vou
         * usar o elemento. null pois não é um atributo. false pois não vou usar um
         * atributo. E por fim, a predencia de processamento do meu elemento.
         */
        super(TemplateMode.HTML, dialectPrefix, NOME_TAG, true, null, false, PRECEDENCIA);
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
            IElementTagStructureHandler structureHandler) {
        /*
         * O ModelFactory eu uso para criar os elementos HTML, que vou adicionar dentro
         * do meu model.
         */
        IModelFactory modelFactory = context.getModelFactory();
        
        /* Recupero os atributos enviados no html, para passar por parametro para o meu fragment. */
        IAttribute page = tag.getAttribute("page");
        IAttribute field = tag.getAttribute("field");
        IAttribute text = tag.getAttribute("text");
        
        /* Dentro do model que vou adicionar os meus elementos HTML. */
        IModel model = modelFactory.createModel();

        /*
         * Cria uma nova tag th:block e adiciona dentro do meu model. No segundo e
         * terceiro parametros, já consigo passar um atributo para essa nova tag e
         * também o valor desse meu atributo.
         *
         * Isso gera o código abaixo:
         * <th:block th:replace="fragments/Ordenacao :: order (${pagina}, 'nome', 'Nome')"></th:block>
         */
        model.add(modelFactory.createStandaloneElementTag("th:block", "th:replace",
                        String.format("fragments/Ordenacao :: order (%s, %s, '%s')",page.getValue(), field.getValue(), text.getValue() )));

        /*
         * Depois de criado meu novo elemento, digo que o elemento que está com o
         * brewer:message deve ser substítuido pelo novo elemento que criei. O segundo
         * parâmetro indica se o thymelaf ainda precisa processar o novo elemento que
         * criei. Como tenho os elementos th:block e th:include, que são do thymeleaf,
         * então tenho que passar true. Se fosse somente HTML, poderia passar false.
         */
        structureHandler.replaceWith(model, true);
    }

}
