package elicoes.api.es2.votacao.repository;

import elicoes.api.es2.votacao.domain.IntencaoVoto;
import elicoes.api.es2.votacao.domain.TipoCandidato;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IntencaoVotoRepository extends JpaRepository<IntencaoVoto, Long> {

	Optional<IntencaoVoto> findByEleitorIdAndEleicaoIdAndTipo(Long eleitorId, Long eleicaoId, TipoCandidato tipo);

}
