package com.algaworks.brewer.config;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import org.springframework.cache.CacheManager;
import javax.cache.Caching;

import org.springframework.beans.BeansException;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.TemplateEngine;
//import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.ISpringTemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.algaworks.brewer.config.format.BigDecimalFormatter;
import com.algaworks.brewer.controller.CervejasController;
import com.algaworks.brewer.controller.converter.CidadeConverter;
import com.algaworks.brewer.controller.converter.EstadoConverter;
import com.algaworks.brewer.controller.converter.EstiloConverter;
import com.algaworks.brewer.controller.converter.GrupoConverter;
import com.algaworks.brewer.session.TabelasItensSession;
import com.algaworks.brewer.thymeleaf.BrewerDialect;
import com.github.mxab.thymeleaf.extras.dataattribute.dialect.DataAttributeDialect;

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
 *
 * Estou adicionado para o ApplicationContext pesquisar, no contexto Web, as
 * classes dos pacotes controller e session.
 */
//@ComponentScan(basePackageClasses = { CervejasController.class, TabelasItensSession.class })
/* Habilita os recursos Web do Spring. */
//@EnableWebMvc
/*
 * Adiciona suporte a alguns recursos do SpringData, relacionadas a parte Web,
 * como por exemplo, habilita o suporte do Pageable.
 */
//@EnableSpringDataWebSupport
/*
 * Habilita o cache no servidor, podendo deixar consultas na memória do
 * servidor, por exemplo. Preciso configurar o CacheManager.
 */
@EnableCaching
/*
 * Habilita o Spring a fazer chamada assíncronas dos métodos anotados
 * com @Async.
 */
@EnableAsync
public class WebConfig implements WebMvcConfigurer {

//	private ApplicationContext applicationContext;
//
//	@Override
//	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//		this.applicationContext = applicationContext;
//	}

//	@Bean
//	public ViewResolver viewResolver() {
//		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
//		resolver.setTemplateEngine(templateEngine());
//		
//		resolver.setCharacterEncoding("UTF-8");
//
//		/*
//		 * Coloco o view resolver do Spring em segundo. Verifica primeiro o relatório.
//		 */
//		resolver.setOrder(1);
//
//		return resolver;
//	}

//	@Bean
//	public TemplateEngine templateEngine() {
//		SpringTemplateEngine engine = new SpringTemplateEngine();
//		engine.setEnableSpringELCompiler(true);
//		engine.setTemplateResolver(templateResolver());
//
//		engine.addDialect(new LayoutDialect());
//		/* Registra o nosso próprio dialeto. */
//		engine.addDialect(new BrewerDialect());
//		/*
//		 * Adiciona o dialeto do spring security extras do thymeleaf, que vem com alguns
//		 * recursos para trabalhar com o security, como por exemplo, mostrar o nome do
//		 * usuário logado. Eu posso pegar o usuário através do #authentication lá na
//		 * view, que é um objeto que essa lib adiciona pra gente.
//		 */
//		engine.addDialect(new SpringSecurityDialect());
//
//		/* Lib para permitir adicionar atributos do tipo data de forma mais fácil. */
//		engine.addDialect(new DataAttributeDialect());
//		return engine;
//	}

//	private ITemplateResolver templateResolver() {
//		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
//		resolver.setApplicationContext(applicationContext);
//		resolver.setPrefix("classpath:/templates/");
//		resolver.setSuffix(".html");
//		resolver.setTemplateMode(TemplateMode.HTML);
//		return resolver;
//	}

//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
//	}

//	/** Permite implementar uns serviços de conversão customizados. */
//	@Bean
//	public FormattingConversionService mvcConversionService() {
//		DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
//		conversionService.addConverter(new EstiloConverter());
//		conversionService.addConverter(new CidadeConverter());
//		conversionService.addConverter(new EstadoConverter());
//		conversionService.addConverter(new GrupoConverter());
//
//		/*
//		 * Sempre informo o pattern de conversão no padrão internacional. Mas quando ele
//		 * for converter, ele considera o idioma do usuário.
//		 *
//		 * Como ele utiliza o locale do usuário, enviado no header, vai dar problema,
//		 * pois a formatação dos números, eu quero manter no padrão brasileiro,
//		 * indiferente do idioma do usuário. Ai eu customizo criando meus próprios
//		 * formatters.
//		 */
////		NumberStyleFormatter numberStyleFormatter = new NumberStyleFormatter("#,##0.00");
////		conversionService.addFormatterForFieldType(BigDecimal.class, numberStyleFormatter);
////		NumberStyleFormatter integerFormatter = new NumberStyleFormatter("#,##0");
////		conversionService.addFormatterForFieldType(Integer.class, integerFormatter);
//
//		/* Crio meus próprios formatters para resolver os problemas acima. */
//		BigDecimalFormatter bigDecimalFormatter = new BigDecimalFormatter("#,##0.00");
//		conversionService.addFormatterForFieldType(BigDecimal.class, bigDecimalFormatter);
//		/* E para o integer, eu posso usar o mesmo, somente mudo o pattern. */
//		BigDecimalFormatter integerFormatter = new BigDecimalFormatter("#,##0");
//		conversionService.addFormatterForFieldType(BigDecimal.class, integerFormatter);
//
//		// API de datas do Java 8
//		/* Registra o conversor para campos do tipo LocalDate. */
//		DateTimeFormatterRegistrar dateTimeFormatter = new DateTimeFormatterRegistrar();
//		/*
//		 * Registra os conversores de Data e hora, para que possa recebê-los formatados
//		 * lá da minha view. Pois o input vai mandar a String no formato com a mascara
//		 * que está definida pelo maskmoney.
//		 */
//		dateTimeFormatter.setDateFormatter(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//		dateTimeFormatter.setTimeFormatter(DateTimeFormatter.ofPattern("HH:mm"));
//		/* Registro o formatador de datas no meu conversionService. */
//		dateTimeFormatter.registerFormatters(conversionService);
//
//		return conversionService;
//	}

