package elicoes.api.es2.votacao.config;

import elicoes.api.es2.votacao.dto.ErroResponse;
import elicoes.api.es2.votacao.exception.ConflitoDeDadosException;
import elicoes.api.es2.votacao.exception.RecursoNaoEncontradoException;
import elicoes.api.es2.votacao.exception.RequisicaoInvalidaException;

import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErroResponse> validacao() {
		return ResponseEntity.badRequest().body(new ErroResponse("Dados inválidos"));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErroResponse> corpoIlegivel() {
		return ResponseEntity.badRequest().body(new ErroResponse("Requisição inválida"));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErroResponse> parametro() {
		return ResponseEntity.badRequest().body(new ErroResponse("Identificador inválido"));
	}

	@ExceptionHandler(RequisicaoInvalidaException.class)
	public ResponseEntity<ErroResponse> requisicao(RequisicaoInvalidaException ex) {
		return ResponseEntity.badRequest().body(new ErroResponse(ex.getMessage()));
	}

	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public ResponseEntity<ErroResponse> naoEncontrado(RecursoNaoEncontradoException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(ex.getMessage()));
	}

	@ExceptionHandler(ConflitoDeDadosException.class)
	public ResponseEntity<ErroResponse> conflito(ConflitoDeDadosException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResponse(ex.getMessage()));
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ErroResponse> status(ResponseStatusException ex) {
		String mensagem = ex.getReason() == null ? "Requisição recusada" : ex.getReason();
		return ResponseEntity.status(ex.getStatusCode()).body(new ErroResponse(mensagem));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErroResponse> interno(Exception ex) {
		log.error("Falha não tratada", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErroResponse("Erro interno"));
	}

}
