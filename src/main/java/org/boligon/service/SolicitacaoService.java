package org.boligon.service;

import org.boligon.entity.Solicitacao;
import org.boligon.entity.Usuario;
import org.boligon.enums.Prioridade;
import org.boligon.enums.StatusSolicitacao;
import org.boligon.exception.ValidacaoException;
import org.boligon.repository.SolicitacaoRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository = new SolicitacaoRepository();


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
}
