package com.algaworks.brewer.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.apache.commons.beanutils.BeanUtils;

import com.algaworks.brewer.validation.AtributoConfirmacao;

/**
 * Implementa a ConstraintValidator, passando para qual annotation a
 * implementação será aplicada e em qual objeto posso usar a anotação. Nesse
 * caso, posso usar em qualquer objeto (Não precisa ser somente na classe
 * Usuario), dessa forma, passo Object.
 */
public class AtributoConfirmacaoValidator implements ConstraintValidator<AtributoConfirmacao, Object> {

	private String atributo;
	private String atributoConfirmacao;

	@Override
	public void initialize(AtributoConfirmacao constraintAnnotation) {
		/*
		 * Esses campos são a string que passo por parâmetro pra anottaion. Ex: "senha"
		 * e "confirmacaoSenha"
		 */
		this.atributo = constraintAnnotation.atributo();
		this.atributoConfirmacao = constraintAnnotation.atributoConfirmacao();
	}

	/*
	 * O primeiro atributo é a instância da minha classe Usuário, com tudo
	 * preenchido.
	 */
	@Override
	public boolean isValid(Object object, ConstraintValidatorContext context) {
		boolean valido = false;
		try {
			/*
			 * Recupera o valor de um atributo de um Object. Passamos o objeto e o nome do
			 * campo que queremos recuperar. Passamos o objeto Usuario e o nome "senha" para
			 * recuperar o valor da senha.
			 */
			Object valorAtributo = BeanUtils.getProperty(object, atributo);
			Object valorAtributoConfirmacao = BeanUtils.getProperty(object, atributoConfirmacao);

			valido = ambosSaoNull(valorAtributo, valorAtributoConfirmacao)
					|| ambosSaoIguais(valorAtributo, valorAtributoConfirmacao);

		} catch (Exception e) {
			throw new RuntimeException("Erro recuperando valores dos atributos", e);
		}

		/*
		 * Preciso sinalizar os atributos que estão com erro, se não não vai mostrar o
		 * erro na tela.
		 */
		if (!valido) {
			/* Desabilito para não duplicar a mensagem na tela. */
			context.disableDefaultConstraintViolation();
			String mensagem = context.getDefaultConstraintMessageTemplate();
			ConstraintViolationBuilder violationBuilder = context.buildConstraintViolationWithTemplate(mensagem);
			/* Estou dizendo que o atributoConfirmacao está com erro. */
			violationBuilder.addPropertyNode(atributoConfirmacao).addConstraintViolation();
		}

		return valido;
	}

	private boolean ambosSaoIguais(Object valorAtributo, Object valorAtributoConfirmacao) {
		return valorAtributo != null && valorAtributo.equals(valorAtributoConfirmacao);
	}

	private boolean ambosSaoNull(Object valorAtributo, Object valorAtributoConfirmacao) {
		return valorAtributo == null && valorAtributoConfirmacao == null;
	}

}
