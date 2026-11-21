package elicoes.api.es2.votacao.web;

import elicoes.api.es2.votacao.dto.CandidatoResumoResponse;
import elicoes.api.es2.votacao.dto.VotoRequest;
import elicoes.api.es2.votacao.dto.VotoResponse;
import elicoes.api.es2.votacao.service.UrnaService;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/eleicoes/{eleicaoId}")
@Validated
public class UrnaController {

	private final UrnaService urnaService;

	public UrnaController(UrnaService urnaService) {
		this.urnaService = urnaService;
	}

	@GetMapping("/candidatos")
	public List<CandidatoResumoResponse> listarCandidatos(@PathVariable @Positive Long eleicaoId,
			@RequestParam(required = false) String tipo) {
		return urnaService.listarCandidatos(eleicaoId, tipo);
	}

	@PostMapping("/votos")
	@ResponseStatus(HttpStatus.CREATED)
	public VotoResponse registrar(@PathVariable @Positive Long eleicaoId, @AuthenticationPrincipal Jwt jwt,
			@Valid @RequestBody VotoRequest request) {
		return urnaService.registrar(eleicaoId, eleitorId(jwt), request);
	}

	@PostMapping("/votos/confirmacao")
	public VotoResponse confirmar(@PathVariable @Positive Long eleicaoId, @AuthenticationPrincipal Jwt jwt,
			@Valid @RequestBody VotoRequest request) {
		return urnaService.confirmar(eleicaoId, eleitorId(jwt), request);
	}

	private Long eleitorId(Jwt jwt) {
		try {
			return Long.valueOf(jwt.getSubject());
		} catch (NumberFormatException ex) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Não autenticado");
		}
	}

}
