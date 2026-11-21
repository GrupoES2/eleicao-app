package elicoes.api.es2.votacao.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VotoRequest(@NotNull @Positive Long candidatoId) {
}
