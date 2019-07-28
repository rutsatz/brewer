package com.algaworks.brewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BrewerApplication {

	private static ApplicationContext APPLICATION_CONTEXT;

	public static void main(String[] args) {
		/* Salvo o contexto do Spring. */
		APPLICATION_CONTEXT = SpringApplication.run(BrewerApplication.class, args);
	}

	/*
	 * Permite que eu recupere um BEAN do contexto do Spring de um contexto de fora,
	 * como por execemplo, no CervejaEntityListener, que é acionado pelo JPA.
	 */
	public static <T> T getBean(Class<T> requiredType) {
		return APPLICATION_CONTEXT.getBean(requiredType);
	}

}
