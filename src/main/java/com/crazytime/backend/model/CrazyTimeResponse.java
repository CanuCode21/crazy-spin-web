package com.crazytime.backend.model;

public record CrazyTimeResponse(
        int puntata,
        String coloreScelto,
        int moltiplicatoreVerde,
        int moltiplicatoreBlu,
        int moltiplicatoreGiallo,
        int moltiplicatoreVinto,
        int vincita
) {
}