package com.algaworks.brewer.config.format;

import java.math.BigDecimal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class BigDecimalFormatter extends NumberFormatter<BigDecimal> {

	@Autowired
	private Environment env;

	@Override
	public String pattern(Locale locale) {
		/*
		 * Aqui não preciso do Locale na mascára de numeros, pois ela não muda entre
		 * Locale. Eu uso o Locale lá na hora de fazer os parses já.
		 */
//		String formato = 
		return env.getProperty("bigdecimal.format", "#,##0.00");
	}

}
