package com.algaworks.brewer.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
/*
 * Passa o padrão que o atributo anotado deve ter, ou seja, ele deve bater com
 * essa expressão regular.
 */
@Pattern(regexp = "([a-zA-z]{2}\\d{4})?")
public @interface SKU {

    /* Coloco a mensagem entre {}, ai ele busca do arquivo de internacionalização. */
	@OverridesAttribute(constraint = Pattern.class, name = "message")
	String message() default "{com.algaworks.constraints.SKU.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
