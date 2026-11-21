package elicoes.api.es2.votacao.repository;

import elicoes.api.es2.votacao.domain.CandidatoLocal;
import elicoes.api.es2.votacao.domain.TipoCandidato;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidatoLocalRepository extends JpaRepository<CandidatoLocal, Long> {

	List<CandidatoLocal> findByEleicaoId(Long eleicaoId);

	List<CandidatoLocal> findByEleicaoIdAndTipo(Long eleicaoId, TipoCandidato tipo);

}
