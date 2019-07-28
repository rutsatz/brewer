//package com.algaworks.brewer.config.init;
//
//import java.util.EnumSet;
//
//import javax.servlet.FilterRegistration;
//import javax.servlet.ServletContext;
//import javax.servlet.SessionTrackingMode;
//
//import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
//import org.springframework.web.filter.CharacterEncodingFilter;
//
///**
// * O Spring Security precisa de filtros pra poder interceptar as requisições e
// * poder adicionar a segurança. Por isso precisamos desse initializer.
// */
//public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {
//
//	@Override
//	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
//
//		/*
//		 * Configuro o tempo máximo da sessão de cada usuário (em segundos). Essa
//		 * configuração aqui define o tempo máximo da sessão, independente se o usuário
//		 * está usando o sistema ou não. É o tempo máximo da sessão, e não o tempo
//		 * máximo sem atividade.
//		 */
////		servletContext.getSessionCookieConfig().setMaxAge(20);
//
//		/*
//		 * Por default o spring utiliza o tracking da sessão pela url. Por isso, as
//		 * vezes aparece o jSessionID na url do browser. Mas isso não é legal. E pode
//		 * ser um problema de segurança. Então posso alterar esse comportamento para
//		 * utilizar Cookies ao invés da url.
//		 */
//		servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
//
//		/*
//		 * Adiciona o filtro de charset no SpringSecurity, antes dele aplicar seus
//		 * filtros, senão a codificação não funciona e dá problemas com os caracteres
//		 * especiais. (O encoding que setamos no AppInitializer não funciona mais depois
//		 * de adicionar o Spring Security).
//		 */
//		FilterRegistration.Dynamic characterEncodingFilter = servletContext.addFilter("encodingFilter",
//				new CharacterEncodingFilter());
//		/* Deve usar UTF-/ */
//		characterEncodingFilter.setInitParameter("encoding", "UTF-8");
//		/* Força o uso do UTF-8. */
//		characterEncodingFilter.setInitParameter("forceEncoding", "true");
//		/* Aplica para todas as urls. */
//		characterEncodingFilter.addMappingForUrlPatterns(null, false, "/*");
//
//	}
//
//}
