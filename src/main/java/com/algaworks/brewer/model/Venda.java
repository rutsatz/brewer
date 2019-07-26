package com.algaworks.brewer.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "venda")
@DynamicUpdate
public class Venda {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long codigo;

	@Column(name = "data_criacao")
	private LocalDateTime dataCriacao;

	@Column(name = "valor_frete")
	private BigDecimal valorFrete;

	@Column(name = "valor_desconto")
	private BigDecimal valorDesconto;

	@Column(name = "valor_total")
	private BigDecimal valorTotal = BigDecimal.ZERO;

	private String observacao;

	@Column(name = "data_hora_entrega")
	private LocalDateTime dataHoraEntrega;

	@ManyToOne
	@JoinColumn(name = "codigo_cliente")
	private Cliente cliente;

	@ManyToOne
	@JoinColumn(name = "codigo_usuario")
	private Usuario usuario;

	/* Quando uma venda é criada, o status inicial dela é orçamento. */
	@Enumerated(EnumType.STRING)
	private StatusVenda status = StatusVenda.ORCAMENTO;

	@OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ItemVenda> itens = new ArrayList<>();

	/*
	 * Precisa desse campo para fazer a ligação com a tela, para recuperar a venda
	 * da sessão corretamente.
	 */
	@Transient
	private String uuid;

	/*
	 * Esses campos estão separados lá na tela, por isso adiciono aqui pra poder
	 * fazer o vínculo.
	 */
	@Transient
	private LocalDate dataEntrega;

	/*
	 * Pra receber o horário lá da tela no formato 10:15, por exemplo, eu preciso
	 * configurar esse formato lá no WebConfig, igual fizemos com a data. Eu preciso
	 * adicionar no dateTimeFormatter.
	 */
	@Transient
	private LocalTime horarioEntrega;

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public BigDecimal getValorFrete() {
		return valorFrete;
	}

	public void setValorFrete(BigDecimal valorFrete) {
		this.valorFrete = valorFrete;
	}

	public BigDecimal getValorDesconto() {
		return valorDesconto;
	}

	public void setValorDesconto(BigDecimal valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public StatusVenda getStatus() {
		return status;
	}

	public void setStatus(StatusVenda status) {
		this.status = status;
	}

	public List<ItemVenda> getItens() {
		return itens;
	}

	public void setItens(List<ItemVenda> itens) {
		this.itens = itens;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public LocalDateTime getDataHoraEntrega() {
		return dataHoraEntrega;
	}

	public void setDataHoraEntrega(LocalDateTime dataHoraEntrega) {
		this.dataHoraEntrega = dataHoraEntrega;
	}

	public LocalDate getDataEntrega() {
		return dataEntrega;
	}

	public void setDataEntrega(LocalDate dataEntrega) {
		this.dataEntrega = dataEntrega;
	}

	public LocalTime getHorarioEntrega() {
		return horarioEntrega;
	}

	public void setHorarioEntrega(LocalTime horarioEntrega) {
		this.horarioEntrega = horarioEntrega;
	}

	public boolean isNova() {
		return codigo == null;
	}

	/*
	 * Usa esse método ao invés do setItens, pois cada item tem o id de uma venda, e
	 * preciso setar esse id.
	 */
	public void adicionarItens(List<ItemVenda> itens) {
		this.itens = itens;
		/* Seta a venda para cada item. */
		this.itens.forEach(i -> i.setVenda(this));
	}

	public BigDecimal getValorTotalItens() {
		/*
		 * Percorre a lista de itens, somando o valor total de cada item. Não preciso
		 * validar se tem os itens e talz, pois quando chegar aqui, já vai estar válido,
		 * pois eu valido lá no controller.
		 */
//        BigDecimal valorTotalItens = getItens().stream().map(ItemVenda::getValorTotal).reduce(BigDecimal::add).get();

		/*
		 * Como o cálculo do total foi tirado do service e movido para cá, eu removo o
		 * get() e coloco um orElse, pois como pode ser uma venda nova ou o usuário não
		 * informou os itens, eu posso não ter os itens. Ai nesse caso, eu passo o
		 * orElse com Zero.
		 */
		return getItens().stream().map(ItemVenda::getValorTotal).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
	}

	/* Calcula o valor total da venda. */
	public void calcularValorTotal() {
		this.valorTotal = calcularValorTotal(getValorTotalItens(), getValorFrete(), getValorDesconto());
	}

	public Long getDiasCriacao() {
		LocalDate inicio = dataCriacao != null ? dataCriacao.toLocalDate() : LocalDate.now();
		return ChronoUnit.DAYS.between(inicio, LocalDate.now());
	}

	public boolean isSalvarPermitido() {
		return !status.equals(StatusVenda.CANCELADA);
	}

	public boolean isSalvarProibido() {
		return !isSalvarPermitido();
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Venda other = (Venda) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

}