package com.algaworks.brewer.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.controller.validator.VendaValidator;
import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.mail.Mailer;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Vendas;
import com.algaworks.brewer.repository.filter.VendaFilter;
import com.algaworks.brewer.security.UsuarioSistema;
import com.algaworks.brewer.service.CadastroVendaService;
import com.algaworks.brewer.session.TabelasItensSession;

@Controller
@RequestMapping("/vendas")
public class VendasController {

	@Autowired
	private Cervejas cervejas;

	@Autowired
	private TabelasItensSession tabelaItens;

	@Autowired
	private CadastroVendaService cadastroVendaService;

	@Autowired
	private VendaValidator vendaValidator;

	@Autowired
	private Vendas vendas;

	@Autowired
	private Mailer mailer;

	/**
	 * Método para inicializar os nossos validadores customizados. Preciso colocar a
	 * anottation @InitBinder para chamar esse método na inicialização do
	 * Controller, para adicionar o nosso validador para esse Controller. Assim,
	 * quando ele encontrar alguma Venda anotada com @Valid, ele vai executar as
	 * validações do nosso validador customizado.
	 *
	 * Como esse validador é para a Venda, e eu tenho o método de pesquisar, ele vai
	 * procurar um validador para o VendaFilter, mas não vai achar, pois ele vai
	 * analisando todos os métodos do controller. Então digo que ele deve aplicar a
	 * validação somente para os parametros "venda".
	 */
//	@InitBinder("venda")
//	public void inicializarValidador(WebDataBinder binder) {
//		binder.setValidator(vendaValidator);
//	}

	@GetMapping("/nova")
	public ModelAndView nova(Venda venda) {
		ModelAndView mv = new ModelAndView("venda/CadastroVenda");

		setUuid(venda);

		mv.addObject("itens", venda.getItens());
		/*
		 * Adiciona para poder recuperar no javascript e poder fazer os binds novamente
		 * e iniciar as variáveis com os valores corretos.
		 */
		mv.addObject("valorFrete", venda.getValorFrete());
		mv.addObject("valorDesconto", venda.getValorDesconto());
		mv.addObject("valorTotalItens", tabelaItens.getValorTotal(venda.getUuid()));
		return mv;
	}

	/**
	 * Adiciono o params, que indica que se tiver esse parâmetro na url, ele irá
	 * chamar esse método.
	 */
	@PostMapping(value = "/nova", params = "salvar")
	public ModelAndView salvar(Venda venda, BindingResult result, RedirectAttributes attributes,
			@AuthenticationPrincipal UsuarioSistema usuarioSistema) {

		validarVenda(venda, result);

		if (result.hasErrors()) {
			return nova(venda);
		}

		/* Seta o usuário que está logado como o usuário da venda. */
		venda.setUsuario(usuarioSistema.getUsuario());

		cadastroVendaService.salvar(venda);
		attributes.addFlashAttribute("mensagem", "Venda salva com sucesso");
		return new ModelAndView("redirect:/vendas/nova");
	}

	@PostMapping(value = "/nova", params = "emitir")
	public ModelAndView emitir(Venda venda, BindingResult result, RedirectAttributes attributes,
			@AuthenticationPrincipal UsuarioSistema usuarioSistema) {

		validarVenda(venda, result);

		if (result.hasErrors()) {
			return nova(venda);
		}

		/* Seta o usuário que está logado como o usuário da venda. */
		venda.setUsuario(usuarioSistema.getUsuario());

		cadastroVendaService.emitir(venda);
		attributes.addFlashAttribute("mensagem", "Venda emitida com sucesso");
		return new ModelAndView("redirect:/vendas/nova");
	}

	@PostMapping(value = "/nova", params = "enviarEmail")
	public ModelAndView enviarEmail(Venda venda, BindingResult result, RedirectAttributes attributes,
			@AuthenticationPrincipal UsuarioSistema usuarioSistema) {

		validarVenda(venda, result);
		if (result.hasErrors()) {
			return nova(venda);
		}

		/* Seta o usuário que está logado como o usuário da venda. */
		venda.setUsuario(usuarioSistema.getUsuario());

		/* Chamada assíncrona. */
		mailer.enviar(venda);

		/* Já pega o id da venda, para mandar no email. */
		venda = cadastroVendaService.salvar(venda);

		attributes.addFlashAttribute("mensagem",
				String.format("Venda nº %d salva com sucesso e e-mail enviado", venda.getCodigo()));
		return new ModelAndView("redirect:/vendas/nova");
	}

