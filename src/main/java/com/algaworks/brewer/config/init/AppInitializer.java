package com.algaworks.brewer.config.init;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.algaworks.brewer.config.JPAConfig;
import com.algaworks.brewer.config.SecurityConfig;
import com.algaworks.brewer.config.ServiceConfig;
import com.algaworks.brewer.config.WebConfig;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
		/*
		 * Needed to support a "request" scope in Spring Security filters,
		 * since they're configured as a Servlet Filter. But not necessary
		 * if they're configured as interceptors in Spring MVC.
		 */
		servletContext.addListener(new RequestContextListener());
	}
	
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { JPAConfig.class, ServiceConfig.class, SecurityConfig.class };
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
		/*
		 * Quando foi adicionado o Spring Security, esse filtro não funciona mais. Eu
		 * preciso mover isso para o SecurityInitializer, colocando antes do
		 * SpringSecurity aplicar seus filtros.
		 */
//		import org.springframework.web.filter.CharacterEncodingFilter;

//		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
//		characterEncodingFilter.setEncoding("UTF-8");
//		characterEncodingFilter.setForceEncoding(true);

	    /* Preciso criar esse filter para permitir receber parâmetros através de requisições do tipo PUT. 
	     * O SpringMVC bloquea os parâmetros PUT por default. */
	    HttpPutFormContentFilter httpPutFormContentFilter = new HttpPutFormContentFilter();
	    
		return new Filter[] { httpPutFormContentFilter };
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
