//package com.algaworks.brewer.config;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//
//import javax.persistence.EntityManagerFactory;
//import javax.sql.DataSource;
//
//import org.apache.commons.dbcp2.BasicDataSource;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.JpaVendorAdapter;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.vendor.Database;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import com.algaworks.brewer.model.Cerveja;
//import com.algaworks.brewer.repository.Cervejas;
//
//@Configuration
///*
// * Também busca as classes utilitárias para usar com a JPA. Ex: PaginacaoUtil.
// */
//@ComponentScan(basePackageClasses = Cervejas.class)
///*
// * Busca os repositories, que são classes anotadas com o repository e que
// * extendem o JpaRepository.
// */
//@EnableJpaRepositories(basePackageClasses = Cervejas.class, enableDefaultTransactions = false)
//@EnableTransactionManagement
//public class JPAConfig {
//
//	/*
//	 * Posso anotar beans com o profile tbm. Quero esse bean somente nesse profile.
//	 */
//	@Profile("local")
//	/*
//	 * Interessante quando formos fazer deploy em tomcat externo, pois lemos o
//	 * arquivo context do tomcat.
//	 */
//	@Bean
//	public DataSource dataSource() {
//		JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
//		dataSourceLookup.setResourceRef(true);
//		/* Recuperamos através do JNDI name. */
//		return dataSourceLookup.getDataSource("jdbc/brewerDB");
//	}
//
//	/* Como estamos usando servidor na nuvem, vamos usar os profiles. */
//	@Profile("prod")
//	/*
//	 * Interessante quando formos fazer deploy em tomcat externo, pois lemos o
//	 * arquivo context do tomcat.
//	 */
//	@Bean
//	public DataSource dataSourceProd() throws URISyntaxException {
//
//		/*
//		 * Essa parte foi pego da documentação do HEROKU. Ele pega os dados dessa
//		 * variável de ambiente.
//		 */
//		URI dbUri = new URI(System.getenv("DATABASE_URL"));
//
//		String username = dbUri.getUserInfo().split(":")[0];
//		String password = dbUri.getUserInfo().split(":")[1];
//		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath()
//				+ "?sslmode=require";
//
//		/*
//		 * Com posse dos dados, configuro meu dataSource. BasicDataSource lá do apache
//		 * commons dbcp2.
//		 */
//		BasicDataSource dataSource = new BasicDataSource();
//		dataSource.setUrl(dbUrl);
//		dataSource.setUsername(username);
//		dataSource.setPassword(password);
//		/* Configuro o tamanho do pool de conexões. */
//		dataSource.setInitialSize(10);
//		return dataSource;
//	}
//
//	@Bean
//	public JpaVendorAdapter jpaVendorAdapter() {
//		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
//		adapter.setDatabase(Database.POSTGRESQL);
//		adapter.setShowSql(false);
//		adapter.setGenerateDdl(false);
//		adapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");
//		return adapter;
//	}
//
//	@Bean
//	public EntityManagerFactory entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
//		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
//		factory.setDataSource(dataSource);
//		factory.setJpaVendorAdapter(jpaVendorAdapter);
//		factory.setPackagesToScan(Cerveja.class.getPackage().getName());
//
//		/*
//		 * Configura o arquivo para buscar as consultas nativas através do named query.
//		 */
//		factory.setMappingResources("sql/consultas-nativas.xml");
//
//		factory.afterPropertiesSet();
//		return factory.getObject();
//	}
//
//	@Bean
//	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
//		JpaTransactionManager transactionManager = new JpaTransactionManager();
//		transactionManager.setEntityManagerFactory(entityManagerFactory);
//		return transactionManager;
//	}
//}
