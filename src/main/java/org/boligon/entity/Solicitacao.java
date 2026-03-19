package org.boligon.entity;

import org.boligon.enums.Categoria;
import org.boligon.enums.Prioridade;
import org.boligon.enums.StatusSolicitacao;

import java.time.LocalDateTime;

public class Solicitacao {

    private Long id;
    private String protocolo;
    private Categoria categoria;
    private String descricao;
    private String localizacao;
    private String bairro;
    private Prioridade prioridade;
    private StatusSolicitacao status;
    private boolean anonima;
    private Long usuarioId;
    private LocalDateTime dataCriacao;
    private LocalDateTime prazoSla;
    private String justificativaAtraso;

    public Solicitacao() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacao status) {
        this.status = status;
    }

    public boolean isAnonima() {
        return anonima;
    }

    public void setAnonima(boolean anonima) {
        this.anonima = anonima;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getPrazoSla() {
        return prazoSla;
    }

    public void setPrazoSla(LocalDateTime prazoSla) {
        this.prazoSla = prazoSla;
    }

    public String getJustificativaAtraso() {
        return justificativaAtraso;
    }

    public void setJustificativaAtraso(String justificativaAtraso) {
        this.justificativaAtraso = justificativaAtraso;
    }

    @Override
    public String toString() {
        return "Solicitacao{" +
                "id=" + id +
                ", protocolo='" + protocolo + '\'' +
                ", categoria=" + categoria +
                ", descricao='" + descricao + '\'' +
                ", localizacao='" + localizacao + '\'' +
                ", bairro='" + bairro + '\'' +
                ", prioridade=" + prioridade +
                ", status=" + status +
                ", anonima=" + anonima +
                ", usuarioId=" + usuarioId +
                ", dataCriacao=" + dataCriacao +
                ", prazoSla=" + prazoSla +
                ", justificativaAtraso='" + justificativaAtraso + '\'' +
                '}';
    }
}