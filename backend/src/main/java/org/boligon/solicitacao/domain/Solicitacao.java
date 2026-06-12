package org.boligon.solicitacao.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.boligon.auth.domain.Usuario;
import org.boligon.enums.Categoria;
import org.boligon.enums.Prioridade;
import org.boligon.enums.StatusSolicitacao;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacoes")
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30, unique = true)
    private String protocolo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Categoria categoria;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Column(length = 255)
    private String anexo;

    @Column(nullable = false, length = 255)
    private String localizacao;

    @Column(nullable = false, length = 100)
    private String bairro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Prioridade prioridade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusSolicitacao status;

    @Column(nullable = false)
    private boolean anonima;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private LocalDateTime prazoSla;

    @Column(length = 500)
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

    public String getAnexo() {
        return anexo;
    }

    public void setAnexo(String anexo) {
        this.anexo = anexo;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Long getUsuarioId() {
        if (usuario == null) {
            return null;
        }
        return usuario.getId();
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
        if (prazoSla == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(prazoSla);
    }
}
