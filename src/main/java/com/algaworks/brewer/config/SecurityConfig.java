package com.algaworks.brewer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.algaworks.brewer.security.AppUserDetailsService;

@EnableWebSecurity
/*
 * Adicionado para o Spring encontrar nossa implementação do UserDetailsService,
 * que ele usa para fazer o login através da nossa busca de usuário.
 */
@ComponentScan(basePackageClasses = AppUserDetailsService.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		/* Usuário em memória, para testes. */
//		auth.inMemoryAuthentication().withUser("admin").password("admin").roles("CADASTRO_CLIENTE");

		/*
		 * Configuramos a forma de autenticação. Estamos fornecendo o serviço de
		 * autenticação, que é o userDetailsService que nós implementamos, e que
		 * adicionamos a regra de buscar o usuários no banco de dados. E também
		 * precisamos passar o encoder que usamos para criptografar a senha, se não o
		 * SpringSecurity vai achar que é texto puro, mas no banco está criptografado.
		 * Eu somente vou buscar no banco, pelo email e se está ativo, mas vai ser o
		 * Spring que vai fazer a comparação da senha.
		 */
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web
				/*
				 * Posso ignorar a autenticação de algumas urls. Ao invés de ficar adicionando
				 * as exceções no authorizeRequests do configure do HttpSecurity, eu posso
				 * adicionar todas elas diretamente aqui.
				 */
				.ignoring().antMatchers("/layout/**").antMatchers("/images/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
				/* Eu quero autorizar requisições. */
				.authorizeRequests()
				/* Adiciono uma configuração de permissão. Estou dizendo que para acessar o cadastro de cidades (/cidades/nova)
				  eu preciso ter a permissão de CADASTRAR_CIDADE. Essa é a coluna nome cadastrada na tabela permissão.
				 Preciso cuidar a ordem. Se eu colocasse essa linha abaixo, depois do .anyRequest().authenticated(), ele
				 iria liberar acesso sempre, mesmo sem ter a role, pois o SpringSecurity vai avaliando na sequência que eu defino. Então
				 ele viria e olharia que ele precisa verificar as autorizações e depois olharia que para qualquer requisição,
				 ele precisa estar autenticado. Aí, opa! Está autenticado, pode liberar. Ele nem avaliaria essa nossa regra que
				 está depois, pois ele encontrou um match antes e liberou o acesso.

				 Por isso preciso definir primeiro o que quero bloquear, e por fim deixar liberado o resto.

				 E se for usar o hasRole(), preciso salvar no banco de dados as permissões com o prefixo ROLE_. Se não quiser
				 salvar no banco com esse prefixo, ai eu tenho que usar outro método, o hasAuthority().
				 */
				.antMatchers("/cidades/nova").hasRole("CADASTRAR_CIDADE")
				/* Do usuário pra frente, precisa ter a role de cadastro de usuário. Essa é uma expressão ANT. Aí tanto o cadastro
				  quanto a pesquisa precisam dessa role. */
				.antMatchers("/usuarios/**").hasRole("CADASTRAR_USUARIO")
				/*
				 * Permite qualquer um acessar os arquivos do "/layout" para frente, pois na
				 * tela de login ele precisa carregar esses arquivos. Porém se ele tentar
				 * carregar os ".css" e ".js" do layout sem ter essas urls liberadas, vai dar
				 * erro, pois ao requisitar os arquivos, a requisição vai ser redirecionada para
				 * a página de login. Aí, ao invés de vir um arquivo ".js", por exemplo, vai vir
				 * o html da página de login. Então pra poder acessar o login sem problemas, eu
				 * libero os arquivos do layout. E o layout também não tem nenhum problema em
				 * liberar sem estar logado.
				 *
				 * Cuidar para a ordem, pois faz toda a diferença. Se eu colocar esse matcher
				 * pra baixo já não vai funcionar. Ele funciona assim: Eu adiciono todas as urls
				 * que quero liberar e quando chamo o anyRequest e authenticated, ele bloquea
				 * todas as demais requisições que não foram liberadas.
				 */
//				.antMatchers("/layout/**").permitAll() // Tirado daqui e colocado no configure do WebSecurity
				/*
				 * Para as imagens a mesma coisa. Eu tenho o logo na tela de login, mas ele está
				 * dentro da pasta images, e não dentro do layout. Então tenho que liberar tbm.
				 */
//				.antMatchers("/images/**").permitAll() // Tirado daqui e colocado no configure do WebSecurity
				/*
				 * Quais requisições? Qualquer uma.
				 */
				.anyRequest()
				/*
				 * Para qualquer uma eu preciso estar autenticado (Não preciso de uma permissão
				 * especial, como vendedor ou administrador, somente preciso ter logado com
				 * usuário e senha).
				 */
				.authenticated()
				/* Se eu quiser bloquear qualquer outra página que tenha sobra, ao invés de liberar, eu tiro o .authenticated()
				  e coloco o .denyAll(). Eu uso um ou outro, se deixar os dois .anyRequest(), pode ser que de problema no SpringSecutiry. */
//				.denyAll()
				/* Volto para o objeto anterior e posso continuar configurando. */
				.and()
				/* Habilito o form de login. */
				.formLogin()
				/*
				 * Digo a url do formulário de login. (Se eu não chamar esse método, o Spring
				 * gera uma página default)
				 */
				.loginPage("/login")
				/*
				 * Na tela de login, permite o acesso de qualquer pessoa. Se eu não fizer isso,
				 * quando for acessar o "/login", vai cair no authenticated lá de cima que vai
				 * ver que para qualquer url eu preciso estar autenticado. Ai qual é a página de
				 * login? "/login". Então manda para o "/login". Mas eu não estou autenticado
				 * para poder acessar a url "/login", então como eu autentico? No "/login". Ou
				 * seja, ele entra nesse loop infinito. Por isso, para o "/login", eu preciso
				 * permitir o acesso de qualquer um, sem autenticação.
				 */
				.permitAll()
				/* Volto para o objeto anterior. */
				.and()
				/* Configura o logout do sistema. */
				.logout()
				    /* Com o CSRF habilitado, preciso liberar o /logout para qualquer usuário, pois pro logout não tem problema. 
				     * Caso contrário, vai dar 404 se tentar fazer logout com o CSRF habilitado. */
				    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				    .and()
				/* Faz o tratamento das exceções, como acesso negado, por exemplo. */
				.exceptionHandling()
				    /* Adiciona a url que o usuário deve ser encaminhado em caso de acesso negado. Eu preciso ter essa
				      página mapeada. (Colocamos no SegurancaController)*/
				    .accessDeniedPage("/403");

				/* Desabilito o CSRF. É muito importante. O default é habilitado. Foi desabilitado somente para desenvolvimento. */
//				.and().csrf().disable();
	}

	/* Configura o gerador de senhas. Usado para criptografar a senha do usuário. */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
