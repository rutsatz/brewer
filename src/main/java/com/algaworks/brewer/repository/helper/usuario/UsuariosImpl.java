package com.algaworks.brewer.repository.helper.usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Grupo;
import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.model.UsuarioGrupo;
import com.algaworks.brewer.repository.filter.UsuarioFilter;

public class UsuariosImpl implements UsuariosQueries {

	@PersistenceContext
	private EntityManager manager;

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
	public List<Usuario> filtrar(UsuarioFilter filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);

		/*
		 * Faz um distinct dá tabela principal, nesse caso, Usuario. Como temos o join
		 * com os grupos de usuários, a consulta repete o mesmo usuário para grupos
		 * diferentes. Aí para eliminar esses usuários repetidos.
		 */
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		adicionarFiltro(filtro, criteria);

		return criteria.list();
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
			 * erro do LazyException continua acontecendo.
			 */
			criteria.createAlias("grupos", "g", JoinType.LEFT_OUTER_JOIN);
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
