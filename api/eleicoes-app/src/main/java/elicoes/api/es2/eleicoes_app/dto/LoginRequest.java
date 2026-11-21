package elicoes.api.es2.eleicoes_app.dto;

import elicoes.api.es2.eleicoes_app.validation.CpfUtils;
import elicoes.api.es2.eleicoes_app.validation.Texto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
		@NotBlank(message = "CPF é obrigatório") String cpf,
		@NotBlank(message = "Senha é obrigatória") String senha
) {

	public LoginRequest {
		cpf = CpfUtils.somenteDigitos(cpf);
		senha = Texto.senha(senha);
	}

}
