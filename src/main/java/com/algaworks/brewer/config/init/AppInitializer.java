package com.algaworks.brewer.config.init;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.algaworks.brewer.config.JPAConfig;
import com.algaworks.brewer.config.ServiceConfig;
import com.algaworks.brewer.config.WebConfig;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { JPAConfig.class, ServiceConfig.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { WebConfig.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected Filter[] getServletFilters() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);

		return new Filter[] { characterEncodingFilter };
	}

	@Override
	protected void customizeRegistration(Dynamic registration) {
		/*
		 * Configura o servidor para receber o upload de arquivos (Multipart). O
		 * MultipartConfigElement recebe vários parâmetros. O primeiro diz ao Tomcat
		 * onde salvar os arquivos temporários. Se não passar nada, deixamos o servidor
		 * decidir onde salvá-los. Depois, existem outros parâmetros como limite máximo
		 * do arquivo e limite máximo da requisição. Se usamos somente o construtor com
		 * o parâmetro location, deixamos o servidor usar as configurações default.
		 */
		registration.setMultipartConfig(new MultipartConfigElement(""));
	}

}
