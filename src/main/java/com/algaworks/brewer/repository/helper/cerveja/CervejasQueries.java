package com.algaworks.brewer.repository.helper.cerveja;

import java.util.List;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.filter.CervejaFilter;

/**
 * Adiciono os métodos que vou estar disponibilizando para serem adicionados no
 * repository. (A implementação eu forneço no CervejasImpl)
 */
public interface CervejasQueries {

	public List<Cerveja> filtrar(CervejaFilter filtro);

}
