package com.algaworks.brewer.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.validator.VendaValidator;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Cervejas;
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
	
	/** Método para inicializar os nossos validadores customizados.
	 * Preciso colocar a anottation @InitBinder para chamar esse método na inicialização do Controller,
	 * para adicionar o nosso validador para esse Controller. Assim, quando ele encontrar alguma
	 * Venda anotada com @Valid, ele vai executar as validações do nosso validador customizado. */
	@InitBinder
	public void inicializarValidador(WebDataBinder binder) {
	    binder.setValidator(vendaValidator);
	}
	
	@GetMapping("/nova")
	public ModelAndView nova(Venda venda) {
		ModelAndView mv = new ModelAndView("venda/CadastroVenda");
		
		/* Somente seta o UUID a primeira vez. */
		if(StringUtils.isEmpty(venda.getUuid())) {
//	        mv.addObject("uuid", UUID.randomUUID().toString());
		    venda.setUuid(UUID.randomUUID().toString());
		}

		mv.addObject("itens", venda.getItens());
		/* Adiciona para poder recuperar no javascript e poder fazer os binds novamente e iniciar as variáveis
		 * com os valores corretos. */
		mv.addObject("valorFrete", venda.getValorFrete());
		mv.addObject("valorDesconto", venda.getValorDesconto());
		mv.addObject("valorTotalItens", tabelaItens.getValorTotal(venda.getUuid()));
		return mv;
	}

	@PostMapping("/nova")
	public ModelAndView salvar(Venda venda, BindingResult result, RedirectAttributes attributes,
			@AuthenticationPrincipal UsuarioSistema usuarioSistema) {

	    /* Seta os itens dessa venda. (Por isso precisa do uuid) */
        venda.adicionarItens(tabelaItens.getItens(venda.getUuid()));
	    
        /* Calcula o valor total para setar na venda e mandar novamente para a tela. Se não fizer isso,
         * ele somente vai colocar o valor total quando eu adicionar ou excluir um item, pois ai o javascript
         * faz uma chamada rest que o server processa, através no método mvTabelaItensVenda, e ai faz
         * o append do html processador pelo thymeleaf. */
        venda.calcularValorTotal();
        
	    /* Como eu preciso terminar de montar o objeto venda, para ai validar, eu posso remover o @Valid
	     * lá do parâmetro e chamar o método validate do nosso validador customizado. E aí, eu passo a
	     * venda para ele, já com os itens setados. Ou seja, é nesse momento que ele vai realizar a
	     * validação, e não mais quando chegar no controller.*/
	    vendaValidator.validate(venda, result);

	    
	    if(result.hasErrors()) {
	        return nova(venda);
	    }
	    
		/* Seta o usuário que está logado como o usuário da venda. */
		venda.setUsuario(usuarioSistema.getUsuario());

		cadastroVendaService.salvar(venda);
		attributes.addFlashAttribute("mensagem", "Venda salva com sucesso");
		return new ModelAndView("redirect:/vendas/nova");
	}

	/**
	 * Método chamado pelo javascript. Ele processa os itens da cerveja e já retorna
	 * para o JS o html pronto. Esse html é a lista do carrinho de compras.
	 */
	@PostMapping("/item")
	public ModelAndView adicionarItem(Long codigoCerveja, String uuid) {
		Cerveja cerveja = cervejas.findOne(codigoCerveja);
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

	private ModelAndView mvTabelaItensVenda(String uuid) {
		ModelAndView mv = new ModelAndView("venda/TabelaItensVenda");
		mv.addObject("itens", tabelaItens.getItens(uuid));
		mv.addObject("valorTotal", tabelaItens.getValorTotal(uuid));
		return mv;
	}

}