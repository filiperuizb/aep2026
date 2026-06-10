package org.boligon.enums;

public enum Prioridade {

    BAIXA(7, "Impacto pontual, sem risco imediato amplo"),
    MEDIA(3, "Afeta rotina de várias pessoas ou serviço público"),
    ALTA(1, "Risco à segurança, saúde ou continuidade de serviço essencial");

    private final int diasSla;
    private final String impactoSocial;

    Prioridade(int diasSla, String impactoSocial) {
        this.diasSla = diasSla;
        this.impactoSocial = impactoSocial;
    }

    public int getDiasSla() {
        return diasSla;
    }

    public String getImpactoSocial() {
        return impactoSocial;
    }
}