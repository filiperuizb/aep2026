package org.boligon;

import org.boligon.configbanco.InicioBanco;
import org.boligon.entity.Usuario;
import org.boligon.ui.LoginUI;
import org.boligon.ui.MenuPrincipalUI;

public class Main {

    public static void main(String[] args) {
        new InicioBanco().inicializarBanco();

        LoginUI loginUI = new LoginUI();
        Usuario usuarioLogado = loginUI.executar();

        MenuPrincipalUI menuUI = new MenuPrincipalUI(usuarioLogado);
        menuUI.executar();
    }
}
