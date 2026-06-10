package org.boligon.historico.dto;

import org.boligon.enums.StatusSolicitacao;
import org.boligon.historico.domain.HistoricoStatus;

import java.time.LocalDateTime;

public class HistoricoResponse {

    private Long id;
    private StatusSolicitacao statusAnterior;
    private StatusSolicitacao statusNovo;
    private String comentario;
    private String nomeResponsavel;
    private LocalDateTime dataMovimentacao;

    public static HistoricoResponse de(HistoricoStatus historico) {
        HistoricoResponse response = new HistoricoResponse();
        response.setId(historico.getId());
        response.setStatusAnterior(historico.getStatusAnterior());
        response.setStatusNovo(historico.getStatusNovo());
        response.setComentario(historico.getComentario());
        response.setNomeResponsavel(historico.getNomeResponsavel());
        response.setDataMovimentacao(historico.getDataMovimentacao());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatusSolicitacao getStatusAnterior() {
        return statusAnterior;
    }

    public void setStatusAnterior(StatusSolicitacao statusAnterior) {
        this.statusAnterior = statusAnterior;
    }

    public StatusSolicitacao getStatusNovo() {
        return statusNovo;
    }

    public void setStatusNovo(StatusSolicitacao statusNovo) {
        this.statusNovo = statusNovo;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public void setNomeResponsavel(String nomeResponsavel) {
        this.nomeResponsavel = nomeResponsavel;
    }

    public LocalDateTime getDataMovimentacao() {
        return dataMovimentacao;
    }

    public void setDataMovimentacao(LocalDateTime dataMovimentacao) {
        this.dataMovimentacao = dataMovimentacao;
    }
}
