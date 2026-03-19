package org.boligon.ui;

import org.boligon.entity.Usuario;

import java.util.Scanner;

public class MenuPrincipalUI {

    private final Scanner scanner = new Scanner(System.in);
    private final Usuario usuarioLogado;
    private boolean executando = true;

    public MenuPrincipalUI(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public void executar() {
        while (executando) {
            exibirMenu();
            String opcao = scanner.nextLine().trim();
            processarOpcao(opcao);
        }
    }

    private void exibirMenu() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║  Bem-vindo, " + usuarioLogado.getNome());
        System.out.println("║  Perfil: " + usuarioLogado.getPerfil());
        System.out.println("╚════════════════════════════════════╝");

        if (usuarioLogado.getPerfil().equals("CIDADAO")) {
            System.out.println("\n[1] Nova Solicitação");
            System.out.println("[2] Acompanhar Solicitação");
            System.out.println("[3] Minhas Solicitações");
            System.out.println("[0] Sair");
        } else if (usuarioLogado.getPerfil().equals("GESTOR")) {
            System.out.println("\n[1] Listar Solicitações");
            System.out.println("[2] Atualizar Status");
            System.out.println("[3] Filtrar por Prioridade");
            System.out.println("[0] Sair");
        }

        System.out.print("\nOpção: ");
    }

    private void processarOpcao(String opcao) {
        if (usuarioLogado.getPerfil().equals("CIDADAO")) {
            processarMenuCidadao(opcao);
        } else if (usuarioLogado.getPerfil().equals("GESTOR")) {
            processarMenuGestor(opcao);
        }
    }

    private void processarMenuCidadao(String opcao) {
        switch (opcao) {
            case "1" -> System.out.println("\n▶ [Em desenvolvimento]");
            case "2" -> System.out.println("\n▶ [Em desenvolvimento]");
            case "3" -> System.out.println("\n▶ [Em desenvolvimento]");
            case "0" -> {
                System.out.println("\nEncerrando...");
                executando = false;
            }
            default -> System.out.println("\nOpção inválida!");
        }
    }

    private void processarMenuGestor(String opcao) {
        switch (opcao) {
            case "1" -> System.out.println("\n▶ [Em desenvolvimento]");
            case "2" -> System.out.println("\n▶ [Em desenvolvimento]");
            case "3" -> System.out.println("\n▶ [Em desenvolvimento]");
            case "0" -> {
                System.out.println("\nEncerrando...");
                executando = false;
            }
            default -> System.out.println("\nOpção inválida!");
        }
    }
}
