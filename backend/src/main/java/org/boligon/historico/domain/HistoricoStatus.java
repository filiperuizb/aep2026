package org.boligon.historico.domain;

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
import org.boligon.enums.StatusSolicitacao;
import org.boligon.solicitacao.domain.Solicitacao;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_status")
public class HistoricoStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitacao_id", nullable = false)
    private Solicitacao solicitacao;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private StatusSolicitacao statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusSolicitacao statusNovo;

    @Column(nullable = false, length = 500)
    private String comentario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "responsavel_id", nullable = false)
    private Usuario responsavel;

    @Column(nullable = false)
    private LocalDateTime dataMovimentacao;

    public HistoricoStatus() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Solicitacao getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(Solicitacao solicitacao) {
        this.solicitacao = solicitacao;
    }

    public Long getSolicitacaoId() {
        if (solicitacao == null) {
            return null;
        }
        return solicitacao.getId();
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

    public Usuario getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Usuario responsavel) {
        this.responsavel = responsavel;
    }

    public Long getResponsavelId() {
        if (responsavel == null) {
            return null;
        }
        return responsavel.getId();
    }

    public String getNomeResponsavel() {
        if (responsavel == null) {
            return null;
        }
        return responsavel.getNome();
    }

    public LocalDateTime getDataMovimentacao() {
        return dataMovimentacao;
    }

    public void setDataMovimentacao(LocalDateTime dataMovimentacao) {
        this.dataMovimentacao = dataMovimentacao;
    }
}
