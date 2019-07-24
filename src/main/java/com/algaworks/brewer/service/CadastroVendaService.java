package com.algaworks.brewer.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Vendas;

@Service
public class CadastroVendaService {

	@Autowired
	private Vendas vendas;

	@Transactional
	public void salvar(Venda venda) {

		if (venda.isNova()) {
			venda.setDataCriacao(LocalDateTime.now());
		}

		/*
		 * Percorre a lista de itens, somando o valor total de cada item. Não preciso
		 * validar se tem os itens e talz, pois quando chegar aqui, já vai estar válido,
		 * pois eu valido lá no controller.
		 */
		BigDecimal valorTotalItens = venda.getItens().stream().map(ItemVenda::getValorTotal).reduce(BigDecimal::add)
				.get();

		BigDecimal valorTotal = calcularValorTotal(valorTotalItens, venda.getValorFrete(), venda.getValorDesconto());
		venda.setValorTotal(valorTotal);

		/* Se informou a data, então tbm informou a hora. */
		if (venda.getDataEntrega() != null) {
			venda.setDataHoraEntrega(LocalDateTime.of(venda.getDataEntrega(), venda.getHorarioEntrega()));
		}

		vendas.save(venda);
	}

	private BigDecimal calcularValorTotal(BigDecimal valorTotalItens, BigDecimal valorFrete, BigDecimal valorDesconto) {
		/* Calculo o total da venda. (Total itens + frete - desconto) */
		BigDecimal valorTotal = valorTotalItens
				/*
				 * Nesse caso, o valor do frete e do desconto não são obrigatórios, então eu
				 * preciso validar. Então eu posso usar o Optional. Estou dizendo: Se não for
				 * null, usa o valor, senão, usa 0.
				 */
				.add(Optional.ofNullable(valorFrete).orElse(BigDecimal.ZERO))
				.subtract(Optional.ofNullable(valorDesconto).orElse(BigDecimal.ZERO));
		return valorTotal;
	}

}
