package org.boligon.enums;

import org.boligon.exception.ValidacaoException;

public enum StatusSolicitacao {
    ABERTO,
    TRIAGEM,
    EM_EXECUCAO,
    RESOLVIDO,
    ENCERRADO;

    public StatusSolicitacao obterProximo() {
        if (this == ABERTO) {
            return TRIAGEM;
        }
        if (this == TRIAGEM) {
            return EM_EXECUCAO;
        }
        if (this == EM_EXECUCAO) {
            return RESOLVIDO;
        }
        if (this == RESOLVIDO) {
            return ENCERRADO;
        }
        return null;
    }

    public void validarTransicaoPara(StatusSolicitacao novo) {
        if (this == novo) {
            throw new ValidacaoException("A solicitação já está com este status.");
        }

        StatusSolicitacao proximoPermitido = obterProximo();
        if (proximoPermitido == null) {
            throw new ValidacaoException("Não é possível alterar o status de uma solicitação encerrada.");
        }

        if (novo != proximoPermitido) {
            throw new ValidacaoException(
                    "Transição inválida: de " + this + " só é permitido ir para " + proximoPermitido + "."
            );
        }
    }
}
