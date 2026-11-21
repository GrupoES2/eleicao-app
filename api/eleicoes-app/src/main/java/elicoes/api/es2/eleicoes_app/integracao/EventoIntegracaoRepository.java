package elicoes.api.es2.eleicoes_app.integracao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoIntegracaoRepository extends JpaRepository<EventoIntegracao, Long> {

	List<EventoIntegracao> findTop20ByPublicadoEmIsNullOrderByIdAsc();

}
