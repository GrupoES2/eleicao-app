package elicoes.api.es2.votacao;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UrnaControllerTest {

	private static final String CHAVE = "eleicoes-local-integracao-chave";

	@Autowired
	private MockMvc mockMvc;

	@Test
	void registraEConfirmaSemIdentificarOEleitor() throws Exception {
		Instant agora = Instant.parse("2026-10-04T12:00:00Z");
		mockMvc.perform(put("/interno/eleicoes/3")
						.header("X-Chave-Interna", CHAVE)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"status\":\"ABERTA\",\"ocorridoEm\":\"" + agora + "\"}"))
				.andExpect(status().isNoContent());
		mockMvc.perform(put("/interno/eleitores/77")
						.header("X-Chave-Interna", CHAVE)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"ocorridoEm\":\"" + agora + "\"}"))
				.andExpect(status().isNoContent());
		mockMvc.perform(put("/interno/candidatos/9")
						.header("X-Chave-Interna", CHAVE)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"eleicaoId":3,"nome":"Ana Silva","numero":"123","partido":"Partido","tipo":"SENADOR","statusEleicao":"ABERTA","ocorridoEm":"%s"}
								""".formatted(agora)))
				.andExpect(status().isNoContent());

		mockMvc.perform(post("/eleicoes/3/votos").contentType(MediaType.APPLICATION_JSON).content("{\"candidatoId\":9}"))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(post("/eleicoes/3/votos")
						.header("Authorization", "Bearer " + token("1", "CHEFE_SECAO"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"candidatoId\":9}"))
				.andExpect(status().isForbidden());

		String corpo = mockMvc.perform(post("/eleicoes/3/votos")
						.header("Authorization", "Bearer " + token("77", "ELEITOR"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"candidatoId\":9}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.confirmado").value(false))
				.andExpect(jsonPath("$.candidatoId").value(9))
				.andReturn()
				.getResponse()
				.getContentAsString();
		org.assertj.core.api.Assertions.assertThat(corpo).doesNotContain("77").doesNotContain("eleitorId");

		mockMvc.perform(post("/eleicoes/3/votos/confirmacao")
						.header("Authorization", "Bearer " + token("77", "ELEITOR"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"candidatoId\":9}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.confirmado").value(true));

		mockMvc.perform(post("/eleicoes/3/votos/confirmacao")
						.header("Authorization", "Bearer " + token("77", "ELEITOR"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"candidatoId\":9}"))
				.andExpect(status().isConflict());

		mockMvc.perform(get("/interno/candidatos/9/possui-votos").header("X-Chave-Interna", CHAVE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.possui").value(true));

		mockMvc.perform(delete("/interno/candidatos/9").header("X-Chave-Interna", CHAVE)
						.queryParam("ocorridoEm", agora.plusSeconds(60).toString()))
				.andExpect(status().isConflict());

		mockMvc.perform(get("/interno/candidatos/9/possui-votos"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void recusaVotoForaDaEleicaoAberta() throws Exception {
		Instant agora = Instant.parse("2026-10-04T11:00:00Z");
		mockMvc.perform(put("/interno/eleicoes/4")
						.header("X-Chave-Interna", CHAVE)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"status\":\"ENCERRADA\",\"ocorridoEm\":\"" + agora + "\"}"))
				.andExpect(status().isNoContent());
		mockMvc.perform(put("/interno/eleitores/78")
						.header("X-Chave-Interna", CHAVE)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"ocorridoEm\":\"" + agora + "\"}"))
				.andExpect(status().isNoContent());

		mockMvc.perform(post("/eleicoes/4/votos")
						.header("Authorization", "Bearer " + token("78", "ELEITOR"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"candidatoId\":9}"))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.mensagem").value("A eleição não está aberta"));
	}

	private String token(String eleitorId, String cargo) {
		SecretKey chave = new SecretKeySpec("eleicoes-local-hmac-chave-32bytes!!".getBytes(StandardCharsets.UTF_8),
				"HmacSHA256");
		var encoder = new NimbusJwtEncoder(new ImmutableSecret<>(chave));
		Instant agora = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.subject(eleitorId)
				.claim("cargo", cargo)
				.issuedAt(agora)
				.expiresAt(agora.plusSeconds(600))
				.build();
		return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
	}

}
