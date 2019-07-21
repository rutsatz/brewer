package com.algaworks.brewer.repository.helper.cerveja;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.algaworks.brewer.dto.CervejaDTO;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.filter.CervejaFilter;

/**
 * Adiciono os métodos que vou estar disponibilizando para serem adicionados no
 * repository. (A implementação eu forneço no CervejasImpl)
 */
public interface CervejasQueries {

	public Page<Cerveja> filtrar(CervejaFilter filtro, Pageable pageable);

	public List<CervejaDTO> porSkuOuNome(String skuOuNome);

}
