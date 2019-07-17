package com.algaworks.brewer.config.init;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * O Spring Security precisa de filtros pra poder interceptar as requisições e
 * poder adicionar a segurança. Por isso precisamos desse initializer.
 */
public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {

	@Override
	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {

		/*
		 * Adiciona o filtro de charset no SpringSecurity, antes dele aplicar seus
		 * filtros, senão a codificação não funciona e dá problemas com os caracteres
		 * especiais. (O encoding que setamos no AppInitializer não funciona mais depois
		 * de adicionar o Spring Security).
		 */
		FilterRegistration.Dynamic characterEncodingFilter = servletContext.addFilter("encodingFilter",
				new CharacterEncodingFilter());
		/* Deve usar UTF-/ */
		characterEncodingFilter.setInitParameter("encoding", "UTF-8");
		/* Força o uso do UTF-8. */
		characterEncodingFilter.setInitParameter("forceEncoding", "true");
		/* Aplica para todas as urls. */
		characterEncodingFilter.addMappingForUrlPatterns(null, false, "/*");

	}

}
