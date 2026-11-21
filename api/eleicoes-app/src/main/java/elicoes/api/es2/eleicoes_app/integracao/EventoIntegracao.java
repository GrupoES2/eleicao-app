package elicoes.api.es2.eleicoes_app.integracao;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class EventoIntegracao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private TipoEventoIntegracao tipo;

	@Column(nullable = false)
	private Long recursoId;

	@Column(nullable = false, length = 4000)
	private String corpo;

	@Column(nullable = false)
	private Instant criadoEm;

	private Instant publicadoEm;

	public Long getId() {
		return id;
	}

	public TipoEventoIntegracao getTipo() {
		return tipo;
	}

	public void setTipo(TipoEventoIntegracao tipo) {
		this.tipo = tipo;
	}

	public Long getRecursoId() {
		return recursoId;
	}

	public void setRecursoId(Long recursoId) {
		this.recursoId = recursoId;
	}

	public String getCorpo() {
		return corpo;
	}

	public void setCorpo(String corpo) {
		this.corpo = corpo;
	}

	public Instant getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Instant criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Instant getPublicadoEm() {
		return publicadoEm;
	}

	public void setPublicadoEm(Instant publicadoEm) {
		this.publicadoEm = publicadoEm;
	}

}
