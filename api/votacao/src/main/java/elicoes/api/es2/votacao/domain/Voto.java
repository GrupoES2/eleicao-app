package elicoes.api.es2.votacao.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Voto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long eleicaoId;

	@Column(nullable = false)
	private Long candidatoId;

	public Long getId() {
		return id;
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

}
