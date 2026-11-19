package com.crazytime.backend.model;

public record PuntateRequest(
        int puntataUno,
        int puntataDue,
        int puntataCinque,
        int puntataDieci,
        int puntataCF, // Coin Flip
        int puntataPC, // Pachinko
        int puntataCH, // Cash Hunt
        int puntataCT, // Crazy Time
        int saldoCorrente
) {
}