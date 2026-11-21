package elicoes.api.es2.votacao.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class EleitorLocal {

	@Id
	private Long id;

	@Column(nullable = false)
	private Instant atualizadoEm;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Instant getAtualizadoEm() {
		return atualizadoEm;
	}

	public void setAtualizadoEm(Instant atualizadoEm) {
		this.atualizadoEm = atualizadoEm;
	}

}
