package com.algaworks.brewer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.helper.cerveja.CervejasQueries;

/**
 * Implemento minha interface de Queries. Dessa forma, o Spring vai implementar
 * também minhas consultas, que ele vai buscar através do nome. Assim, adiciono
 * minhas próprios consultas no repository.
 */
@Repository
public interface Cervejas extends JpaRepository<Cerveja, Long>, CervejasQueries {

}
