package elicoes.api.es2.eleicoes_app.security;

import elicoes.api.es2.eleicoes_app.domain.Usuario;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

@Component
public class EmissorToken {

	private static final long VALIDADE_SEGUNDOS = 2 * 60 * 60;

	private final JwtEncoder encoder;

	public EmissorToken(@Value("${app.jwt.segredo}") String segredo) {
		SecretKey chave = new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
		this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(chave));
	}

	public String emitir(Usuario usuario) {
		Instant agora = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.subject(usuario.getId().toString())
				.claim("cargo", usuario.getCargo().name())
				.issuedAt(agora)
				.expiresAt(agora.plusSeconds(VALIDADE_SEGUNDOS))
				.build();
		JwsHeader cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();
		return encoder.encode(JwtEncoderParameters.from(cabecalho, claims)).getTokenValue();
	}

}
