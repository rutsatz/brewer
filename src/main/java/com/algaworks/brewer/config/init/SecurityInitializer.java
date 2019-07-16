package com.algaworks.brewer.config.init;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/**
 * O Spring Security precisa de filtros pra poder interceptar as requisições e
 * poder adicionar a segurança. Por isso precisamos desse initializer.
 */
public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {

}
