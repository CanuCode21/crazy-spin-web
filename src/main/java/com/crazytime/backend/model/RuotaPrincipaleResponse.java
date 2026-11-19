package com.crazytime.backend.model;

public record RuotaPrincipaleResponse(
        String spicchioUscito,
        int indiceUscito,
        int vincita,
        int nuovoSaldo,
        String messaggio
) {}