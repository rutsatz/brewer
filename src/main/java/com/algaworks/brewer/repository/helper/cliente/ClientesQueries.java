package com.algaworks.brewer.repository.helper.cliente;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.filter.ClientesFilter;

public interface ClientesQueries {

	public Page<Cliente> filtrar(ClientesFilter filtro, Pageable pageable);

}
