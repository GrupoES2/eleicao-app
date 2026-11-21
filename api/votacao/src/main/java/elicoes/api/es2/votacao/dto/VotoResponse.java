package elicoes.api.es2.votacao.dto;

import elicoes.api.es2.votacao.domain.TipoCandidato;

public record VotoResponse(Long eleicaoId, Long candidatoId, TipoCandidato tipo, boolean confirmado) {
}
