package elicoes.api.es2.votacao.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

public class ChaveInternaFilter extends OncePerRequestFilter {

	private final byte[] chave;

	public ChaveInternaFilter(@Value("${app.integracao.chave}") String chave) {
		this.chave = chave.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String recebida = request.getHeader("X-Chave-Interna");
		byte[] informada = recebida == null ? new byte[0] : recebida.getBytes(StandardCharsets.UTF_8);
		if (!MessageDigest.isEqual(chave, informada)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.getWriter().write("{\"mensagem\":\"Não autenticado\"}");
			return;
		}
		filterChain.doFilter(request, response);
	}

}
