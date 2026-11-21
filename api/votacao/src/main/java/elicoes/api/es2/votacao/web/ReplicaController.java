package elicoes.api.es2.votacao.web;

import elicoes.api.es2.votacao.dto.CandidatoReplicaRequest;
import elicoes.api.es2.votacao.dto.EleicaoReplicaRequest;
import elicoes.api.es2.votacao.dto.EleitorReplicaRequest;
import elicoes.api.es2.votacao.dto.PossuiVotosResponse;
import elicoes.api.es2.votacao.service.ReplicaService;

import java.time.Instant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/interno")
@Validated
public class ReplicaController {

	private final ReplicaService replicaService;

	public ReplicaController(ReplicaService replicaService) {
		this.replicaService = replicaService;
	}

	@PutMapping("/eleicoes/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizarEleicao(@PathVariable @Positive Long id, @Valid @RequestBody EleicaoReplicaRequest request) {
		replicaService.atualizarEleicao(id, request);
	}

	@PutMapping("/eleitores/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizarEleitor(@PathVariable @Positive Long id, @Valid @RequestBody EleitorReplicaRequest request) {
		replicaService.atualizarEleitor(id, request);
	}

	@DeleteMapping("/eleitores/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removerEleitor(@PathVariable @Positive Long id, @RequestParam Instant ocorridoEm) {
		replicaService.removerEleitor(id, ocorridoEm);
	}

	@PutMapping("/candidatos/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizarCandidato(@PathVariable @Positive Long id,
			@Valid @RequestBody CandidatoReplicaRequest request) {
		replicaService.atualizarCandidato(id, request);
	}

	@DeleteMapping("/candidatos/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removerCandidato(@PathVariable @Positive Long id, @RequestParam Instant ocorridoEm) {
		replicaService.removerCandidato(id, ocorridoEm);
	}

	@GetMapping("/candidatos/{id}/possui-votos")
	public PossuiVotosResponse possuiVotos(@PathVariable @Positive Long id) {
		return new PossuiVotosResponse(replicaService.possuiVotos(id));
	}

}
