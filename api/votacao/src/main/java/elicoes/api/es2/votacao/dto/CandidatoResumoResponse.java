package elicoes.api.es2.votacao.dto;

import elicoes.api.es2.votacao.domain.TipoCandidato;

public record CandidatoResumoResponse(Long id, String nome, String numero, String partido, TipoCandidato tipo) {
}
