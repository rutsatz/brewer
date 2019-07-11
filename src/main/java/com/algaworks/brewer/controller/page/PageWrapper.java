package com.algaworks.brewer.controller.page;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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

    /**
     * Método chamado pelo html para montar a utl com a ordenação.
     *
     * @param propriedade Campo da ordenação, enviado pelo html.
     * @return Url com os parâmetros de ordenação.
     */
    public String urlOrdenada(String propriedade) {
        /*
         * É criado um novo uriBuilder para a ordenação, apartir do uriBuilder do
         * request. Não posso aproveitar o mesmo uriBuilder do request pq os parâmertos
         * de paginação que adicionei no método acima serão mantidos, sendo que eu não
         * quero esse parametros. Ou seja, não vai atrapalhar a url da requisição.
         */
        UriComponentsBuilder uriBuilderOrder = UriComponentsBuilder.fromUriString(uriBuilder.build(true).encode().toUriString());

        /* Formata a url, ajustando o valor do sort. Ex.: sort=name,asc */
        String valorSort = String.format("%s,%s", propriedade, inverterDirecao(propriedade));

        return uriBuilderOrder.replaceQueryParam("sort", valorSort).build(true).encode().toUriString();
    }

    public String inverterDirecao(String propriedade) {
        /* Direção default. */
        String direcao = "asc";

        /* Se html tiver enviado uma direção, eu inverto, se não, mantém a default. */
        Sort.Order order = page.getSort() != null ? page.getSort().getOrderFor(propriedade) : null;
        if(order!= null) {
//            direcao = Sort.Direction.ASC.equals(order.getDirection()) ? "desc" : "asc";
            direcao = order.isAscending() ? "desc" : "asc";
        }

        return direcao;
    }

    /**
     * Verifica se a ordenação é descendente.
     *
     * @param propriedade Campo da ordenação a ser verificado.
     * @return true se for descendente.
     */
    public boolean descendente(String propriedade) {
        /* Se inverter a direção e for ascendente, então a direção atual é descendente. */
        return inverterDirecao(propriedade).equals("asc");
    }

    public boolean ordenada(String propriedade) {
        Sort.Order order = page.getSort() != null ? page.getSort().getOrderFor(propriedade) : null;

        /* Se não tem nenhuma ordenação, já sai. */
        if(order == null) {
            return false;
        }

        /* Se tem ordenação, verifica se o campo está ordenado. */
        return page.getSort().getOrderFor(propriedade) != null ? true : false;
    }
}
