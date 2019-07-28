package com.algaworks.brewer.config;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import javax.sql.DataSource;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.number.NumberStyleFormatter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsViewResolver;

import com.algaworks.brewer.controller.CervejasController;
import com.algaworks.brewer.session.TabelasItensSession;

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
@ComponentScan(basePackageClasses = { CervejasController.class, TabelasItensSession.class })
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
/*
 * Habilita o Spring a fazer chamada assíncronas dos métodos anotados
 * com @Async.
 */
@EnableAsync
public class WebConfig extends WebMvcConfigurerAdapter {

	/*
	 * Adiciona um novo ViewResolver para o JasperReport. Ele recebe um DataSouce do
	 * javax.sql.
	 */
	@Bean
	public ViewResolver jasperReportsViewResolver(DataSource dataSource) {
		/* Objeto fornecido pelo Spring, pois ele já faz uma integração com o Jasper. */
		JasperReportsViewResolver resolver = new JasperReportsViewResolver();

		/*
		 * Dá mesma forma que digo onde estão os templates do thymeleaf, preciso dizer
		 * onde estão os relatórios.
		 */
		resolver.setPrefix("classpath:/relatorios/");
		resolver.setSuffix(".jasper");
		/*
		 * Adicionei mais um view resolver, ai agora tem dois, o do thymeleaf e o do
		 * jasper. E como o Spring vai saber qual que ele deve usar? Para isso, eu
		 * configuro o view name abaixo, pois através dele o Spring consegue escolher.
		 * Então todos os templates que derem match nessa view name, ele vai usar o
		 * Jasper. Ai os relatorios eu sempre coloco o prefixo relatorio_.
		 */
		resolver.setViewNames("relatorio_*");

		resolver.setViewClass(JasperReportsMultiFormatView.class);

		resolver.setJdbcDataSource(dataSource);

		/*
		 * Como agora tenho dois view resolver, preciso configurar uma ordem, para dizer
		 * qual o spring deve avaliar primeiro. Como o relatório é a exceção, coloco ele
		 * por primeiro. Ai se não der match aqui, vai pro próximo.
		 */
		resolver.setOrder(0);

		return resolver;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		NumberStyleFormatter bigDecimalFormatter = new NumberStyleFormatter("#,##0.00");
		registry.addFormatterForFieldType(BigDecimal.class, bigDecimalFormatter);

		DateTimeFormatterRegistrar dateTimeFormatter = new DateTimeFormatterRegistrar();
		dateTimeFormatter.setDateFormatter(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		dateTimeFormatter.setTimeFormatter(DateTimeFormatter.ofPattern("HH:mm"));
		dateTimeFormatter.registerFormatters(registry);
	}

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
//	@Bean
//	public CacheManager cacheManager() {
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
//	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource bundle = new ReloadableResourceBundleMessageSource();
		/* Configura para buscar umas mensagens. Não precisa colocar o .properties. */
		bundle.setBasename("classpath:/messages");
		bundle.setDefaultEncoding("UTF-8"); // https://www.utf8-chartable.de/
		return bundle;
	}

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
	@Bean
	public LocalValidatorFactoryBean validator() {
		LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
		/* Dizemos onde que estão as mensagens de validação. */
		validatorFactoryBean.setValidationMessageSource(messageSource());

		return validatorFactoryBean;
	}

	/*
	 * Sobrescreve esse método para configurar os arquivos de internacionalização,
	 * para ser possível internacionalizar as mensagens de validação do
	 * BeanValidation.
	 */
	@Override
	public Validator getValidator() {
		return validator();
	}

}
