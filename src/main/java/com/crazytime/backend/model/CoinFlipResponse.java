package com.crazytime.backend.model;

public record CoinFlipResponse(
        int puntata,
        int moltiplicatoreRosso,
        int moltiplicatoreBlu,
        String coloreUscito,
        int moltiplicatore,
        int vincita
) {
}