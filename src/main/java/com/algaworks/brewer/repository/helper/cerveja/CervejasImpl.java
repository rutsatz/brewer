package com.algaworks.brewer.repository.helper.cerveja;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.filter.CervejaFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

/*
 * Implementação do meu filtro de Cervejas. O nome da classe deve ser esse. O
 * nome do meu JPARepository (Cervejas) seguido do sufixo Impl. Esse sufixo é o
 * padrão usado pelo Spring para procurar as implementações, mas eu poderia ir
 * no JPAConfig e dentro da annotation @EnableJpaRepositories, alterar o
 * comportamento padrão fornecendo um novo sufixo através do parâmetro
 * repositoryImplementationPostfix. Ai, todas as minhas classes teriam que
 * terminar com esse nome (Nome do repository + PostFix). Tenho que seguir a convenção.
 */
public class CervejasImpl implements CervejasQueries {

	/*
	 * Para injetar o EntityManager, eu uso o @PersistenceContext e não
	 * o @AutoWired.
	 */
	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PaginacaoUtil paginacaoUtil;

	/*
	 * Podemos adicionar o @suppressWarnings pois é somente por causa do return do
	 * criteria, que retorna um List sem nada, enquanto que o método retorna um
	 * List<Cerveja>. Então, ele dá o warning, mas é só por causa do Generics.
	 */
	@SuppressWarnings("unchecked")
	@Override
	/*
	 * Eu preciso do @Transaction por causa do unwrap do Session, pois como estamos
	 * injetando o Entity Manager, o Spring não sabe se deve abrir transação ou não.
	 * Mas como é uma transação somente para leitura, não precisar abrir uma
	 * transação com commit e rollback.
	 */
	@Transactional(readOnly = true)
	public Page<Cerveja> filtrar(CervejaFilter filtro, Pageable pageable) {

		/*
		 * Uso o unwrap de Session do hibernate, pois estou usando o criteria do
		 * Hibernate, então preciso tirar o Session do hibernate de dentro da JPA, pois
		 * contém métodos especificos do hibernate.
		 */
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cerveja.class);

		paginacaoUtil.preparar(criteria, pageable);

		adicionarFiltro(filtro, criteria);
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}

	/* Conta o total de linhas para o filtro informado. */
	private Long total(CervejaFilter filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cerveja.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(CervejaFilter filtro, Criteria criteria) {
		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getSku())) {
				criteria.add(Restrictions.eq("sku", filtro.getSku()));
			}
			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}
			if (isEstiloPresente(filtro)) {
				criteria.add(Restrictions.eq("estilo", filtro.getEstilo()));
			}
			if (filtro.getSabor() != null) {
				criteria.add(Restrictions.eq("sabor", filtro.getSabor()));
			}
			if (filtro.getOrigem() != null) {
				criteria.add(Restrictions.eq("origem", filtro.getOrigem()));
			}
			if (filtro.getValorDe() != null) {
				criteria.add(Restrictions.ge("valor", filtro.getValorDe()));
			}
			if (filtro.getValorAte() != null) {
				criteria.add(Restrictions.le("valor", filtro.getValorAte()));
			}
		}
	}

	private boolean isEstiloPresente(CervejaFilter filtro) {
		return filtro.getEstilo() != null && filtro.getEstilo().getCodigo() != null;
	}

}
