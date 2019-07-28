package com.algaworks.brewer.config.format;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class IntegerFormatter extends NumberFormatter<Integer> {

	@Autowired
	private Environment env;

	@Override
	public String pattern(Locale locale) {
		/*
		 * Aqui não preciso do Locale na mascára de numeros, pois ela não muda entre
		 * Locale. Eu uso o Locale lá na hora de fazer os parses já.
		 */
		return env.getProperty(env.getProperty("integer.format", "#,##0"));
	}

}
