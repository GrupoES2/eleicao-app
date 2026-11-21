package elicoes.api.es2.votacao.service;

import elicoes.api.es2.votacao.domain.CandidatoLocal;
import elicoes.api.es2.votacao.domain.EleicaoLocal;
import elicoes.api.es2.votacao.domain.IntencaoVoto;
import elicoes.api.es2.votacao.domain.Participacao;
import elicoes.api.es2.votacao.domain.StatusEleicao;
import elicoes.api.es2.votacao.domain.TipoCandidato;
import elicoes.api.es2.votacao.domain.Voto;
import elicoes.api.es2.votacao.dto.CandidatoResumoResponse;
import elicoes.api.es2.votacao.dto.VotoRequest;
import elicoes.api.es2.votacao.dto.VotoResponse;
import elicoes.api.es2.votacao.exception.ConflitoDeDadosException;
import elicoes.api.es2.votacao.exception.RecursoNaoEncontradoException;
import elicoes.api.es2.votacao.exception.RequisicaoInvalidaException;
import elicoes.api.es2.votacao.repository.CandidatoLocalRepository;
import elicoes.api.es2.votacao.repository.EleicaoLocalRepository;
import elicoes.api.es2.votacao.repository.EleitorLocalRepository;
import elicoes.api.es2.votacao.repository.IntencaoVotoRepository;
import elicoes.api.es2.votacao.repository.ParticipacaoRepository;
import elicoes.api.es2.votacao.repository.VotoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class UrnaService {

	private static final Logger log = LoggerFactory.getLogger(UrnaService.class);

	private final EleicaoLocalRepository eleicoes;
	private final EleitorLocalRepository eleitores;
	private final CandidatoLocalRepository candidatos;
	private final IntencaoVotoRepository intencoes;
	private final ParticipacaoRepository participacoes;
	private final VotoRepository votos;

	public UrnaService(EleicaoLocalRepository eleicoes, EleitorLocalRepository eleitores,
			CandidatoLocalRepository candidatos, IntencaoVotoRepository intencoes,
			ParticipacaoRepository participacoes, VotoRepository votos) {
		this.eleicoes = eleicoes;
		this.eleitores = eleitores;
		this.candidatos = candidatos;
		this.intencoes = intencoes;
		this.participacoes = participacoes;
		this.votos = votos;
	}

	@Transactional(readOnly = true)
	public List<CandidatoResumoResponse> listarCandidatos(Long eleicaoId, String tipo) {
		exigirAberta(eleicaoId);
		var consulta = tipo == null || tipo.isBlank()
				? candidatos.findByEleicaoId(eleicaoId)
				: candidatos.findByEleicaoIdAndTipo(eleicaoId, tipoDe(tipo));
		return consulta.stream()
				.map(candidato -> new CandidatoResumoResponse(candidato.getId(), candidato.getNome(),
						candidato.getNumero(), candidato.getPartido(), candidato.getTipo()))
				.toList();
	}

	public VotoResponse registrar(Long eleicaoId, Long eleitorId, VotoRequest request) {
		exigirApto(eleicaoId, eleitorId);
		CandidatoLocal candidato = buscarCandidato(eleicaoId, request.candidatoId());
		if (participacoes.existsByEleitorIdAndEleicaoIdAndTipo(eleitorId, eleicaoId, candidato.getTipo())) {
			throw new ConflitoDeDadosException("Voto já confirmado para este cargo");
		}
		IntencaoVoto intencao = intencoes.findByEleitorIdAndEleicaoIdAndTipo(eleitorId, eleicaoId, candidato.getTipo())
				.orElseGet(IntencaoVoto::new);
		intencao.setEleitorId(eleitorId);
		intencao.setEleicaoId(eleicaoId);
		intencao.setCandidatoId(candidato.getId());
		intencao.setTipo(candidato.getTipo());
		intencoes.save(intencao);
		log.info("Intenção de voto registrada eleicaoId={} candidatoId={}", eleicaoId, candidato.getId());
		return new VotoResponse(eleicaoId, candidato.getId(), candidato.getTipo(), false);
	}

	public VotoResponse confirmar(Long eleicaoId, Long eleitorId, VotoRequest request) {
		exigirApto(eleicaoId, eleitorId);
		CandidatoLocal candidato = buscarCandidato(eleicaoId, request.candidatoId());
		if (participacoes.existsByEleitorIdAndEleicaoIdAndTipo(eleitorId, eleicaoId, candidato.getTipo())) {
			throw new ConflitoDeDadosException("Voto já confirmado para este cargo");
		}
		IntencaoVoto intencao = intencoes.findByEleitorIdAndEleicaoIdAndTipo(eleitorId, eleicaoId, candidato.getTipo())
				.orElseThrow(() -> new ConflitoDeDadosException("Não há voto para confirmar"));
		if (!intencao.getCandidatoId().equals(candidato.getId())) {
			throw new ConflitoDeDadosException("A confirmação não corresponde ao voto registrado");
		}

		Voto voto = new Voto();
		voto.setEleicaoId(eleicaoId);
		voto.setCandidatoId(candidato.getId());
		votos.save(voto);

		Participacao participacao = new Participacao();
		participacao.setEleitorId(eleitorId);
		participacao.setEleicaoId(eleicaoId);
		participacao.setTipo(candidato.getTipo());
		participacoes.save(participacao);
		intencoes.delete(intencao);

		log.info("Voto confirmado eleicaoId={} candidatoId={}", eleicaoId, candidato.getId());
		return new VotoResponse(eleicaoId, candidato.getId(), candidato.getTipo(), true);
	}

	private void exigirApto(Long eleicaoId, Long eleitorId) {
		exigirAberta(eleicaoId);
		if (!eleitores.existsById(eleitorId)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Eleitor não apto");
		}
	}

	private void exigirAberta(Long eleicaoId) {
		EleicaoLocal eleicao = eleicoes.findById(eleicaoId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Eleição não encontrada"));
		if (eleicao.getStatus() != StatusEleicao.ABERTA) {
			throw new ConflitoDeDadosException("A eleição não está aberta");
		}
	}

	private CandidatoLocal buscarCandidato(Long eleicaoId, Long candidatoId) {
		CandidatoLocal candidato = candidatos.findById(candidatoId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Candidato não encontrado"));
		if (!candidato.getEleicaoId().equals(eleicaoId)) {
			throw new RecursoNaoEncontradoException("Candidato não encontrado");
		}
		return candidato;
	}

	private TipoCandidato tipoDe(String tipo) {
		try {
			return TipoCandidato.valueOf(tipo);
		} catch (IllegalArgumentException ex) {
			throw new RequisicaoInvalidaException("Cargo inválido");
		}
	}

}
