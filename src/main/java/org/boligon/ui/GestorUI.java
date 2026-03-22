package org.boligon.ui;

import org.boligon.dto.HistoricoStatusDTO;
import org.boligon.entity.Solicitacao;
import org.boligon.entity.Usuario;
import org.boligon.enums.Categoria;
import org.boligon.enums.Prioridade;
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
            System.out.println("[2] Filtrar por Prioridade");
            System.out.println("[3] Filtrar por Status");
            System.out.println("[4] Filtrar por Bairro");
            System.out.println("[5] Filtrar por Categoria");
            System.out.println("[6] Alterar Status da Solicitação");
            System.out.println("[7] Visualizar Histórico");
            System.out.println("[0] Voltar");
            System.out.print("\nOpção: ");

            String opcao = scanner.nextLine().trim();

            switch (opcao) {
                case "1":
                    listarTodasSolicitacoes();
                    break;
                case "2":
                    filtrarPorPrioridade();
                    break;
                case "3":
                    filtrarPorStatus();
                    break;
                case "4":
                    filtrarPorBairro();
                    break;
                case "5":
                    filtrarPorCategoria();
                    break;
                case "6":
                    alterarStatusSolicitacao();
                    break;
                case "7":
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

    private void filtrarPorPrioridade() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║   FILTRAR POR PRIORIDADE              ║");
        System.out.println("╚═══════════════════════════════════════╝");

        try {
            Prioridade[] prioridades = Prioridade.values();
            System.out.println("\nSelecione a prioridade:");
            for (int i = 0; i < prioridades.length; i++) {
                System.out.println("[" + (i + 1) + "] " + prioridades[i]);
            }
            System.out.print("\nOpção: ");

            int opcao = Integer.parseInt(scanner.nextLine().trim());
            if (opcao < 1 || opcao > prioridades.length) {
                System.out.println("\n✗ Opção inválida!");
                parar();
                return;
            }

            Prioridade prioridade = prioridades[opcao - 1];
            var solicitacoes = solicitacaoService.listarPorPrioridade(prioridade);

            exibirListaFiltered(solicitacoes, "Prioridade: " + prioridade);

        } catch (NumberFormatException e) {
            System.out.println("\n✗ Por favor, digite um número válido!");
            parar();
        } catch (Exception e) {
            System.out.println("\n✗ Erro ao filtrar: " + e.getMessage());
            parar();
        }
    }

    private void filtrarPorStatus() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║   FILTRAR POR STATUS                 ║");
        System.out.println("╚═══════════════════════════════════════╝");

        try {
            StatusSolicitacao[] statuses = StatusSolicitacao.values();
            System.out.println("\nSelecione o status:");
            for (int i = 0; i < statuses.length; i++) {
                System.out.println("[" + (i + 1) + "] " + statuses[i]);
            }
            System.out.print("\nOpção: ");

            int opcao = Integer.parseInt(scanner.nextLine().trim());
            if (opcao < 1 || opcao > statuses.length) {
                System.out.println("\n✗ Opção inválida!");
                parar();
                return;
            }

            StatusSolicitacao status = statuses[opcao - 1];
            var solicitacoes = solicitacaoService.listarPorStatus(status);

            exibirListaFiltered(solicitacoes, "Status: " + status);

        } catch (NumberFormatException e) {
            System.out.println("\n✗ Por favor, digite um número válido!");
            parar();
        } catch (Exception e) {
            System.out.println("\n✗ Erro ao filtrar: " + e.getMessage());
            parar();
        }
    }

    private void filtrarPorBairro() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║   FILTRAR POR BAIRRO                 ║");
        System.out.println("╚═══════════════════════════════════════╝");

        try {
            System.out.print("\nDigite o nome do bairro: ");
            String bairro = scanner.nextLine().trim();

            if (bairro.isEmpty()) {
                System.out.println("\n✗ Bairro não pode estar vazio!");
                parar();
                return;
            }

            var solicitacoes = solicitacaoService.listarPorBairro(bairro);

            exibirListaFiltered(solicitacoes, "Bairro: " + bairro);

        } catch (Exception e) {
            System.out.println("\n✗ Erro ao filtrar: " + e.getMessage());
            parar();
        }
    }

    private void filtrarPorCategoria() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║   FILTRAR POR CATEGORIA              ║");
        System.out.println("╚═══════════════════════════════════════╝");

        try {
            Categoria[] categorias = Categoria.values();
            System.out.println("\nSelecione a categoria:");
            for (int i = 0; i < categorias.length; i++) {
                System.out.println("[" + (i + 1) + "] " + categorias[i].getValor());
            }
            System.out.print("\nOpção: ");

            int opcao = Integer.parseInt(scanner.nextLine().trim());
            if (opcao < 1 || opcao > categorias.length) {
                System.out.println("\n✗ Opção inválida!");
                parar();
                return;
            }

            Categoria categoria = categorias[opcao - 1];
            var solicitacoes = solicitacaoService.listarPorCategoria(categoria);

            exibirListaFiltered(solicitacoes, "Categoria: " + categoria.getValor());

        } catch (NumberFormatException e) {
            System.out.println("\n✗ Por favor, digite um número válido!");
            parar();
        } catch (Exception e) {
            System.out.println("\n✗ Erro ao filtrar: " + e.getMessage());
            parar();
        }
    }

    private void exibirListaFiltered(java.util.List<Solicitacao> solicitacoes, String titulo) {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║   RESULTADO DA BUSCA                 ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println("\n" + titulo);

        if (solicitacoes.isEmpty()) {
            System.out.println("\nNenhuma solicitação encontrada.");
        } else {
            System.out.println("\n[" + solicitacoes.size() + " resultado(s)]");
            for (Solicitacao s : solicitacoes) {
                System.out.println("\n├─ Protocolo: " + s.getProtocolo());
                System.out.println("├─ Categoria: " + s.getCategoria().getValor());
                System.out.println("├─ Bairro: " + s.getBairro());
                System.out.println("├─ Prioridade: " + s.getPrioridade());
                System.out.println("├─ Status: " + s.getStatus());
                System.out.println("└─ Data: " + s.getDataCriacao());
            }
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

