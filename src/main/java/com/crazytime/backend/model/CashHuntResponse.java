package com.crazytime.backend.model;

public record CashHuntResponse(
        int bersaglioScelto,
        int moltiplicatoreTrovato,
        int puntata,
        int vincita,
        int[][] matrice // Griglia 12x9 rivelata al termine del bonus
) {
}