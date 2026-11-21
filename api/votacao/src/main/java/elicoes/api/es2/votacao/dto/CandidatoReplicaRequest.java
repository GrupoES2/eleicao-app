package elicoes.api.es2.votacao.dto;

import elicoes.api.es2.votacao.domain.StatusEleicao;
import elicoes.api.es2.votacao.domain.TipoCandidato;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CandidatoReplicaRequest(
		@NotNull @Positive Long eleicaoId,
		@NotBlank String nome,
		@NotBlank String numero,
		@NotBlank String partido,
		@NotNull TipoCandidato tipo,
		@NotNull StatusEleicao statusEleicao,
		@NotNull Instant ocorridoEm
) {
}
