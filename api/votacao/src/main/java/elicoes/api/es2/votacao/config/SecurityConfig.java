package elicoes.api.es2.votacao.config;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

	@Bean
	JwtDecoder jwtDecoder(@Value("${app.jwt.segredo}") String segredo) {
		SecretKey chave = new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
		return NimbusJwtDecoder.withSecretKey(chave).macAlgorithm(MacAlgorithm.HS256).build();
	}

	@Bean
	@Order(1)
	SecurityFilterChain interno(HttpSecurity http, @Value("${app.integracao.chave}") String chave) throws Exception {
		http.securityMatcher("/interno/**")
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
				.addFilterBefore(new ChaveInternaFilter(chave), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	@Order(2)
	SecurityFilterChain urna(HttpSecurity http) throws Exception {
		JwtAuthenticationConverter conversor = new JwtAuthenticationConverter();
		conversor.setJwtGrantedAuthoritiesConverter(jwt -> {
			String cargo = jwt.getClaimAsString("cargo");
			if (cargo == null || cargo.isBlank()) {
				return List.of();
			}
			return List.of(new SimpleGrantedAuthority("ROLE_" + cargo));
		});
		http.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/eleicoes/**").hasRole("ELEITOR")
						.anyRequest().denyAll())
				.oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(conversor)))
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint((request, response, authException) ->
								escrever(response, HttpServletResponse.SC_UNAUTHORIZED, "Não autenticado"))
						.accessDeniedHandler((request, response, accessDeniedException) ->
								escrever(response, HttpServletResponse.SC_FORBIDDEN, "Acesso negado")));
		return http.build();
	}

	private static void escrever(HttpServletResponse response, int status, String mensagem) throws java.io.IOException {
		response.setStatus(status);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType("application/json");
		response.getWriter().write("{\"mensagem\":\"" + mensagem + "\"}");
	}

}
