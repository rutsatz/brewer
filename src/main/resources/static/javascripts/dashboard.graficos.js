var Brewer = Brewer || {};

Brewer.GraficoVendaPorMes = (function() {
	
	function GraficoVendaPorMes() {
		/* Pega o contexto 2d do canvas. */
		this.ctx = $('#graficoVendasPorMes')[0].getContext('2d');
	}
	
	GraficoVendaPorMes.prototype.iniciar = function() {
		$.ajax({
			url: 'vendas/totalPorMes',
			method: 'GET',
			success: onDadosRecebidos.bind(this)
		});
	}
	
	function onDadosRecebidos(vendaMes) {
		var meses = [];
		var valores = [];
		
		vendaMes.forEach(function(obj) {
			/** O unshift insere no inicio, desse forma, mantenho a ordem correta. */
			meses.unshift(obj.mes);
			valores.unshift(obj.total);
		});
		
		
		var graficoVendasPorMes = new Chart(this.ctx, {
		    type: 'line',
		    data: {
		    	labels: meses,
		    	datasets: [{
		    		label: 'Vendas por mês',
		    		backgroundColor: "rgba(26,179,148,0.5)",
		    		pointBorderColor: "rgba(26,179,148,1)",
		            pointBackgroundColor: "#fff",
		            data: valores
		    	}]
		    },
//		    options: options
		});
	}
	
	return GraficoVendaPorMes;
	
}());

$(function() {
	
	var graficoVendaPorMes = new Brewer.GraficoVendaPorMes();
	graficoVendaPorMes.iniciar();
	
});
