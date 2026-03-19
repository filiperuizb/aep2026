package org.boligon.ui;

import org.boligon.entity.Usuario;
import org.boligon.entity.PerfilUsuario;

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

        switch (usuarioLogado.getPerfil()) {
            case CIDADAO:
                System.out.println("\n[1] Nova Solicitação");
                System.out.println("[2] Acompanhar Solicitação");
                System.out.println("[3] Minhas Solicitações");
                System.out.println("[0] Sair");
                break;
            case GESTOR:
                System.out.println("\n[1] Listar Solicitações");
                System.out.println("[2] Atualizar Status");
                System.out.println("[3] Filtrar por Prioridade");
                System.out.println("[0] Sair");
                break;
            case ANONIMO:
                System.out.println("\n[1] Enviar Solicitação Anônima");
                System.out.println("[0] Sair");
                break;
        }

        System.out.print("\nOpção: ");
    }

    private void processarOpcao(String opcao) {
        switch (usuarioLogado.getPerfil()) {
            case CIDADAO:
                processarMenuCidadao(opcao);
                break;
            case GESTOR:
                processarMenuGestor(opcao);
                break;
            case ANONIMO:
                processarMenuAnonimo(opcao);
                break;
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

    private void processarMenuAnonimo(String opcao) {
        switch (opcao) {
            case "1" -> System.out.println("\n▶ [Em desenvolvimento]");
            case "0" -> {
                System.out.println("\nEncerrando...");
                executando = false;
            }
            default -> System.out.println("\nOpção inválida!");
        }
    }
}
