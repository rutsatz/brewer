package com.algaworks.brewer.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

		/* Se informou a data, então tbm informou a hora. */
		if (venda.getDataEntrega() != null) {
			venda.setDataHoraEntrega(LocalDateTime.of(venda.getDataEntrega(),
			                /* Se usuário informou data mas não hora, seta a hora para meio dia. */
			                venda.getHorarioEntrega() != null ? venda.getHorarioEntrega() : LocalTime.NOON));
		}

		vendas.save(venda);
	}

}
