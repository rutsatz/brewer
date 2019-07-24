package com.algaworks.brewer.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

	@GetMapping("/nova")
	public ModelAndView nova(Venda venda) {
		ModelAndView mv = new ModelAndView("venda/CadastroVenda");
//		mv.addObject("uuid", UUID.randomUUID().toString());
		venda.setUuid(UUID.randomUUID().toString());
		return mv;
	}

	@PostMapping("/nova")
	public ModelAndView salvar(Venda venda, RedirectAttributes attributes,
			@AuthenticationPrincipal UsuarioSistema usuarioSistema) {

		/* Seta o usuário que está logado como o usuário da venda. */
		venda.setUsuario(usuarioSistema.getUsuario());
		/* Seta os itens dessa venda. (Por isso precisa do uuid) */
		venda.adicionarItens(tabelaItens.getItens(venda.getUuid()));

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