package elicoes.api.es2.eleicoes_app.controller;

import elicoes.api.es2.eleicoes_app.dto.LoginRequest;
import elicoes.api.es2.eleicoes_app.dto.LoginResponse;
import elicoes.api.es2.eleicoes_app.service.AutenticacaoAplicacaoService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

	private final AutenticacaoAplicacaoService autenticacao;

	public AutenticacaoController(AutenticacaoAplicacaoService autenticacao) {
		this.autenticacao = autenticacao;
	}

	@PostMapping("/login")
	public LoginResponse login(@Valid @RequestBody LoginRequest request) {
		return autenticacao.entrar(request);
	}

}
