package elicoes.api.es2.eleicoes_app.integracao;

import java.time.Duration;
import java.time.Instant;

import tools.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(name = "app.votacao.publicacao.enabled", havingValue = "true")
public class PublicadorVotacao {

	private static final Logger log = LoggerFactory.getLogger(PublicadorVotacao.class);

	private final EventoIntegracaoRepository eventos;
	private final ObjectMapper objectMapper;
	private final RestClient cliente;
	private final String chave;

	public PublicadorVotacao(EventoIntegracaoRepository eventos, ObjectMapper objectMapper,
			@Value("${app.votacao.url}") String url, @Value("${app.integracao.chave}") String chave) {
		this.eventos = eventos;
		this.objectMapper = objectMapper;
		this.chave = chave;
		SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
		fabrica.setConnectTimeout(Duration.ofMillis(500));
		fabrica.setReadTimeout(Duration.ofMillis(500));
		this.cliente = RestClient.builder().baseUrl(url).requestFactory(fabrica).build();
	}

	@Scheduled(fixedDelay = 2000)
	public void publicar() {
		for (EventoIntegracao evento : eventos.findTop20ByPublicadoEmIsNullOrderByIdAsc()) {
			try {
				int status = enviar(evento);
				if ((status >= 200 && status < 300) || status == 409) {
					evento.setPublicadoEm(Instant.now());
					eventos.save(evento);
					continue;
				}
				log.warn("Publicação recusada eventoId={} status={}", evento.getId(), status);
			} catch (RuntimeException ex) {
				log.warn("Publicação indisponível eventoId={}", evento.getId());
			}
			break;
		}
	}

	private int enviar(EventoIntegracao evento) {
		if (evento.getTipo() == TipoEventoIntegracao.CANDIDATO_REMOVIDO) {
			Remocao remocao = objectMapper.readValue(evento.getCorpo(), Remocao.class);
			return cliente.delete()
					.uri(builder -> builder.path("/interno/candidatos/{id}")
							.queryParam("ocorridoEm", remocao.ocorridoEm().toString())
							.build(evento.getRecursoId()))
					.header("X-Chave-Interna", chave)
					.exchange((requisicao, resposta) -> resposta.getStatusCode().value());
		}
		CandidatoPublicado publicado = objectMapper.readValue(evento.getCorpo(), CandidatoPublicado.class);
		return cliente.put()
				.uri("/interno/candidatos/{id}", evento.getRecursoId())
				.header("X-Chave-Interna", chave)
				.body(publicado)
				.exchange((requisicao, resposta) -> resposta.getStatusCode().value());
	}

	private record Remocao(Instant ocorridoEm) {
	}

}
