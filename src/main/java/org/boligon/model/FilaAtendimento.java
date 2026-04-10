package org.boligon.model;

import org.boligon.entity.Solicitacao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FilaAtendimento {

    private final List<Solicitacao> solicitacoesOrdenadas;

    public FilaAtendimento(List<Solicitacao> solicitacoes) {
        List<Solicitacao> copia = new ArrayList<>(solicitacoes);
        copia.sort(Comparator
                .comparing((Solicitacao s) -> s.getPrioridade().getDiasSla())
                .thenComparing(Solicitacao::getDataCriacao, Comparator.nullsLast(Comparator.naturalOrder())));
        this.solicitacoesOrdenadas = List.copyOf(copia);
    }

    public List<Solicitacao> getSolicitacoesOrdenadas() {
        return solicitacoesOrdenadas;
    }
}
