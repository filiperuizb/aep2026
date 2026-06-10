package org.boligon.solicitacao.dto;

import org.boligon.historico.dto.HistoricoResponse;
import org.boligon.solicitacao.domain.Solicitacao;

import java.util.List;

public class SolicitacaoDetalheResponse {

    private SolicitacaoResponse solicitacao;
    private List<HistoricoResponse> historico;

    public static SolicitacaoDetalheResponse de(Solicitacao solicitacao, List<HistoricoResponse> historico) {
        SolicitacaoDetalheResponse response = new SolicitacaoDetalheResponse();
        response.setSolicitacao(SolicitacaoResponse.de(solicitacao));
        response.setHistorico(historico);
        return response;
    }

    public SolicitacaoResponse getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoResponse solicitacao) {
        this.solicitacao = solicitacao;
    }

    public List<HistoricoResponse> getHistorico() {
        return historico;
    }

    public void setHistorico(List<HistoricoResponse> historico) {
        this.historico = historico;
    }
}
