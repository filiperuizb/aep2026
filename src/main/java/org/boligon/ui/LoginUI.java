package org.boligon.ui;

import org.boligon.service.AuthService;
import org.boligon.entity.Usuario;

import java.util.Scanner;

public class LoginUI {

    private final Scanner scanner = new Scanner(System.in);
    private final AuthService authService = new AuthService();

    public Usuario executar() {
        while (true) {
            exibirMenu();
            String opcao = scanner.nextLine().trim();

            if (opcao.equals("1")) {
                Usuario usuario = tentarLogin();
                if (usuario != null) {
                    return usuario;
                }
            } else if (opcao.equals("2")) {
                tentarRegistro();
            } else if (opcao.equals("3")) {
                return entrarAnonimo();
            } else if (opcao.equals("0")) {
                System.out.println("\nEncerrando aplicação.");
                System.exit(0);
            } else {
                System.out.println("\nOpção inválida!");
                parar();
            }
        }
    }

    private void exibirMenu() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║      OBSERVA-AÇÃO - SISTEMA         ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.println("\n[1] Login");
        System.out.println("[2] Registrar");
        System.out.println("[3] Entrar Anônimo");
        System.out.println("[0] Sair");
        System.out.print("\nOpção: ");
    }

    private Usuario tentarLogin() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        try {
            Usuario usuario = authService.login(email, senha);
            System.out.println("\n✓ Login realizado com sucesso!");
            return usuario;
        } catch (RuntimeException e) {
            System.out.println("\n✗ " + e.getMessage());
            parar();
            return null;
        }
    }

    private void tentarRegistro() {
        System.out.println("\n--- REGISTRAR ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        try {
            authService.registrar(nome, email, senha);
            System.out.println("\n✓ Registro realizado com sucesso! Faça login agora.");
            parar();
        } catch (RuntimeException e) {
            System.out.println("\n✗ " + e.getMessage());
            parar();
        }
    }

    private Usuario entrarAnonimo() {
        System.out.println("\n✓ Bem-vindo usuário anônimo!");
        parar();
        return criarUsuarioAnonimo();
    }

    private Usuario criarUsuarioAnonimo() {
        Usuario usuario = new Usuario();
        usuario.setNome("Anônimo");
        usuario.setPerfil(org.boligon.enums.PerfilUsuario.ANONIMO);
        return usuario;
    }

    private void parar() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }
}
