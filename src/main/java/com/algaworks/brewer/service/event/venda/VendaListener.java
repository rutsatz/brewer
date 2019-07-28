package com.algaworks.brewer.service.event.venda;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.repository.Cervejas;

@Component
public class VendaListener {

	@Autowired
	private Cervejas cervejas;

	/*
	 * Escuta o evento de venda emitida, para dar a baixa no estoque. Como o evento
	 * é emitido pelo método emitir do CadastroVendaService, eu já estou dentro de
	 * uma transação.
	 */
	@EventListener
	public void vendaEmitida(VendaEvent vendaEvent) {

		for (ItemVenda item : vendaEvent.getVenda().getItens()) {
			Cerveja cerveja = cervejas.findById(item.getCerveja().getCodigo()).get();
			cerveja.setQuantidadeEstoque(cerveja.getQuantidadeEstoque() - item.getQuantidade());
			cervejas.save(cerveja);
		}

	}

}
