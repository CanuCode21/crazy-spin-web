package com.crazytime.backend.model;

public record CashHuntRequest(
        int bersaglioScelto, // Indice da 0 a 107 sulla griglia 12x9
        int puntata
) {
}