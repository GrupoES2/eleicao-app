package elicoes.api.es2.votacao.service;

import elicoes.api.es2.votacao.domain.CandidatoLocal;
import elicoes.api.es2.votacao.domain.EleicaoLocal;
import elicoes.api.es2.votacao.domain.EleitorLocal;
import elicoes.api.es2.votacao.dto.CandidatoReplicaRequest;
import elicoes.api.es2.votacao.dto.EleicaoReplicaRequest;
import elicoes.api.es2.votacao.dto.EleitorReplicaRequest;
import elicoes.api.es2.votacao.exception.ConflitoDeDadosException;
import elicoes.api.es2.votacao.repository.CandidatoLocalRepository;
import elicoes.api.es2.votacao.repository.EleicaoLocalRepository;
import elicoes.api.es2.votacao.repository.EleitorLocalRepository;
import elicoes.api.es2.votacao.repository.VotoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class ReplicaService {

	private static final Logger log = LoggerFactory.getLogger(ReplicaService.class);

	private final EleicaoLocalRepository eleicoes;
	private final EleitorLocalRepository eleitores;
	private final CandidatoLocalRepository candidatos;
	private final VotoRepository votos;

	public ReplicaService(EleicaoLocalRepository eleicoes, EleitorLocalRepository eleitores,
			CandidatoLocalRepository candidatos, VotoRepository votos) {
		this.eleicoes = eleicoes;
		this.eleitores = eleitores;
		this.candidatos = candidatos;
		this.votos = votos;
	}

	public void atualizarEleicao(Long id, EleicaoReplicaRequest request) {
		EleicaoLocal eleicao = eleicoes.findById(id).orElseGet(EleicaoLocal::new);
		if (defasado(eleicao.getAtualizadoEm(), request.ocorridoEm())) {
			return;
		}
		eleicao.setId(id);
		eleicao.setStatus(request.status());
		eleicao.setAtualizadoEm(request.ocorridoEm());
		eleicoes.save(eleicao);
		log.info("Eleição local atualizada id={}", id);
	}

	public void atualizarEleitor(Long id, EleitorReplicaRequest request) {
		EleitorLocal eleitor = eleitores.findById(id).orElseGet(EleitorLocal::new);
		if (defasado(eleitor.getAtualizadoEm(), request.ocorridoEm())) {
			return;
		}
		eleitor.setId(id);
		eleitor.setAtualizadoEm(request.ocorridoEm());
		eleitores.save(eleitor);
	}

	public void removerEleitor(Long id, Instant ocorridoEm) {
		eleitores.findById(id).ifPresent(eleitor -> {
			if (defasado(eleitor.getAtualizadoEm(), ocorridoEm)) {
				return;
			}
			eleitores.delete(eleitor);
		});
	}

	public void atualizarCandidato(Long id, CandidatoReplicaRequest request) {
		atualizarEleicao(request.eleicaoId(), new EleicaoReplicaRequest(request.statusEleicao(), request.ocorridoEm()));
		CandidatoLocal candidato = candidatos.findById(id).orElseGet(CandidatoLocal::new);
		if (defasado(candidato.getAtualizadoEm(), request.ocorridoEm())) {
			return;
		}
		candidato.setId(id);
		candidato.setEleicaoId(request.eleicaoId());
		candidato.setNome(request.nome());
		candidato.setNumero(request.numero());
		candidato.setPartido(request.partido());
		candidato.setTipo(request.tipo());
		candidato.setAtualizadoEm(request.ocorridoEm());
		candidatos.save(candidato);
		log.info("Candidato local atualizado id={} eleicaoId={}", id, request.eleicaoId());
	}

	public void removerCandidato(Long id, Instant ocorridoEm) {
		if (votos.existsByCandidatoId(id)) {
			throw new ConflitoDeDadosException("Candidato com votos registrados não pode ser excluído");
		}
		candidatos.findById(id).ifPresent(candidato -> {
			if (defasado(candidato.getAtualizadoEm(), ocorridoEm)) {
				return;
			}
			candidatos.delete(candidato);
			log.info("Candidato local removido id={}", id);
		});
	}

	public boolean possuiVotos(Long candidatoId) {
		return votos.existsByCandidatoId(candidatoId);
	}

	private boolean defasado(Instant atual, Instant recebido) {
		return atual != null && !recebido.isAfter(atual);
	}

}
