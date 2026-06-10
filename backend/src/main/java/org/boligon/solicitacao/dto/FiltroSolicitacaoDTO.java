package org.boligon.solicitacao.dto;

import org.boligon.enums.Categoria;
import org.boligon.enums.Prioridade;
import org.boligon.enums.StatusSolicitacao;

public class FiltroSolicitacaoDTO {

    private Prioridade prioridade;
    private Categoria categoria;
    private StatusSolicitacao status;
    private String bairro;

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacao status) {
        this.status = status;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public boolean isVazio() {
        return prioridade == null && categoria == null && status == null
                && (bairro == null || bairro.trim().isEmpty());
    }
}
