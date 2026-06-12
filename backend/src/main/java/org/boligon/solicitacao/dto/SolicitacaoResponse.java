package org.boligon.solicitacao.dto;

import org.boligon.enums.Categoria;
import org.boligon.enums.Prioridade;
import org.boligon.enums.StatusSolicitacao;
import org.boligon.solicitacao.domain.Solicitacao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SolicitacaoResponse {

    private Long id;
    private String protocolo;
    private Categoria categoria;
    private String descricao;
    private String anexo;
    private boolean temAnexo;
    private String localizacao;
    private String bairro;
    private Prioridade prioridade;
    private StatusSolicitacao status;
    private boolean anonima;
    private Long usuarioId;
    private String nomeSolicitante;
    private String emailSolicitante;
    private LocalDateTime dataCriacao;
    private LocalDateTime prazoSla;
    private String justificativaAtraso;
    private boolean foraDoPrazoSla;
    private StatusSolicitacao proximoStatus;

    public static SolicitacaoResponse converter(Solicitacao solicitacao) {
        SolicitacaoResponse response = new SolicitacaoResponse();
        response.setId(solicitacao.getId());
        response.setProtocolo(solicitacao.getProtocolo());
        response.setCategoria(solicitacao.getCategoria());
        response.setDescricao(solicitacao.getDescricao());
        response.setAnexo(solicitacao.getAnexoNome());
        response.setTemAnexo(solicitacao.possuiAnexo());
        response.setLocalizacao(solicitacao.getLocalizacao());
        response.setBairro(solicitacao.getBairro());
        response.setPrioridade(solicitacao.getPrioridade());
        response.setStatus(solicitacao.getStatus());
        response.setAnonima(solicitacao.isAnonima());
        response.setUsuarioId(solicitacao.getUsuarioId());
        if (!solicitacao.isAnonima() && solicitacao.getUsuario() != null) {
            response.setNomeSolicitante(solicitacao.getUsuario().getNome());
            response.setEmailSolicitante(solicitacao.getUsuario().getEmail());
        }
        response.setDataCriacao(solicitacao.getDataCriacao());
        response.setPrazoSla(solicitacao.getPrazoSla());
        response.setJustificativaAtraso(solicitacao.getJustificativaAtraso());
        response.setForaDoPrazoSla(solicitacao.isForaDoPrazoSla());
        response.setProximoStatus(solicitacao.getStatus().obterProximo());
        return response;
    }

    public static List<SolicitacaoResponse> converterLista(List<Solicitacao> solicitacoes) {
        List<SolicitacaoResponse> resposta = new ArrayList<>();
        for (Solicitacao solicitacao : solicitacoes) {
            resposta.add(converter(solicitacao));
        }
        return resposta;
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

    public String getAnexo() {
        return anexo;
    }

    public void setAnexo(String anexo) {
        this.anexo = anexo;
    }

    public boolean isTemAnexo() {
        return temAnexo;
    }

    public void setTemAnexo(boolean temAnexo) {
        this.temAnexo = temAnexo;
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

    public String getNomeSolicitante() {
        return nomeSolicitante;
    }

    public void setNomeSolicitante(String nomeSolicitante) {
        this.nomeSolicitante = nomeSolicitante;
    }

    public String getEmailSolicitante() {
        return emailSolicitante;
    }

    public void setEmailSolicitante(String emailSolicitante) {
        this.emailSolicitante = emailSolicitante;
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

    public boolean isForaDoPrazoSla() {
        return foraDoPrazoSla;
    }

    public void setForaDoPrazoSla(boolean foraDoPrazoSla) {
        this.foraDoPrazoSla = foraDoPrazoSla;
    }

    public StatusSolicitacao getProximoStatus() {
        return proximoStatus;
    }

    public void setProximoStatus(StatusSolicitacao proximoStatus) {
        this.proximoStatus = proximoStatus;
    }
}
