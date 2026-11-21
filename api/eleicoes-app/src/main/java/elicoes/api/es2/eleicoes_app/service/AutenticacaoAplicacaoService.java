package elicoes.api.es2.eleicoes_app.service;

import elicoes.api.es2.eleicoes_app.domain.Usuario;
import elicoes.api.es2.eleicoes_app.dto.LoginRequest;
import elicoes.api.es2.eleicoes_app.dto.LoginResponse;
import elicoes.api.es2.eleicoes_app.exception.CredenciaisInvalidasException;
import elicoes.api.es2.eleicoes_app.repository.UsuarioRepository;
import elicoes.api.es2.eleicoes_app.security.EmissorToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoAplicacaoService {

	private static final Logger log = LoggerFactory.getLogger(AutenticacaoAplicacaoService.class);

	private final UsuarioRepository usuarios;
	private final PasswordEncoder passwordEncoder;
	private final EmissorToken emissorToken;

	public AutenticacaoAplicacaoService(UsuarioRepository usuarios, PasswordEncoder passwordEncoder,
			EmissorToken emissorToken) {
		this.usuarios = usuarios;
		this.passwordEncoder = passwordEncoder;
		this.emissorToken = emissorToken;
	}

	public LoginResponse entrar(LoginRequest request) {
		Usuario usuario = usuarios.findByCpf(request.cpf()).orElse(null);
		if (usuario == null || usuario.getSenha() == null || usuario.getCargo() == null
				|| !passwordEncoder.matches(request.senha(), usuario.getSenha())) {
			log.info("Tentativa de login recusada");
			throw new CredenciaisInvalidasException();
		}
		return new LoginResponse(usuario.getId(), usuario.getNome(), usuario.getCargo(), emissorToken.emitir(usuario));
	}

}
