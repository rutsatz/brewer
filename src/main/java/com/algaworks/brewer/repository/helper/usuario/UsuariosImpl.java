package com.algaworks.brewer.repository.helper.usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Grupo;
import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.model.UsuarioGrupo;
import com.algaworks.brewer.repository.filter.UsuarioFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

public class UsuariosImpl implements UsuariosQueries {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PaginacaoUtil paginacaoUtil;

	@Override
	public Optional<Usuario> porEmailEAtivo(String email) {
		return manager.createQuery("from Usuario where lower(email) = lower(:email) and ativo = true", Usuario.class)
				.setParameter("email", email).getResultList()
				/* Usa o stream e o findFirst para já retornar um Optional. */
				.stream().findFirst();
	}

	@Override
	public List<String> permissoes(Usuario usuario) {
		return manager.createQuery(
				"select distinct p.nome from Usuario u inner join u.grupos g inner join g.permissoes p where u = :usuario",
				String.class).setParameter("usuario", usuario).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Page<Usuario> filtrar(UsuarioFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);

		/*
		 * Faz um distinct dá tabela principal, nesse caso, Usuario. Como temos o join
		 * com os grupos de usuários, a consulta repete o mesmo usuário para grupos
		 * diferentes. Aí para eliminar esses usuários repetidos.
		 */
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		paginacaoUtil.preparar(criteria, pageable);

		adicionarFiltro(filtro, criteria);

		List<Usuario> filtrados = criteria.list();
		/*
		 * Como não consigo mais fazer o join dos grupos com o distinct por causa do
		 * limit da paginação, eu preciso inicializar os grupos separadamente. Ai uso o
		 * hibernate pra inicializar os grupos dos usuários que foram filtrados.
		 */
		filtrados.forEach(u -> Hibernate.initialize(u.getGrupos()));

		return new PageImpl<>(filtrados, pageable, total(filtro));
	}

	@Transactional(readOnly = true)
	@Override
	public Usuario buscarComGrupos(Long codigo) {

		Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);
		criteria.createAlias("grupos", "g", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("codigo", codigo));
		/*
		 * Como temos vários grupos, podem vir várias rows. Ai eu digo que quero agrupar
		 * em Usuario.
		 */
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		return (Usuario) criteria.uniqueResult();
	}

	/* Conta o total de linhas para o filtro informado. */
	private Long total(UsuarioFilter filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(UsuarioFilter filtro, Criteria criteria) {
		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}

			if (!StringUtils.isEmpty(filtro.getEmail())) {
				criteria.add(Restrictions.ilike("email", filtro.getEmail(), MatchMode.START));
			}

			/*
			 * Faz o join com os grupos. Isso é necessário para evitar o
			 * LazyInitializationException, pois os grupos são um relacionamento ToMany e
			 * qualquer coisa ToMany é lazy. E como é algo específico, não vou marcar como
			 * eager, vou fazer um join. Preciso colocar o LEFT_OUTER_JOIN, pois senão o
			 * erro do LazyException continua acontecendo. Isso pois com o LEFT o Hibernate
			 * já inicializa a lista Grupos. Se não colocar e deixar o INNER, ele não
			 * inicializa.
			 * 
			 * Foi removido esse join, pois ao adicionar a paginação, é adicionado um limit,
			 * que faz com que a consulta não veja todos os registros fazendo com que o
			 * distinct que adicionamos lá em cima não funcione. Terei que inicializar os
			 * grupos separadamente.
			 */
//			criteria.createAlias("grupos", "g", JoinType.LEFT_OUTER_JOIN);
			if (filtro.getGrupos() != null && !filtro.getGrupos().isEmpty()) {
				List<Criterion> subqueries = new ArrayList<>();
				for (Long codigoGrupo : filtro.getGrupos().stream().mapToLong(Grupo::getCodigo).toArray()) {
					/*
					 * É um DetachedCriteria por cauca que é uma consulta separada. Ai para cada
					 * grupo selecionado, eu vou adicionando esse consulta.
					 */
					DetachedCriteria dc = DetachedCriteria.forClass(UsuarioGrupo.class);
					dc.add(Restrictions.eq("id.grupo.codigo", codigoGrupo));
					/* Quero pegar somente o código do usuário. */
					dc.setProjection(Projections.property("id.usuario"));

					/* Adiciona na lista todas as subconsultas dos grupos do usuário. */
					subqueries.add(Subqueries.propertyIn("codigo", dc));
				}

				Criterion[] criterions = new Criterion[subqueries.size()];
				/*
				 * Adiciona junto aos filtros já existentes a lista de consultas que deve ser
				 * feito o in.
				 */
				criteria.add(Restrictions.and(subqueries.toArray(criterions)));
			}
		}
	}

}
