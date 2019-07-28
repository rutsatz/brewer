package com.algaworks.brewer.config.format;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class LocalDateFormatter extends TemporalFormatter<LocalDate> {

	@Autowired
	private Environment env;

	@Override
	public String pattern(Locale locale) {
		/*
		 * Propertie que vem lá do application.properties. Então, se eu especificar lá,
		 * ele vai usar o formato do arquivo. Mas se não tiver nada, vou deixar o padrão
		 * pt-BR.
		 */
		return env.getProperty("localdate.format-" + locale, "dd/MM/yyyy");
	}

	@Override
	public LocalDate parse(String text, DateTimeFormatter formatter) {
		return LocalDate.parse(text, formatter);
	}

}
