package com.algaworks.brewer.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.algaworks.brewer.mail.Mailer;

@Configuration
@ComponentScan(basePackageClasses = Mailer.class)
/*
 * Carrego um arquivo de properties que está dentro do projeto. Ele vai carregar
 * dentro da classe Environment.Dessa forma, carrego sempre fixo.
 */
//@PropertySource({ "classpath:env/mail.properties" })
/*
 * Para carregar dinamico, conforme o ambiente, eu posso comcatenar o nome do
 * arquivo com o nome do ambiente que eu configuro externamente através de uma
 * variável que uso para compor o nome do arquivo. E se eu não informar nenhum
 * ambiente, eu digo que o local é o default.
 */
@PropertySource({ "classpath:env/mail-${ambiente:local}.properties" })
/*
 * Carrego as properties através de um arquivo externo a aplicação. A partir do
 * java8, posso colocar duas anotações repetidas, que ai ele vai sobrescrevendo
 * as anteriores, se tiver uma propriedade repetida. Se o arquivo não existir,
 * vai dar erro na hora de subir a aplicação. Então, se eu não quero que dê
 * erro, devo passar o parâmetro ignoreResourceNotFound. Consequentemente, não
 * usar os parâmetros do arquivo anterior.
 */
@PropertySource(value = { "file://${HOME}/.brewer-mail.properties" }, ignoreResourceNotFound = true)
public class MailConfig {

	@Autowired
	private Environment env;

	@Bean
	public JavaMailSender mailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

		mailSender.setHost("smtp.mailgun.org");
		mailSender.setPort(587);
		mailSender.setUsername(env.getProperty("mail.username"));
		mailSender.setPassword(env.getProperty("password"));

		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		/* Deve autenticar. */
		props.put("mail.smtps.auth", true);
		/* É pra usar TLS, para encapsular. */
		props.put("mail.smtps.starttls", true);
		props.put("mail.debug", false);
		props.put("mail.smtp.connectiontimeout", 10000); // milliseconds

		mailSender.setJavaMailProperties(props);

		return mailSender;
	}

}
