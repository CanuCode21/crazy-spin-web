package com.crazytime.backend.service;

import com.crazytime.backend.model.CoinFlipRequest;
import com.crazytime.backend.model.CoinFlipResponse;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class CoinFlipService {

    private static final int RANGE_PERCENTUALE = 100;

    private final Random random = new Random();

    public CoinFlipResponse gioca(CoinFlipRequest request) {
        int puntata = validaPuntata(request.puntata());

        int moltiplicatoreRosso;
        int moltiplicatoreBlu;

        // Evita che i due lati della moneta abbiano lo stesso moltiplicatore
        do {
            moltiplicatoreRosso = generaMoltiplicatore();
            moltiplicatoreBlu = generaMoltiplicatore();
        } while (moltiplicatoreRosso == moltiplicatoreBlu);

        boolean estrattoRosso = random.nextBoolean();
        String latoVincente = estrattoRosso ? "ROSSO" : "BLU";
        int moltiplicatoreUscito = estrattoRosso ? moltiplicatoreRosso : moltiplicatoreBlu;

        int vincita = puntata * moltiplicatoreUscito;

        return new CoinFlipResponse(
                puntata,
                moltiplicatoreRosso,
                moltiplicatoreBlu,
                latoVincente,
                moltiplicatoreUscito,
                vincita
        );
    }

    private int validaPuntata(int puntata) {
        if (puntata < 0) {
            throw new IllegalArgumentException("La puntata non può essere negativa.");
        }
        return puntata;
    }

    private int generaMoltiplicatore() {
        int valore = random.nextInt(RANGE_PERCENTUALE) + 1;

        if (valore <= 17) return 2;
        if (valore <= 24) return 4;
        if (valore <= 39) return 5;
        if (valore <= 52) return 6;
        if (valore <= 62) return 8;
        if (valore <= 72) return 10;
        if (valore <= 81) return 15;
        if (valore <= 89) return 20;
        if (valore <= 96) return 25;
        return 50; // Valori 97..100
    }
}