package org.boligon.ui;

import org.boligon.dto.FiltroSolicitacaoDTO;
import org.boligon.dto.HistoricoStatusDTO;
import org.boligon.entity.Solicitacao;
import org.boligon.entity.Usuario;
import org.boligon.enums.Categoria;
import org.boligon.enums.Prioridade;
import org.boligon.enums.StatusSolicitacao;
import org.boligon.service.HistoricoStatusService;
import org.boligon.service.SolicitacaoService;

import java.time.LocalDateTime;
import java.util.Scanner;

public class GestorUI {

    private final Scanner scanner;
    private final SolicitacaoService solicitacaoService = new SolicitacaoService();
    private final HistoricoStatusService historicoStatusService = new HistoricoStatusService();
    private final Usuario usuarioLogado;

    public GestorUI(Usuario usuarioLogado, Scanner scanner) {
        this.usuarioLogado = usuarioLogado;
        this.scanner = scanner;
    }

    public void exibirMenuGestor() {
        boolean executando = true;
        while (executando) {
            System.out.println("\n╔═══════════════════════════════════════╗");
            System.out.println("║       MENU - GESTOR                   ║");
            System.out.println("╚═══════════════════════════════════════╝");
            System.out.println("\n[1] Listar fila de atendimento (por prioridade)");
            System.out.println("[2] Filtrar por Prioridade");
            System.out.println("[3] Filtrar por Status");
            System.out.println("[4] Filtrar por Bairro");
            System.out.println("[5] Filtrar por Categoria");
            System.out.println("[6] Filtro combinado");
            System.out.println("[7] Alterar Status da Solicitação");
            System.out.println("[8] Visualizar Histórico");
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
                    filtroCombinado();
                    break;
                case "7":
                    alterarStatusSolicitacao();
                    break;
                case "8":
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
        System.out.println("║    FILA DE ATENDIMENTO                ║");
        System.out.println("╚═══════════════════════════════════════╝");

        try {
            var solicitacoes = solicitacaoService.obterFilaDeAtendimento().getSolicitacoesOrdenadas();

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

    private void filtroCombinado() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║   FILTRO COMBINADO                    ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println("\nDeixe em branco ou use 0 para ignorar cada critério.");

        try {
            FiltroSolicitacaoDTO filtro = new FiltroSolicitacaoDTO();

            System.out.print("\nBairro (trecho do nome, vazio ignora): ");
            String bairro = scanner.nextLine().trim();
            if (!bairro.isEmpty()) {
                filtro.setBairro(bairro);
            }

            Prioridade[] prioridades = Prioridade.values();
            System.out.println("\nPrioridade [0] ignorar");
            for (int i = 0; i < prioridades.length; i++) {
                System.out.println("[" + (i + 1) + "] " + prioridades[i]);
            }
            System.out.print("Opção: ");
            String opP = scanner.nextLine().trim();
            if (!opP.isEmpty() && !opP.equals("0")) {
                int ip = Integer.parseInt(opP);
                if (ip >= 1 && ip <= prioridades.length) {
                    filtro.setPrioridade(prioridades[ip - 1]);
                }
            }

            Categoria[] categorias = Categoria.values();
            System.out.println("\nCategoria [0] ignorar");
            for (int i = 0; i < categorias.length; i++) {
                System.out.println("[" + (i + 1) + "] " + categorias[i].getValor());
            }
            System.out.print("Opção: ");
            String opC = scanner.nextLine().trim();
            if (!opC.isEmpty() && !opC.equals("0")) {
                int ic = Integer.parseInt(opC);
                if (ic >= 1 && ic <= categorias.length) {
                    filtro.setCategoria(categorias[ic - 1]);
                }
            }

            StatusSolicitacao[] statuses = StatusSolicitacao.values();
            System.out.println("\nStatus [0] ignorar");
            for (int i = 0; i < statuses.length; i++) {
                System.out.println("[" + (i + 1) + "] " + statuses[i]);
            }
            System.out.print("Opção: ");
            String opS = scanner.nextLine().trim();
            if (!opS.isEmpty() && !opS.equals("0")) {
                int is = Integer.parseInt(opS);
                if (is >= 1 && is <= statuses.length) {
                    filtro.setStatus(statuses[is - 1]);
                }
            }

            var solicitacoes = solicitacaoService.filtrar(filtro);
            exibirListaFiltered(solicitacoes, "Filtro combinado");

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

    private StatusSolicitacao obterProximoStatusNaFila(StatusSolicitacao atual) {
        return switch (atual) {
            case ABERTO -> StatusSolicitacao.TRIAGEM;
            case TRIAGEM -> StatusSolicitacao.EM_EXECUCAO;
            case EM_EXECUCAO -> StatusSolicitacao.RESOLVIDO;
            case RESOLVIDO -> StatusSolicitacao.ENCERRADO;
            case ENCERRADO -> null;
        };
    }

    private void alterarStatusSolicitacao() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║      ALTERAR STATUS DE SOLICITAÇÃO    ║");
        System.out.println("╚═══════════════════════════════════════╝");

        try {
            System.out.print("\nDigite o protocolo da solicitação: ");
            String protocolo = scanner.nextLine().trim();

            Solicitacao solicitacao = solicitacaoService.buscarPorProtocolo(protocolo);

            AcompanhamentoUI.imprimirDetalhes(solicitacao);

            StatusSolicitacao statusAnterior = solicitacao.getStatus();
            StatusSolicitacao novoStatus = obterProximoStatusNaFila(statusAnterior);

            if (novoStatus == null) {
                System.out.println("\n✗ Esta solicitação já está encerrada; não há próximo status.");
                parar();
                return;
            }

            System.out.println("\nPróximo status na fila: " + novoStatus);
            System.out.print("Confirmar alteração? [S/n]: ");
            String conf = scanner.nextLine().trim().toLowerCase();
            if (conf.equals("n") || conf.equals("nao")) {
                parar();
                return;
            }

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

            if (LocalDateTime.now().isAfter(solicitacao.getPrazoSla())) {
                System.out.print("\nJustificativa do atraso em relação ao SLA (obrigatória): ");
                String just = scanner.nextLine().trim();
                if (just.isEmpty()) {
                    System.out.println("\n✗ Justificativa de atraso é obrigatória quando o prazo SLA já passou.");
                    parar();
                    return;
                }
                dto.setJustificativaAtraso(just);
            }

            solicitacaoService.atualizarStatus(dto);

            Solicitacao atualizada = solicitacaoService.buscarPorProtocolo(protocolo);

            System.out.println("\n✓ Status atualizado com sucesso!");
            System.out.println("├─ Protocolo: " + atualizada.getProtocolo());
            System.out.println("├─ Status Anterior: " + statusAnterior);
            System.out.println("└─ Novo Status: " + atualizada.getStatus());

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

            AcompanhamentoUI.imprimirHistorico(historico);
        } catch (Exception e) {
            System.out.println("\n✗ " + e.getMessage());
        }

        parar();
    }

    private void parar() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }
}
