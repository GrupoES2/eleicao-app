package elicoes.api.es2.eleicoes_app.integracao;

import java.time.Instant;

public record CandidatoPublicado(
		Long eleicaoId,
		String nome,
		String numero,
		String partido,
		String tipo,
		String statusEleicao,
		Instant ocorridoEm
) {
}
