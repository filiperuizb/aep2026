package org.boligon.shared.model;

import org.boligon.solicitacao.domain.Solicitacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FilaAtendimento {

    private final List<Solicitacao> solicitacoesOrdenadas;

    public FilaAtendimento(List<Solicitacao> solicitacoes) {
        List<Solicitacao> copia = new ArrayList<>(solicitacoes);
        copia.sort(new OrdenadorPorPrioridade());
        this.solicitacoesOrdenadas = Collections.unmodifiableList(copia);
    }

    public List<Solicitacao> getSolicitacoesOrdenadas() {
        return solicitacoesOrdenadas;
    }

    private static class OrdenadorPorPrioridade implements Comparator<Solicitacao> {

        @Override
        public int compare(Solicitacao a, Solicitacao b) {
            int prioridade = Integer.compare(
                    a.getPrioridade().getDiasSla(),
                    b.getPrioridade().getDiasSla()
            );
            if (prioridade != 0) {
                return prioridade;
            }

            if (a.getDataCriacao() == null && b.getDataCriacao() == null) {
                return 0;
            }
            if (a.getDataCriacao() == null) {
                return 1;
            }
            if (b.getDataCriacao() == null) {
                return -1;
            }
            return a.getDataCriacao().compareTo(b.getDataCriacao());
        }
    }
}
