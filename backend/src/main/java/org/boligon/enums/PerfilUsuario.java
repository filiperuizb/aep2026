package org.boligon.enums;

public enum PerfilUsuario {
    CIDADAO("CIDADAO"),
    GESTOR("GESTOR"),
    ANONIMO("ANONIMO");

    private final String valor;

    PerfilUsuario(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
