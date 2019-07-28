package com.algaworks.brewer.controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.algaworks.brewer.dto.PeriodoRelatorio;

@Controller
@RequestMapping("/relatorios")
public class RelatoriosController {

	@GetMapping("/vendasEmitidas")
	public ModelAndView relatorioVendasEmitidas() {
		ModelAndView mv = new ModelAndView("relatorio/RelatorioVendasEmitidas");
		mv.addObject(new PeriodoRelatorio());
		return mv;
	}

	/*
	 * Retornamos um ModelAndView pois o view resolver do jasper que vai gerar o pdf
	 * e nós precisamos passar pra ele o nome do relatório.
	 */
	@PostMapping("/vendasEmitidas")
	public ModelAndView gerarRelatorioVendasEmitidas(PeriodoRelatorio periodoRelatorio) {
		Map<String, Object> parametros = new HashMap<>();

		/*
		 * O nosso projeto está configurado com o converter de datas para a nova api do
		 * java, ou seja, usando o LocalDate. Mas no relatório, está configurado para o
		 * java.util.Date. Então preciso converter de LocalDate para o util.Date.
		 */
		Date dataInicio = Date.from(LocalDateTime.of(periodoRelatorio.getDataInicio(), LocalTime.of(0, 0, 0))
				.atZone(ZoneId.systemDefault()).toInstant());
		Date dataFim = Date.from(LocalDateTime.of(periodoRelatorio.getDataFim(), LocalTime.of(23, 59, 59))
				.atZone(ZoneId.systemDefault()).toInstant());

		/* Digo qual o formato do relatório. */
		parametros.put("format", "pdf");
		/* Passo os parâmetros que eu configurei no relatório. */
		parametros.put("data_inicio", dataInicio);
		parametros.put("data_fim", dataFim);

		/* Retorno o nome do relatorio e os parametros dele. */
		return new ModelAndView("relatorio_vendas_emitidas", parametros);
	}

}