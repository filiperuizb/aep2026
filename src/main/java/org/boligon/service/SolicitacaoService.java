package org.boligon.service;

import org.boligon.dto.HistoricoStatusDTO;
import org.boligon.entity.Solicitacao;
import org.boligon.entity.Usuario;
import org.boligon.enums.Categoria;
import org.boligon.enums.Prioridade;
import org.boligon.enums.StatusSolicitacao;
import org.boligon.exception.EntidadeNaoEncontradaException;
import org.boligon.exception.ValidacaoException;
import org.boligon.repository.SolicitacaoRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository = new SolicitacaoRepository();
    private final HistoricoStatusService historicoStatusService = new HistoricoStatusService();


    public void criarSolicitacao(Solicitacao novaSolicitacao, Usuario usuarioLogado) {
        validarSolicitacao(novaSolicitacao);
        definirAutorSolicitacao(novaSolicitacao, usuarioLogado);

        novaSolicitacao.setProtocolo(gerarProtocolo());
        novaSolicitacao.setDataCriacao(LocalDateTime.now());
        novaSolicitacao.setStatus(StatusSolicitacao.ABERTO);
        novaSolicitacao.setPrazoSla(calcularPrazoSla(novaSolicitacao.getPrioridade()));

        solicitacaoRepository.salvar(novaSolicitacao);
    }

    public Solicitacao buscarPorProtocolo(String protocolo) {

        if(protocolo.isEmpty()) throw new ValidacaoException("É obrigatório informar um protocolo nessa busca.");


        Solicitacao solicitacao = solicitacaoRepository.buscarPorProtocolo(protocolo);

        if(solicitacao == null) throw new ValidacaoException("Solicitação não encontrada.");
        return solicitacao;
    }

    public List<Solicitacao> listarPorUsuarioId(Long id) {
        if(id == null) throw new ValidacaoException("É obrigatório informar o id do Usuário");
        return solicitacaoRepository.listarPorUsuarioId(id);
    }


    public List<Solicitacao> listarTodas() {
        return solicitacaoRepository.listarTodas();
    }

    private void validarSolicitacao(Solicitacao solicitacao) {

        if (solicitacao == null) {
            throw new ValidacaoException("A solicitação é obrigatória.");
        }

        if (solicitacao.getCategoria() == null) {
            throw new ValidacaoException("A categoria é obrigatória.");
        }

        if (solicitacao.getDescricao() == null || solicitacao.getDescricao().trim().isEmpty()) {
            throw new ValidacaoException("A descrição é obrigatória.");
        }

        if (solicitacao.getLocalizacao() == null || solicitacao.getLocalizacao().trim().isEmpty()) {
            throw new ValidacaoException("A localização é obrigatória.");
        }

        if (solicitacao.getBairro() == null || solicitacao.getBairro().trim().isEmpty()) {
            throw new ValidacaoException("O bairro é obrigatório.");
        }

        if (solicitacao.getPrioridade() == null) {
            throw new ValidacaoException("A prioridade é obrigatória.");
        }
    }

    private void definirAutorSolicitacao(Solicitacao solicitacao, Usuario usuarioLogado) {
        if(solicitacao.isAnonima()) {
            solicitacao.setUsuarioId(null);
            return;
        }

        solicitacao.setUsuarioId(usuarioLogado.getId());
    }

    private String gerarProtocolo() {
        String data = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long numero = System.currentTimeMillis() % 100000;

        return "OBS - " + data + "-" + numero;
    }

    private LocalDateTime calcularPrazoSla(Prioridade prioridade) {
        return LocalDateTime.now().plusDays(prioridade.getDiasSla());
    }

    public void atualizarStatus(HistoricoStatusDTO dto) {

        this.historicoStatusService.validarAtualizacaoStatus(dto);

        Solicitacao solicitacao = solicitacaoRepository.buscarPorId(dto.getSolicitacaoId());

        if (solicitacao == null) {
            throw new EntidadeNaoEncontradaException("Solicitação não encontrada.");
        }

        StatusSolicitacao statusAnterior = solicitacao.getStatus();

        solicitacaoRepository.atualizarStatus(
                dto.getSolicitacaoId(),
                dto.getStatusNovo(),
                null
        );

        historicoStatusService.registrarMovimentacao(
                dto,
                statusAnterior
        );
    }

    public List<Solicitacao> listarPorPrioridade(Prioridade prioridade) {
        if (prioridade == null) throw new ValidacaoException("A prioridade é obrigatória.");
        return solicitacaoRepository.listarPorPrioridade(prioridade);
    }

    public List<Solicitacao> listarPorStatus(StatusSolicitacao status) {
        if (status == null) throw new ValidacaoException("O status é obrigatório.");
        return solicitacaoRepository.listarPorStatus(status);
    }

    public List<Solicitacao> listarPorBairro(String bairro) {
        if (bairro == null || bairro.trim().isEmpty()) throw new ValidacaoException("O bairro é obrigatório.");
        return solicitacaoRepository.listarPorBairro(bairro.trim());
    }

    public List<Solicitacao> listarPorCategoria(Categoria categoria) {
        if(categoria==null) throw new ValidacaoException("A categoria é obrigatória.");
        return solicitacaoRepository.listarPorCategoria(categoria);
    }
}
