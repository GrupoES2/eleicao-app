package elicoes.api.es2.votacao.repository;

import elicoes.api.es2.votacao.domain.Participacao;
import elicoes.api.es2.votacao.domain.TipoCandidato;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipacaoRepository extends JpaRepository<Participacao, Long> {

	boolean existsByEleitorIdAndEleicaoIdAndTipo(Long eleitorId, Long eleicaoId, TipoCandidato tipo);

}
