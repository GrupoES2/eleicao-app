package elicoes.api.es2.votacao.repository;

import elicoes.api.es2.votacao.domain.Voto;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VotoRepository extends JpaRepository<Voto, Long> {

	boolean existsByCandidatoId(Long candidatoId);

}
