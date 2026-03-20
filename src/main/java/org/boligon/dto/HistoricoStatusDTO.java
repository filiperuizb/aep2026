package org.boligon.dto;

import org.boligon.enums.StatusSolicitacao;

public class HistoricoStatusDTO {

    private Long solicitacaoId;
    private StatusSolicitacao statusNovo;
    private String comentario;
    private Long responsavelId;

    public Long getSolicitacaoId() {
        return solicitacaoId;
    }

    public void setSolicitacaoId(Long solicitacaoId) {
        this.solicitacaoId = solicitacaoId;
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

    public Long getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(Long responsavelId) {
        this.responsavelId = responsavelId;
    }
}