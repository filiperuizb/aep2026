package org.boligon.ui;

import org.boligon.dto.HistoricoStatusDTO;
import org.boligon.entity.Solicitacao;
import org.boligon.entity.Usuario;
import org.boligon.enums.StatusSolicitacao;
import org.boligon.service.HistoricoStatusService;
import org.boligon.service.SolicitacaoService;

import java.util.Scanner;

public class GestorUI {

    private final Scanner scanner = new Scanner(System.in);
    private final SolicitacaoService solicitacaoService = new SolicitacaoService();
    private final HistoricoStatusService historicoStatusService = new HistoricoStatusService();
    private final Usuario usuarioLogado;

    public GestorUI(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public void exibirMenuGestor() {
        boolean executando = true;
        while (executando) {
            System.out.println("\n╔═══════════════════════════════════════╗");
            System.out.println("║       MENU - GESTOR                   ║");
            System.out.println("╚═══════════════════════════════════════╝");
            System.out.println("\n[1] Listar Todas as Solicitações");
            System.out.println("[2] Alterar Status da Solicitação");
            System.out.println("[3] Visualizar Histórico");
            System.out.println("[0] Voltar");
            System.out.print("\nOpção: ");

            String opcao = scanner.nextLine().trim();

            switch (opcao) {
                case "1":
                    listarTodasSolicitacoes();
                    break;
                case "2":
                    alterarStatusSolicitacao();
                    break;
                case "3":
                    visualizarHistorico();
                    break;
                case "0":
                    executando = false;
                    break;
                default:
                    System.out.println("\n✗ Opção inválida!");
                    parar();
            }
        }
    }

    private void listarTodasSolicitacoes() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║    TODAS AS SOLICITAÇÕES              ║");
        System.out.println("╚═══════════════════════════════════════╝");

        try {
            var solicitacoes = solicitacaoService.listarTodas();

            if (solicitacoes.isEmpty()) {
                System.out.println("\nNenhuma solicitação encontrada.");
            } else {
                for (Solicitacao s : solicitacoes) {
                    System.out.println("\n├─ Protocolo: " + s.getProtocolo());
                    System.out.println("├─ Categoria: " + s.getCategoria().getValor());
                    System.out.println("├─ Bairro: " + s.getBairro());
                    System.out.println("├─ Prioridade: " + s.getPrioridade());
                    System.out.println("├─ Status: " + s.getStatus());
                    System.out.println("└─ Data: " + s.getDataCriacao());
                }
            }
        } catch (Exception e) {
            System.out.println("\n✗ Erro ao listar solicitações: " + e.getMessage());
        }

        parar();
    }

    private void alterarStatusSolicitacao() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║      ALTERAR STATUS DE SOLICITAÇÃO    ║");
        System.out.println("╚═══════════════════════════════════════╝");

        try {
            System.out.print("\nDigite o protocolo da solicitação: ");
            String protocolo = scanner.nextLine().trim();

            Solicitacao solicitacao = solicitacaoService.buscarPorProtocolo(protocolo);

            exibirDetalhes(solicitacao);

            System.out.println("\nSelecione o novo status:");
            StatusSolicitacao[] statusDispon = StatusSolicitacao.values();
            for (int i = 0; i < statusDispon.length; i++) {
                System.out.println("[" + (i + 1) + "] " + statusDispon[i]);
            }
            System.out.print("\nOpção: ");

            int opcaoStatus = Integer.parseInt(scanner.nextLine().trim());
            if (opcaoStatus < 1 || opcaoStatus > statusDispon.length) {
                System.out.println("\n✗ Opção inválida!");
                parar();
                return;
            }

            StatusSolicitacao novoStatus = statusDispon[opcaoStatus - 1];

            System.out.print("\nComentário (obrigatório): ");
            String comentario = scanner.nextLine().trim();

            if (comentario.isEmpty()) {
                System.out.println("\n✗ Comentário é obrigatório!");
                parar();
                return;
            }

            HistoricoStatusDTO dto = new HistoricoStatusDTO();
            dto.setSolicitacaoId(solicitacao.getId());
            dto.setStatusNovo(novoStatus);
            dto.setComentario(comentario);
            dto.setResponsavelId(usuarioLogado.getId());

            solicitacaoService.atualizarStatus(dto);

            System.out.println("\n✓ Status atualizado com sucesso!");
            System.out.println("├─ Protocolo: " + solicitacao.getProtocolo());
            System.out.println("├─ Status Anterior: " + solicitacao.getStatus());
            System.out.println("└─ Novo Status: " + novoStatus);

            parar();

        } catch (Exception e) {
            System.out.println("\n✗ Erro ao atualizar status: " + e.getMessage());
            parar();
        }
    }

    private void visualizarHistorico() {
        System.out.println("\n--- VISUALIZAR HISTÓRICO ---");
        System.out.print("Digite o protocolo da solicitação: ");
        String protocolo = scanner.nextLine().trim();

        try {
            Solicitacao solicitacao = solicitacaoService.buscarPorProtocolo(protocolo);
            var historico = historicoStatusService.listarPorSolicitacaoId(solicitacao.getId());

            System.out.println("\n╔═══════════════════════════════════════╗");
            System.out.println("║       HISTÓRICO DE ALTERAÇÕES         ║");
            System.out.println("╚═══════════════════════════════════════╝");

            if (historico.isEmpty()) {
                System.out.println("\nNenhuma alteração registrada.");
            } else {
                for (var h : historico) {
                    System.out.println("\n├─ Data: " + h.getDataMovimentacao());
                    System.out.println("├─ Status: " + h.getStatusAnterior() + " → " + h.getStatusNovo());
                    System.out.println("└─ Comentário: " + h.getComentario());
                }
            }
        } catch (Exception e) {
            System.out.println("\n✗ " + e.getMessage());
        }

        parar();
    }

    private void exibirDetalhes(Solicitacao solicitacao) {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║       DETALHES DA SOLICITAÇÃO         ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println("\nProtocolo: " + solicitacao.getProtocolo());
        System.out.println("Categoria: " + solicitacao.getCategoria().getValor());
        System.out.println("Descrição: " + solicitacao.getDescricao());
        System.out.println("Localização: " + solicitacao.getLocalizacao());
        System.out.println("Bairro: " + solicitacao.getBairro());
        System.out.println("Prioridade: " + solicitacao.getPrioridade());
        System.out.println("Status Atual: " + solicitacao.getStatus());
        System.out.println("Data de Criação: " + solicitacao.getDataCriacao());
        System.out.println("Prazo SLA: " + solicitacao.getPrazoSla());
    }

    private void parar() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }
}

