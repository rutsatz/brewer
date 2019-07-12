package com.algaworks.brewer.repository.paginacao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * Anotado com @Component para ser um componente do Spring e o Spring conseguir
 * injetar esse bean em outras classes.
 */
@Component
public class PaginacaoUtil {

	public void preparar(Criteria criteria, Pageable pageable) {

		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistro = paginaAtual * totalRegistrosPorPagina;

		criteria.setFirstResult(primeiroRegistro);
		criteria.setMaxResults(totalRegistrosPorPagina);

		Sort sort = pageable.getSort();
		if (sort != null) {
			/*
			 * Eu posso ordenar por mais de um parâmetro, por isso é uma lista. No nosso
			 * caso, vai ter somente ordenação por um campo.
			 */
			Sort.Order order = sort.iterator().next();
			/* Retorna o nome do campo que deve ser ordenado. Ex.: nome, sku. */
			String property = order.getProperty();
			/*
			 * Precisamos traduzir para o order do Criteria. Agora usamos o order do
			 * Hibernate, para adicionar ao criteria dele.
			 */
			criteria.addOrder(order.isAscending() ? Order.asc(property) : Order.desc(property));
		}

	}

}
