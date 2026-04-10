package org.boligon.ui;

import org.boligon.entity.HistoricoStatus;
import org.boligon.entity.Solicitacao;
import org.boligon.enums.StatusSolicitacao;
import org.boligon.service.HistoricoStatusService;
import org.boligon.service.SolicitacaoService;

import java.util.List;
import java.util.Scanner;

public class AcompanhamentoUI {

    private final Scanner scanner;
    private final SolicitacaoService solicitacaoService = new SolicitacaoService();
    private final HistoricoStatusService historicoStatusService = new HistoricoStatusService();

    public AcompanhamentoUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void executar() {
        System.out.println("\n--- ACOMPANHAR SOLICITAÇÃO ---");
        System.out.print("Digite o protocolo: ");
        String protocolo = scanner.nextLine().trim();

        try {
            Solicitacao solicitacao = solicitacaoService.buscarPorProtocolo(protocolo);
            imprimirDetalhes(solicitacao);
            System.out.println("\n--- HISTÓRICO DE ALTERAÇÕES ---");
            List<HistoricoStatus> historico = historicoStatusService.listarPorSolicitacaoId(solicitacao.getId());
            imprimirHistorico(historico);
        } catch (Exception e) {
            System.out.println("\n✗ " + e.getMessage());
        }

        parar();
    }

    public static void imprimirDetalhes(Solicitacao solicitacao) {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║       DETALHES DA SOLICITAÇÃO         ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println("\nProtocolo: " + solicitacao.getProtocolo());
        System.out.println("Categoria: " + solicitacao.getCategoria().getValor());
        System.out.println("Descrição: " + solicitacao.getDescricao());
        if (solicitacao.getAnexo() != null && !solicitacao.getAnexo().isBlank()) {
            System.out.println("Anexo: " + solicitacao.getAnexo());
        }
        System.out.println("Localização: " + solicitacao.getLocalizacao());
        System.out.println("Bairro: " + solicitacao.getBairro());
        System.out.println("Prioridade: " + solicitacao.getPrioridade() + " (SLA: " + solicitacao.getPrioridade().getDiasSla() + " dias)");
        System.out.println("Impacto social (referência): " + solicitacao.getPrioridade().getImpactoSocial());
        System.out.println("Status: " + solicitacao.getStatus());
        System.out.println("Data de Criação: " + solicitacao.exibirDataCriacaoFormatada());
        System.out.println("Prazo SLA: " + solicitacao.exibirPrazoSlaFormatada());
        if (solicitacao.isForaDoPrazoSla() && solicitacao.getStatus() != StatusSolicitacao.ENCERRADO) {
            System.out.println("Situação: fora do prazo do SLA");
        }
        if (solicitacao.getJustificativaAtraso() != null && !solicitacao.getJustificativaAtraso().isBlank()) {
            System.out.println("Justificativa de atraso: " + solicitacao.getJustificativaAtraso());
        }
        if (solicitacao.isAnonima()) {
            System.out.println("Autor: Anônimo");
        }
    }

    public static void imprimirHistorico(List<HistoricoStatus> historico) {
        if (historico.isEmpty()) {
            System.out.println("Nenhuma alteração registrada.");
            return;
        }
        for (HistoricoStatus h : historico) {
            System.out.println("\n├─ Data: " + h.getDataMovimentacao());
            System.out.println("├─ Status: " + h.getStatusAnterior() + " → " + h.getStatusNovo());
            System.out.println("├─ Responsável: " + (h.getNomeResponsavel() != null ? h.getNomeResponsavel() : ("id " + h.getResponsavelId())));
            System.out.println("└─ Comentário: " + h.getComentario());
        }
    }

    private void parar() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }
}
