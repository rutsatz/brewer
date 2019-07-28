package com.algaworks.brewer.service;

import java.io.InputStream;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algaworks.brewer.dto.PeriodoRelatorio;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Service
public class RelatorioService {

	/* Conexão do banco, para passar ao Jasper. */
	@Autowired
	private DataSource dataSource;

	/*
	 * Método foi alterado para usar a api do jasper diretamente, pois a integração
	 * do Spring foi removida.
	 */
	public byte[] gerarRelatorioVendasEmitidas(PeriodoRelatorio periodoRelatorio) throws Exception {

		/*
		 * O nosso projeto está configurado com o converter de datas para a nova api do
		 * java, ou seja, usando o LocalDate. Mas no relatório, está configurado para o
		 * java.util.Date. Então preciso converter de LocalDate para o util.Date.
		 */
		Date dataInicio = Date.from(LocalDateTime.of(periodoRelatorio.getDataInicio(), LocalTime.of(0, 0, 0))
				.atZone(ZoneId.systemDefault()).toInstant());
		Date dataFim = Date.from(LocalDateTime.of(periodoRelatorio.getDataFim(), LocalTime.of(23, 59, 59))
				.atZone(ZoneId.systemDefault()).toInstant());

		Map<String, Object> parametros = new HashMap<>();
		/* Digo qual o formato do relatório. */
		parametros.put("format", "pdf");
		/* Passo os parâmetros que eu configurei no relatório. */
		parametros.put("data_inicio", dataInicio);
		parametros.put("data_fim", dataFim);

		/* Carrega o relatório do JasperReport. */
		InputStream inputStream = this.getClass().getResourceAsStream("/relatorios/relatorio_vendas_emitidas.jasper");

		Connection con = this.dataSource.getConnection();

		try {

			JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, parametros, con);
			return JasperExportManager.exportReportToPdf(jasperPrint);

		} finally {
			con.close();
		}

	}

}
