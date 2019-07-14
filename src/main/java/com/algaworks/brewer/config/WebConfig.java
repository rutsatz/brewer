package com.algaworks.brewer.config;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeansException;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.number.NumberStyleFormatter;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.algaworks.brewer.controller.CervejasController;
import com.algaworks.brewer.controller.converter.CidadeConverter;
import com.algaworks.brewer.controller.converter.EstadoConverter;
import com.algaworks.brewer.controller.converter.EstiloConverter;
import com.algaworks.brewer.thymeleaf.BrewerDialect;
import com.github.mxab.thymeleaf.extras.dataattribute.dialect.DataAttributeDialect;
import com.google.common.cache.CacheBuilder;

import nz.net.ultraq.thymeleaf.LayoutDialect;

/*
 * A classe WebConfig é informada lá no AppInitializer como uma classe para ser
 * usada para buscar configurações do sistema.
 */
@Configuration
/*
 * Configura o pacote base para buscar os controllers. É usado a classe
 * CervejasController como base pq é melhor que passar uma String, pois em caso
 * de refatoração, não vai dar problema. E esse controller é o principal do
 * sistema, então ele sempre vai existir.
 */
@ComponentScan(basePackageClasses = { CervejasController.class })
/* Habilita os recursos Web do Spring. */
@EnableWebMvc
/*
 * Adiciona suporte a alguns recursos do SpringData, relacionadas a parte Web,
 * como por exemplo, habilita o suporte do Pageable.
 */
@EnableSpringDataWebSupport
/*
 * Habilita o cache no servidor, podendo deixar consultas na memória do
 * servidor, por exemplo. Preciso configurar o CacheManager.
 */
@EnableCaching
public class WebConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Bean
	public ViewResolver viewResolver() {
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(templateEngine());
		resolver.setCharacterEncoding("UTF-8");
		return resolver;
	}

	@Bean
	public TemplateEngine templateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setEnableSpringELCompiler(true);
		engine.setTemplateResolver(templateResolver());

		engine.addDialect(new LayoutDialect());
		/* Registra o nosso próprio dialeto. */
		engine.addDialect(new BrewerDialect());

		/* Lib para permitir adicionar atributos do tipo data de forma mais fácil. */
		engine.addDialect(new DataAttributeDialect());
		return engine;
	}

	private ITemplateResolver templateResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setApplicationContext(applicationContext);
		resolver.setPrefix("classpath:/templates/");
		resolver.setSuffix(".html");
		resolver.setTemplateMode(TemplateMode.HTML);
		return resolver;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
	}

	/** Permite implementar uns serviços de conversão customizados. */
	@Bean
	public FormattingConversionService mvcConversionService() {
		DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
		conversionService.addConverter(new EstiloConverter());
		conversionService.addConverter(new CidadeConverter());
		conversionService.addConverter(new EstadoConverter());

		/*
		 * Sempre informo o pattern de conversão no padrão internacional. Mas quando ele
		 * for converter, ele considera o idioma do usuário.
		 */
		NumberStyleFormatter numberStyleFormatter = new NumberStyleFormatter("#,##0.00");
		conversionService.addFormatterForFieldType(BigDecimal.class, numberStyleFormatter);

		NumberStyleFormatter integerFormatter = new NumberStyleFormatter("#,##0");
		conversionService.addFormatterForFieldType(Integer.class, integerFormatter);

		return conversionService;
	}

	/*
	 * Força o sistema a sempre usar o padrão do Brasil pras conversões,
	 * independente do Accept-language que o browser do usuário enviar.
	 */
	@Bean
	public LocaleResolver localeResolver() {
		return new FixedLocaleResolver(new Locale("pt", "BR"));
	}

	/*
	 * Configura o cache do servidor com o guava.
	 */
	@Bean
	public CacheManager cacheManager() {
		/*
		 * Troquei a implementação do ConcurrentMapCache pelo Guava, e não preciso mexer
		 * em mais nada, somente fornecer o novo ChacheManager.
		 */
		CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
				/* Configura quantas entidades eu quero em cache. */
				.maximumSize(3)
				/*
				 * Configura o tempo de validade do cache, sem ninguém usar. Se tiver alguém
				 * usando, o tempo fica resetando.
				 */
				.expireAfterAccess(20, TimeUnit.SECONDS);

		GuavaCacheManager cacheManager = new GuavaCacheManager();
		cacheManager.setCacheBuilder(cacheBuilder);
		return cacheManager;
	}

}
