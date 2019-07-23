package com.algaworks.brewer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.session.TabelaItensVenda;

@Controller
@RequestMapping("/vendas")
public class VendasController {

	@Autowired
	private Cervejas cervejas;

	@Autowired
	private TabelaItensVenda tabelaItensVenda;

	@GetMapping("/nova")
	public String nova() {
		return "venda/CadastroVenda";
	}

	/**
	 * Método chamado pelo javascript. Ele processa os itens da cerveja e já retorna
	 * para o JS o html pronto. Esse html é a lista do carrinho de compras.
	 */
	@PostMapping("/item")
	public ModelAndView adicionarItem(Long codigoCerveja) {
		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItensVenda.adicionarItem(cerveja, 1);

		return mvTabelaItensVenda();
	}

	@PutMapping("/item/{codigoCerveja}")
	public ModelAndView alterarQuantidadeItem(@PathVariable("codigoCerveja") Cerveja cerveja, Integer quantidade) {

//		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItensVenda.alterarQuantidadeItens(cerveja, quantidade);

		return mvTabelaItensVenda();
	}

	/*
	 * Configuro o JpaRepository para buscar a cerveja automaticamente para mim,
	 * através da variável codigoCerveja. Assim, não preciso fazer o
	 * cervejas.findOne(codigoCerveja);. Preciso configurar isso no WebConfig no
	 * método domainClassConverter.
	 */
	@DeleteMapping("/item/{codigoCerveja}")
	public ModelAndView excluirItem(@PathVariable("codigoCerveja") Cerveja cerveja) {
//		Cerveja cerveja = cervejas.findOne(codigoCerveja);
		tabelaItensVenda.excluirItem(cerveja);

		return mvTabelaItensVenda();
	}

	private ModelAndView mvTabelaItensVenda() {
		ModelAndView mv = new ModelAndView("venda/TabelaItensVenda");
		mv.addObject("itens", tabelaItensVenda.getItens());
		return mv;
	}

}