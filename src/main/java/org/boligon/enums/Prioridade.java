package org.boligon.enums;

public enum Prioridade {

    BAIXA(7),
    MEDIA(3),
    ALTA(1);

    private final int diasSla;

    Prioridade(int diasSla) {
        this.diasSla = diasSla;
    }

    public int getDiasSla() {
        return diasSla;
    }
}