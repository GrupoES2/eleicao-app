package elicoes.api.es2.votacao.dto;

import elicoes.api.es2.votacao.domain.StatusEleicao;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;

public record EleicaoReplicaRequest(@NotNull StatusEleicao status, @NotNull Instant ocorridoEm) {
}
