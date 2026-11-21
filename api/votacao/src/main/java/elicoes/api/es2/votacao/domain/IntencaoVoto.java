package elicoes.api.es2.votacao.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"eleitorId", "eleicaoId", "tipo"}))
public class IntencaoVoto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long eleitorId;

	@Column(nullable = false)
	private Long eleicaoId;

	@Column(nullable = false)
	private Long candidatoId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private TipoCandidato tipo;

	public Long getId() {
		return id;
	}

	public Long getEleitorId() {
		return eleitorId;
	}

	public void setEleitorId(Long eleitorId) {
		this.eleitorId = eleitorId;
	}

	public Long getEleicaoId() {
		return eleicaoId;
	}

	public void setEleicaoId(Long eleicaoId) {
		this.eleicaoId = eleicaoId;
	}

	public Long getCandidatoId() {
		return candidatoId;
	}

	public void setCandidatoId(Long candidatoId) {
		this.candidatoId = candidatoId;
	}

	public TipoCandidato getTipo() {
		return tipo;
	}

	public void setTipo(TipoCandidato tipo) {
		this.tipo = tipo;
	}

}
