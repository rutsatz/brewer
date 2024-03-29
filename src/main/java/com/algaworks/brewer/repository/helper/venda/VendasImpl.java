package com.algaworks.brewer.repository.helper.venda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.filter.VendaFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

public class VendasImpl implements VendasQueries {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PaginacaoUtil paginacaoUtil;

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Page<Venda> filtrar(VendaFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Venda.class);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);

		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}

	@Transactional(readOnly = true)
	@Override
	public Venda buscarComItens(Long codigo) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Venda.class);
		criteria.createAlias("itens", "i", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("codigo", codigo));
		/*
		 * Como temos vários grupos, podem vir várias rows. Ai eu digo que quero agrupar
		 * em Usuario.
		 */
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		return (Venda) criteria.uniqueResult();
	}

    @Transactional(readOnly = true)
    @Override
    public BigDecimal valorTotalNoAno() {
        /* Usa o optional porque vai que retorna 0. */
        Optional<BigDecimal> optional = Optional.ofNullable(
                        manager.createQuery("select sum(valorTotal) from Venda where year(dataCriacao) = :ano and status = :status", BigDecimal.class)
                                        .setParameter("ano", Year.now().getValue())
                                        .setParameter("status", StatusVenda.EMITIDA)
                                        .getSingleResult());
        /* Se não encontrar nada, ai retorna 0. */
        return optional.orElse(BigDecimal.ZERO);
    }
    
    @Override
    public BigDecimal valorTotalNoMes() {
        /* Usa o optional porque vai que retorna 0. */
        Optional<BigDecimal> optional = Optional.ofNullable(
                        manager.createQuery("select sum(valorTotal) from Venda where month(dataCriacao) = :mes and status = :status", BigDecimal.class)
                                        .setParameter("mes", MonthDay.now().getMonthValue())
                                        .setParameter("status", StatusVenda.EMITIDA)
                                        .getSingleResult());
        /* Se não encontrar nada, ai retorna 0. */
        return optional.orElse(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal valorTicketMedioNoAno() {
        /* Usa o optional porque vai que retorna 0. */
        Optional<BigDecimal> optional = Optional.ofNullable(
                        manager.createQuery("select sum(valorTotal)/count(*) from Venda where year(dataCriacao) = :ano and status = :status", BigDecimal.class)
                                        .setParameter("ano", Year.now().getValue())
                                        .setParameter("status", StatusVenda.EMITIDA)
                                        .getSingleResult());
        /* Se não encontrar nada, ai retorna 0. */
        return optional.orElse(BigDecimal.ZERO);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<VendaMes> totalPorMes() {
        /* A NamedQuery é a que defini lá no consultas-nativas.xml. Lá no JpaConfig, eu ensinei pra ele
         * onde buscar esse arquivo. Se eu quiser, posso passar parâmetros para a NamedQuery igual faço com
         * jpql, chamando o método setParameter(). */
        List<VendaMes> vendasMes = manager.createNamedQuery("Vendas.totalPorMes").getResultList();

        /* Tratamento para caso existir um mês que não tiver nenhuma venda. Aí o grafico vai ficar errado. */
        LocalDate hoje = LocalDate.now();
        /* Vai de 1 a 6, pois a consulta é dos últimos 6 meses. */
        for (int i = 1; i <= 6; i++) {
            /* Recria o label retornado da consulta, no mesmo formato (2016/08), para ver se esse mes existe. */
            String mesIdeal = String.format("%d/%02d", hoje.getYear(), hoje.getMonthValue());

            /* Percorre a lista, verificando se o mes existe. */
            boolean possuiMes = vendasMes.stream().filter(v -> v.getMes().equals(mesIdeal)).findAny().isPresent();
            /* Ai, se não possuir venda naquele mês, adiciona uma venda zerada, já na posição correta. */
            if(!possuiMes) {
                vendasMes.add(i-1, new VendaMes(mesIdeal, 0));
            }

            /* A cada iteração, decrementa um mês, até verificar se todos existem. */
            hoje = hoje.minusMonths(1);
        }

        return vendasMes;
    }

	private Long total(VendaFilter filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Venda.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(VendaFilter filtro, Criteria criteria) {
		criteria.createAlias("cliente", "c");

		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getCodigo())) {
				criteria.add(Restrictions.eq("codigo", filtro.getCodigo()));
			}

			if (filtro.getStatus() != null) {
				criteria.add(Restrictions.eq("status", filtro.getStatus()));
			}

			if (filtro.getDesde() != null) {
				LocalDateTime desde = LocalDateTime.of(filtro.getDesde(), LocalTime.of(0, 0));
				criteria.add(Restrictions.ge("dataCriacao", desde));
			}

			if (filtro.getAte() != null) {
				LocalDateTime ate = LocalDateTime.of(filtro.getAte(), LocalTime.of(23, 59));
				criteria.add(Restrictions.le("dataCriacao", ate));
			}

			if (filtro.getValorMinimo() != null) {
				criteria.add(Restrictions.ge("valorTotal", filtro.getValorMinimo()));
			}

			if (filtro.getValorMaximo() != null) {
				criteria.add(Restrictions.le("valorTotal", filtro.getValorMaximo()));
			}

			if (!StringUtils.isEmpty(filtro.getNomeCliente())) {
				criteria.add(Restrictions.ilike("c.nome", filtro.getNomeCliente(), MatchMode.ANYWHERE));
			}

			if (!StringUtils.isEmpty(filtro.getCpfOuCnpjCliente())) {
				criteria.add(
						Restrictions.eq("c.cpfOuCnpj", TipoPessoa.removerFormatacao(filtro.getCpfOuCnpjCliente())));
			}
		}
	}

}