package elicoes.api.es2.votacao.repository;

import elicoes.api.es2.votacao.domain.EleitorLocal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EleitorLocalRepository extends JpaRepository<EleitorLocal, Long> {
}
