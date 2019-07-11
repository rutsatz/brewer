package com.algaworks.brewer.controller.page;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

public class PageWrapper<T> {

	private Page<T> page;

	private UriComponentsBuilder uriBuilder;

	public PageWrapper(Page<T> page, HttpServletRequest httpServletRequest) {
		this.page = page;
		this.uriBuilder = ServletUriComponentsBuilder.fromRequest(httpServletRequest);
	}

	public List<T> getConteudo() {
		return page.getContent();
	}

	public boolean isVazia() {
		return page.getContent().isEmpty();
	}

	public int getAtual() {
		return page.getNumber();
	}

	public boolean isPrimeira() {
		return page.isFirst();
	}

	public boolean isUltima() {
		return page.isLast();
	}

	public int getTotal() {
		return page.getTotalPages();
	}

	/**
	 * Método usado na view para gerar as urls para a paginação.
	 *
	 * @param pagina Número da nova página.
	 * @return Url apontando para essa página (Matendo os parâmetros da requisição
	 *         original, como os filtros, por exemplo).
	 */
	public String urlParaPagina(int pagina) {
		/*
		 * Se tive o parâmetro page na requisição, substitui ele pelo número da página.
		 * Se não tiver o parâmetro, ele adiciona.
		 *
		 * O build(true) e o encode são usado para tratar número decimais na url, pois o
		 * browser converte em, por exemplo, 3%2C0. Aí na hora do browser receber a
		 * resposta e montar novamente o layout, os valores vem errados.
		 */
		return uriBuilder.replaceQueryParam("page", pagina).build(true).encode().toUriString();
	}
}
