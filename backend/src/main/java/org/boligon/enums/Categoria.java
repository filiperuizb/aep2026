package org.boligon.enums;

public enum Categoria {
    ILUMINACAO("Iluminação Pública"),
    BURACO("Buraco"),
    ZELADORIA("Zeladoria/Limpeza"),
    PODA("Poda de Árvore"),
    SAUDE("Saúde"),
    SEGURANCA_ESCOLAR("Segurança escolar");

    private final String valor;

    Categoria(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