	/**
	 * Método chamado pelo javascript. Ele processa os itens da cerveja e já retorna
	 * para o JS o html pronto. Esse html é a lista do carrinho de compras.
	 */
	@PostMapping("/item")
	public ModelAndView adicionarItem(Long codigoCerveja, String uuid) {
		Cerveja cerveja = cervejas.getOne(codigoCerveja);
		tabelaItens.adicionarItem(uuid, cerveja, 1);

		return mvTabelaItensVenda(uuid);
	}

	@PutMapping("/item/{codigoCerveja}")
	public ModelAndView alterarQuantidadeItem(@PathVariable("codigoCerveja") Cerveja cerveja, Integer quantidade,
			String uuid) {

//		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItens.alterarQuantidadeItens(uuid, cerveja, quantidade);

		return mvTabelaItensVenda(uuid);
	}

	/*
	 * Configuro o JpaRepository para buscar a cerveja automaticamente para mim,
	 * através da variável codigoCerveja. Assim, não preciso fazer o
	 * cervejas.findOne(codigoCerveja);. Preciso configurar isso no WebConfig no
	 * método domainClassConverter.
	 *
	 * No DELETE eu não consigo passar parâmetros no corpo da requisição. Como eu
	 * preciso do uuid da tela, eu passo ela na url, diferentemente dos outros
	 * métodos.
	 */
	@DeleteMapping("/item/{uuid}/{codigoCerveja}")
	public ModelAndView excluirItem(@PathVariable("codigoCerveja") Cerveja cerveja, @PathVariable String uuid) {
//		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItens.excluirItem(uuid, cerveja);

		return mvTabelaItensVenda(uuid);
	}

	@GetMapping
	public ModelAndView pesquisar(VendaFilter vendaFilter, @PageableDefault(size = 3) Pageable pageable,
			HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("/venda/PesquisaVendas");
		mv.addObject("todosStatus", StatusVenda.values());
		mv.addObject("tiposPessoa", TipoPessoa.values());

		PageWrapper<Venda> paginaWrapper = new PageWrapper<>(vendas.filtrar(vendaFilter, pageable), httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		Venda venda = vendas.buscarComItens(codigo);

		setUuid(venda);
		for (ItemVenda item : venda.getItens()) {
			tabelaItens.adicionarItem(venda.getUuid(), item.getCerveja(), item.getQuantidade());
		}

		ModelAndView mv = nova(venda);
		mv.addObject(venda);
		return mv;
	}

	@PostMapping(value = "/nova", params = "cancelar")
	public ModelAndView cancelar(Venda venda, BindingResult result, RedirectAttributes attributes,
			@AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		try {
			cadastroVendaService.cancelar(venda);
		} catch (AccessDeniedException e) {
			ModelAndView mv = new ModelAndView("error");
			mv.addObject("status", 403);
			return mv;
		}

		attributes.addFlashAttribute("mensagem", "Venda cancelada com sucesso");
		return new ModelAndView("redirect:/vendas/" + venda.getCodigo());
	}

	@GetMapping("/totalPorMes")
	public @ResponseBody List<VendaMes> listarTotalVendaPorMes() {
		return vendas.totalPorMes();
	}

	private ModelAndView mvTabelaItensVenda(String uuid) {
		ModelAndView mv = new ModelAndView("venda/TabelaItensVenda");
		mv.addObject("itens", tabelaItens.getItens(uuid));
		mv.addObject("valorTotal", tabelaItens.getValorTotal(uuid));
		return mv;
	}

	private void validarVenda(Venda venda, BindingResult result) {
		/* Seta os itens dessa venda. (Por isso precisa do uuid) */
		venda.adicionarItens(tabelaItens.getItens(venda.getUuid()));

		/*
		 * Calcula o valor total para setar na venda e mandar novamente para a tela. Se
		 * não fizer isso, ele somente vai colocar o valor total quando eu adicionar ou
		 * excluir um item, pois ai o javascript faz uma chamada rest que o server
		 * processa, através no método mvTabelaItensVenda, e ai faz o append do html
		 * processador pelo thymeleaf.
		 */
		venda.calcularValorTotal();

		/*
		 * Como eu preciso terminar de montar o objeto venda, para ai validar, eu posso
		 * remover o @Valid lá do parâmetro e chamar o método validate do nosso
		 * validador customizado. E aí, eu passo a venda para ele, já com os itens
		 * setados. Ou seja, é nesse momento que ele vai realizar a validação, e não
		 * mais quando chegar no controller.
		 */
		vendaValidator.validate(venda, result);
	}

	private void setUuid(Venda venda) {
		/* Somente seta o UUID a primeira vez. */
		if (StringUtils.isEmpty(venda.getUuid())) {
//	        mv.addObject("uuid", UUID.randomUUID().toString());
			venda.setUuid(UUID.randomUUID().toString());
		}
	}

}