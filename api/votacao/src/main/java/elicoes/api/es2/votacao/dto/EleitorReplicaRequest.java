package elicoes.api.es2.votacao.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;

public record EleitorReplicaRequest(@NotNull Instant ocorridoEm) {
}
