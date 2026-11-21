package elicoes.api.es2.votacao.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity
public class CandidatoLocal {

	@Id
	private Long id;

	@Column(nullable = false)
	private Long eleicaoId;

	@Column(nullable = false, length = 160)
	private String nome;

	@Column(nullable = false, length = 5)
	private String numero;

	@Column(nullable = false, length = 80)
	private String partido;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private TipoCandidato tipo;

	@Column(nullable = false)
	private Instant atualizadoEm;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEleicaoId() {
		return eleicaoId;
	}

	public void setEleicaoId(Long eleicaoId) {
		this.eleicaoId = eleicaoId;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getPartido() {
		return partido;
	}

	public void setPartido(String partido) {
		this.partido = partido;
	}

	public TipoCandidato getTipo() {
		return tipo;
	}

	public void setTipo(TipoCandidato tipo) {
		this.tipo = tipo;
	}

	public Instant getAtualizadoEm() {
		return atualizadoEm;
	}

	public void setAtualizadoEm(Instant atualizadoEm) {
		this.atualizadoEm = atualizadoEm;
	}

}
