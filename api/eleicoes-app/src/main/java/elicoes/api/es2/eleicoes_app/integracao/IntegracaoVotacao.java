package elicoes.api.es2.eleicoes_app.integracao;

import elicoes.api.es2.eleicoes_app.domain.Candidato;
import elicoes.api.es2.eleicoes_app.domain.Usuario;
import elicoes.api.es2.eleicoes_app.exception.ConflitoDeDadosException;

import java.time.Duration;
import java.time.Instant;

import tools.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class IntegracaoVotacao {

	private static final Logger log = LoggerFactory.getLogger(IntegracaoVotacao.class);

	private final EventoIntegracaoRepository eventos;
	private final ObjectMapper objectMapper;
	private final RestClient cliente;
	private final String chave;

	public IntegracaoVotacao(EventoIntegracaoRepository eventos, ObjectMapper objectMapper,
			@Value("${app.votacao.url}") String url, @Value("${app.integracao.chave}") String chave) {
		this.eventos = eventos;
		this.objectMapper = objectMapper;
		this.chave = chave;
		SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
		fabrica.setConnectTimeout(Duration.ofMillis(500));
		fabrica.setReadTimeout(Duration.ofMillis(500));
		this.cliente = RestClient.builder().baseUrl(url).requestFactory(fabrica).build();
	}

	public void enfileirarCandidato(Candidato candidato) {
		Usuario usuario = candidato.getUsuario();
		CandidatoPublicado publicado = new CandidatoPublicado(
				candidato.getEleicao().getId(),
				usuario.getNome() + " " + usuario.getSobrenome(),
				candidato.getNumero(),
				candidato.getPartido(),
				candidato.getTipo().name(),
				candidato.getEleicao().getStatus().name(),
				Instant.now());
		gravar(TipoEventoIntegracao.CANDIDATO_ATUALIZADO, candidato.getId(), publicado);
	}

	public void enfileirarRemocao(Long candidatoId) {
		gravar(TipoEventoIntegracao.CANDIDATO_REMOVIDO, candidatoId, new Remocao(Instant.now()));
	}

	public boolean possuiVotos(Long candidatoId) {
		try {
			return cliente.get()
					.uri("/interno/candidatos/{id}/possui-votos", candidatoId)
					.header("X-Chave-Interna", chave)
					.exchange((requisicao, resposta) -> {
						int status = resposta.getStatusCode().value();
						if (status == 404) {
							return false;
						}
						if (status >= 200 && status < 300) {
							PossuiVotos corpo = objectMapper.readValue(resposta.getBody(), PossuiVotos.class);
							return corpo != null && corpo.possui();
						}
						throw new ConflitoDeDadosException("Não foi possível confirmar os votos do candidato");
					});
		} catch (RestClientException ex) {
			log.warn("Consulta de votos indisponível candidatoId={}", candidatoId);
			throw new ConflitoDeDadosException("Não foi possível confirmar os votos do candidato");
		}
	}

	private void gravar(TipoEventoIntegracao tipo, Long recursoId, Object corpo) {
		EventoIntegracao evento = new EventoIntegracao();
		evento.setTipo(tipo);
		evento.setRecursoId(recursoId);
		evento.setCorpo(objectMapper.writeValueAsString(corpo));
		evento.setCriadoEm(Instant.now());
		eventos.save(evento);
	}

	private record Remocao(Instant ocorridoEm) {
	}

	private record PossuiVotos(boolean possui) {
	}

}
