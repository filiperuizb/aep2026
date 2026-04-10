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
        String perfilTexto = obterPerfilFormatado();
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║  Bem-vindo, " + usuarioLogado.getNome());
        System.out.println("║  Perfil: " + perfilTexto);
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
                System.out.println("[2] Acompanhar por protocolo");
                System.out.println("[0] Sair");
                break;
        }

        System.out.print("\nOpção: ");
    }

    private String obterPerfilFormatado() {
        return switch (usuarioLogado.getPerfil()) {
            case CIDADAO -> "Cidadão";
            case GESTOR -> "Gestor";
            case ANONIMO -> "Anônimo";
        };
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
            case "1", "2", "3" -> {
                CidadaoUI cidadaoUI = new CidadaoUI(usuarioLogado);
                cidadaoUI.exibirMenuCidadao();
            }
            case "0" -> {
                System.out.println("\nEncerrando...");
                executando = false;
            }
            default -> System.out.println("\nOpção inválida!");
        }
    }

    private void processarMenuGestor(String opcao) {
        switch (opcao) {
            case "1", "2", "3" -> {
                GestorUI gestorUI = new GestorUI(usuarioLogado);
                gestorUI.exibirMenuGestor();
            }
            case "0" -> {
                System.out.println("\nEncerrando...");
                executando = false;
            }
            default -> System.out.println("\nOpção inválida!");
        }
    }

    private void processarMenuAnonimo(String opcao) {
        switch (opcao) {
            case "1" -> {
                CidadaoUI cidadaoUI = new CidadaoUI(usuarioLogado);
                cidadaoUI.exibirMenuCidadao();
            }
            case "2" -> new AcompanhamentoUI(scanner).executar();
            case "0" -> {
                System.out.println("\nEncerrando...");
                executando = false;
            }
            default -> System.out.println("\nOpção inválida!");
        }
    }
}
