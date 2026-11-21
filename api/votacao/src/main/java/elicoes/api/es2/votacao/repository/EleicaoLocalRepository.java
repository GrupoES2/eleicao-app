package elicoes.api.es2.votacao.repository;

import elicoes.api.es2.votacao.domain.EleicaoLocal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EleicaoLocalRepository extends JpaRepository<EleicaoLocal, Long> {
}
