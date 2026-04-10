package org.boligon.ui;

import org.boligon.entity.Solicitacao;
import org.boligon.entity.Usuario;
import org.boligon.enums.Categoria;
import org.boligon.enums.PerfilUsuario;
import org.boligon.enums.Prioridade;
import org.boligon.enums.StatusSolicitacao;
import org.boligon.service.SolicitacaoService;

import java.util.Scanner;

public class CidadaoUI {

    private final Scanner scanner;
    private final SolicitacaoService solicitacaoService = new SolicitacaoService();
    private final Usuario usuarioLogado;

    public CidadaoUI(Usuario usuarioLogado, Scanner scanner) {
        this.usuarioLogado = usuarioLogado;
        this.scanner = scanner;
    }

    public void executarNovaSolicitacao() {
        criarSolicitacao();
    }

    public void executarAcompanhamento() {
        new AcompanhamentoUI(scanner).executar();
    }

    public void executarMinhasSolicitacoes() {
        if (usuarioLogado.getPerfil().equals(PerfilUsuario.ANONIMO)) {
            System.out.println("\n✗ Usuários anônimos não podem visualizar histórico.");
            parar();
            return;
        }
        minhasSolicitacoes();
    }

    private void criarSolicitacao() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║    CRIAR NOVA SOLICITAÇÃO             ║");
        System.out.println("╚═══════════════════════════════════════╝");

        try {
            Solicitacao novaSolicitacao = new Solicitacao();

            novaSolicitacao.setCategoria(selecionarCategoria());

            System.out.print("\nDescrição do problema: ");
            String descricao = scanner.nextLine().trim();
            novaSolicitacao.setDescricao(descricao);

            System.out.print("Caminho ou nome do anexo (opcional, Enter para pular): ");
            String anexo = scanner.nextLine().trim();
            if (!anexo.isEmpty()) {
                novaSolicitacao.setAnexo(anexo);
            }

            System.out.print("Localização (rua e número): ");
            String localizacao = scanner.nextLine().trim();
            novaSolicitacao.setLocalizacao(localizacao);

            System.out.print("Bairro: ");
            String bairro = scanner.nextLine().trim();
            novaSolicitacao.setBairro(bairro);

            novaSolicitacao.setPrioridade(selecionarPrioridade());

            boolean isAnonimo;
            if (usuarioLogado.getPerfil().equals(PerfilUsuario.ANONIMO)) {
                isAnonimo = true;
            } else {
                System.out.print("\nEnviar de forma anônima? [s/N]: ");
                String resp = scanner.nextLine().trim().toLowerCase();
                isAnonimo = resp.equals("s") || resp.equals("sim");
            }
            novaSolicitacao.setAnonima(isAnonimo);

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
                System.out.println("[" + (i + 1) + "] " + prioridades[i]
                        + " (" + prioridades[i].getDiasSla() + " dias) — "
                        + prioridades[i].getImpactoSocial());
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
                    System.out.println("├─ Prazo SLA: " + s.exibirPrazoSlaFormatada());
                    if (s.isForaDoPrazoSla() && s.getStatus() != StatusSolicitacao.ENCERRADO) {
                        System.out.println("├─ Situação: fora do prazo");
                    }
                    System.out.println("└─ Data: " + s.exibirDataCriacaoFormatada());
                }
            }
        } catch (Exception e) {
            System.out.println("✗ Erro ao listar solicitações: " + e.getMessage());
        }

        parar();
    }

    private void parar() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }
}
