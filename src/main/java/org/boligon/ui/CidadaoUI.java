package org.boligon.ui;

import org.boligon.entity.Solicitacao;
import org.boligon.entity.Usuario;
import org.boligon.enums.Categoria;
import org.boligon.enums.Prioridade;
import org.boligon.service.SolicitacaoService;

import java.util.Scanner;

public class CidadaoUI {

    private final Scanner scanner = new Scanner(System.in);
    private final SolicitacaoService solicitacaoService = new SolicitacaoService();
    private final Usuario usuarioLogado;

    public CidadaoUI(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public void exibirMenuCidadao() {
        boolean executando = true;
        while (executando) {
            System.out.println("\n╔═══════════════════════════════════════╗");
            System.out.println("║       MENU - CIDADÃO/ANÔNIMO          ║");
            System.out.println("╚═══════════════════════════════════════╝");
            System.out.println("\n[1] Enviar Nova Solicitação");
            System.out.println("[2] Acompanhar Solicitação");
            System.out.println("[3] Minhas Solicitações");
            System.out.println("[0] Voltar");
            System.out.print("\nOpção: ");

            String opcao = scanner.nextLine().trim();

            switch (opcao) {
                case "1":
                    criarSolicitacao();
                    break;
                case "2":
                    acompanharSolicitacao();
                    break;
                case "3":
                    if (!usuarioLogado.getPerfil().equals(org.boligon.enums.PerfilUsuario.ANONIMO)) {
                        minhasSolicitacoes();
                    } else {
                        System.out.println("\n✗ Usuários anônimos não podem visualizar histórico.");
                        parar();
                    }
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

    private void criarSolicitacao() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║    CRIAR NOVA SOLICITAÇÃO             ║");
        System.out.println("╚═══════════════════════════════════════╝");

        try {
            Solicitacao novaSolicitacao = new Solicitacao();

            // Categoria
            novaSolicitacao.setCategoria(selecionarCategoria());

            // Descrição
            System.out.print("\nDescrição do problema: ");
            String descricao = scanner.nextLine().trim();
            novaSolicitacao.setDescricao(descricao);

            // Localização
            System.out.print("Localização (rua e número): ");
            String localizacao = scanner.nextLine().trim();
            novaSolicitacao.setLocalizacao(localizacao);

            // Bairro
            System.out.print("Bairro: ");
            String bairro = scanner.nextLine().trim();
            novaSolicitacao.setBairro(bairro);

            // Prioridade
            novaSolicitacao.setPrioridade(selecionarPrioridade());

            // Define se é anônima
            boolean isAnonimo = usuarioLogado.getPerfil().equals(org.boligon.enums.PerfilUsuario.ANONIMO);
            novaSolicitacao.setAnonima(isAnonimo);

            // Criar solicitação
            solicitacaoService.criarSolicitacao(novaSolicitacao, usuarioLogado);

            System.out.println("\n✓ Solicitação criada com sucesso!");
            System.out.println("├─ Protocolo: " + novaSolicitacao.getProtocolo());
            System.out.println("├─ Categoria: " + novaSolicitacao.getCategoria().getValor());
            System.out.println("├─ Bairro: " + novaSolicitacao.getBairro());
            System.out.println("└─ Status: " + novaSolicitacao.getStatus());

            parar();

        } catch (Exception e) {
            System.out.println("\n✗ Erro ao criar solicitação: " + e.getMessage());
            parar();
        }
    }

    private Categoria selecionarCategoria() {
        while (true) {
            System.out.println("\nSelecione a categoria:");
            Categoria[] categorias = Categoria.values();
            for (int i = 0; i < categorias.length; i++) {
                System.out.println("[" + (i + 1) + "] " + categorias[i].getValor());
            }
            System.out.print("\nOpção: ");

            try {
                int opcao = Integer.parseInt(scanner.nextLine().trim());
                if (opcao > 0 && opcao <= categorias.length) {
                    return categorias[opcao - 1];
                } else {
                    System.out.println("✗ Opção inválida!");
                }
            } catch (NumberFormatException e) {
                System.out.println("✗ Digite um número válido!");
            }
        }
    }

    private Prioridade selecionarPrioridade() {
        while (true) {
            System.out.println("\nSelecione a prioridade:");
            Prioridade[] prioridades = Prioridade.values();
            for (int i = 0; i < prioridades.length; i++) {
                System.out.println("[" + (i + 1) + "] " + prioridades[i] + " (" + prioridades[i].getDiasSla() + " dias)");
            }
            System.out.print("\nOpção: ");

            try {
                int opcao = Integer.parseInt(scanner.nextLine().trim());
                if (opcao > 0 && opcao <= prioridades.length) {
                    return prioridades[opcao - 1];
                } else {
                    System.out.println("✗ Opção inválida!");
                }
            } catch (NumberFormatException e) {
                System.out.println("✗ Digite um número válido!");
            }
        }
    }

    private void acompanharSolicitacao() {
        System.out.println("\n--- ACOMPANHAR SOLICITAÇÃO ---");
        System.out.print("Digite o protocolo: ");
        String protocolo = scanner.nextLine().trim();

        try {
            Solicitacao solicitacao = solicitacaoService.buscarPorProtocolo(protocolo);
            exibirDetalhes(solicitacao);
        } catch (Exception e) {
            System.out.println("\n✗ " + e.getMessage());
        }

        parar();
    }

    private void minhasSolicitacoes() {
        System.out.println("\n--- MINHAS SOLICITAÇÕES ---");

        try {
            var solicitacoes = solicitacaoService.listarPorUsuarioId(usuarioLogado.getId());

            if (solicitacoes.isEmpty()) {
                System.out.println("Você não possui solicitações.");
            } else {
                for (Solicitacao s : solicitacoes) {
                    System.out.println("\n├─ Protocolo: " + s.getProtocolo());
                    System.out.println("├─ Categoria: " + s.getCategoria().getValor());
                    System.out.println("├─ Bairro: " + s.getBairro());
                    System.out.println("├─ Status: " + s.getStatus());
                    System.out.println("└─ Data: " + s.exibirDataCriacaoFormatada());
                }
            }
        } catch (Exception e) {
            System.out.println("✗ Erro ao listar solicitações: " + e.getMessage());
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
        System.out.println("Status: " + solicitacao.getStatus());
        System.out.println("Data de Criação: " + solicitacao.exibirDataCriacaoFormatada());
        System.out.println("Prazo SLA: " + solicitacao.exibirPrazoSlaFormatada());

        if (solicitacao.isAnonima()) {
            System.out.println("Autor: Anônimo");
        }
    }

    private void parar() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }
}