	/*
	 * Força o sistema a sempre usar o padrão do Brasil pras conversões,
	 * independente do Accept-language que o browser do usuário enviar.
	 */
//	@Bean
//	public LocaleResolver localeResolver() {
//		return new FixedLocaleResolver(new Locale("pt", "BR"));
//	}

	/*
	 * Configura o cache do servidor com o guava.
	 */
	@Bean
	public CacheManager cacheManager() throws Exception {
//		/*
//		 * Troquei a implementação do ConcurrentMapCache pelo Guava, e não preciso mexer
//		 * em mais nada, somente fornecer o novo ChacheManager.
//		 */
//		CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
//				/* Configura quantas entidades eu quero em cache. */
//				.maximumSize(3)
//				/*
//				 * Configura o tempo de validade do cache, sem ninguém usar. Se tiver alguém
//				 * usando, o tempo fica resetando.
//				 */
//				.expireAfterAccess(20, TimeUnit.SECONDS);
//
//		GuavaCacheManager cacheManager = new GuavaCacheManager();
//		cacheManager.setCacheBuilder(cacheBuilder);
//		return cacheManager;

		/*
		 * JCache é a especificação de Cache do JEE e o EhCache é uma de suas
		 * implementações. (Tipo JPA e Hibernate).
		 *
		 * Com essa configuração, a Api de Cache (JCache) vai encontrar a implementação
		 * do EhCache. A API faz isso de forma automática pra gente, pois o EhCache
		 * implementa a especificação do JCache. E as configurações eu passo por
		 * parâmetro para o getCacheManager, através de um arquivo de conf. E passo um
		 * segundo parâmetro, o ClassLoader, que é pra ele conseguir encontrar o nosso
		 * arquivo.
		 */
		return new JCacheCacheManager(Caching.getCachingProvider()
				.getCacheManager(getClass().getResource("/env/ehcache.xml").toURI(), getClass().getClassLoader()));
	}

//	@Bean
//	public MessageSource messageSource() {
//		ReloadableResourceBundleMessageSource bundle = new ReloadableResourceBundleMessageSource();
//		/* Configura para buscar umas mensagens. Não precisa colocar o .properties. */
//		bundle.setBasename("classpath:/messages");
//		bundle.setDefaultEncoding("UTF-8"); // https://www.utf8-chartable.de/
//		return bundle;
//	}

	/**
	 * Configura a integração das minhas classes de domínio (meu model), para
	 * converter diretamente para as classes, deixando o JpaRepository buscar os
	 * dados automaticamente do banco. Por exemplo: Ao invés de eu receber o código
	 * da cerveja por parâmetro e fazer um cervejas.findOne(codigoCerveja);, eu digo
	 * que estou recebendo o código de uma cerveja e ele já faz isso pra mim. Mas
	 * pra isso funcionar, eu preciso configurar esse método.
	 */
//	@Bean
//	public DomainClassConverter<FormattingConversionService> domainClassConverter() {
//		return new DomainClassConverter<FormattingConversionService>(mvcConversionService());
//	}

	/*
	 * Para poder usar as mensagens de internacionalização no BeanValidator, preciso
	 * adicionar esse Bean e sobrescrever o método getValidator().
	 */
//	@Bean
//	public LocalValidatorFactoryBean validator() {
//		LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
//		/* Dizemos onde que estão as mensagens de validação. */
//		validatorFactoryBean.setValidationMessageSource(messageSource());
//
//		return validatorFactoryBean;
//	}

	/*
	 * Sobrescreve esse método para configurar os arquivos de internacionalização,
	 * para ser possível internacionalizar as mensagens de validação do
	 * BeanValidation.
	 */
//	@Override
//	public Validator getValidator() {
//		return validator();
//	}

}
