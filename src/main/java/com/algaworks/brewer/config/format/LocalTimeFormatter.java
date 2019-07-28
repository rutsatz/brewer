package com.algaworks.brewer.config.format;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class LocalTimeFormatter extends TemporalFormatter<LocalDateTime> {

	@Autowired
	private Environment env;

	@Override
	public String pattern(Locale locale) {
		/*
		 * Propertie que vem lá do application.properties. Então, se eu especificar lá,
		 * ele vai usar o formato do arquivo. Mas se não tiver nada, vou deixar o padrão
		 * pt-BR.
		 */
		return env.getProperty("localdatetime.format-" + locale, "dd/MM/yyyy HH:mm");
	}

	@Override
	public LocalDateTime parse(String text, DateTimeFormatter formatter) {
		return LocalDateTime.parse(text, formatter);
	}

}
