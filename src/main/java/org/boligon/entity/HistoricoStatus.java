package org.boligon.entity;

import org.boligon.enums.StatusSolicitacao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistoricoStatus {

    private Long id;
    private Long solicitacaoId;
    private StatusSolicitacao statusAnterior;
    private StatusSolicitacao statusNovo;
    private String comentario;
    private Long responsavelId;
    private LocalDateTime dataMovimentacao;
    private String nomeResponsavel;

    public HistoricoStatus() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSolicitacaoId() {
        return solicitacaoId;
    }

    public void setSolicitacaoId(Long solicitacaoId) {
        this.solicitacaoId = solicitacaoId;
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

    public Long getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(Long responsavelId) {
        this.responsavelId = responsavelId;
    }

    public LocalDateTime getDataMovimentacao() {
        return dataMovimentacao;
    }

    public void setDataMovimentacao(LocalDateTime dataMovimentacao) {
        this.dataMovimentacao = dataMovimentacao;
    }

    public String exibirDataMovimentacaoFormatada() {
        return this.dataMovimentacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public void setNomeResponsavel(String nomeResponsavel) {
        this.nomeResponsavel = nomeResponsavel;
    }
}
