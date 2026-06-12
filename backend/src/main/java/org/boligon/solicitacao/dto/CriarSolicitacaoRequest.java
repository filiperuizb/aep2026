package org.boligon.solicitacao.dto;

import org.boligon.enums.Categoria;
import org.boligon.enums.Prioridade;

public class CriarSolicitacaoRequest {

    private Categoria categoria;
    private String descricao;
    private String anexoNome;
    private String anexoTipo;
    private String anexoBase64;
    private String localizacao;
    private String bairro;
    private Prioridade prioridade;
    private boolean anonima;
    private Long usuarioId;

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

    public String getAnexoNome() {
        return anexoNome;
    }

    public void setAnexoNome(String anexoNome) {
        this.anexoNome = anexoNome;
    }

    public String getAnexoTipo() {
        return anexoTipo;
    }

    public void setAnexoTipo(String anexoTipo) {
        this.anexoTipo = anexoTipo;
    }

    public String getAnexoBase64() {
        return anexoBase64;
    }

    public void setAnexoBase64(String anexoBase64) {
        this.anexoBase64 = anexoBase64;
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
}
