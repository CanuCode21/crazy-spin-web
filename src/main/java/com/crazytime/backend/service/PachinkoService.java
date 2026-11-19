package com.crazytime.backend.service;

import com.crazytime.backend.model.PachinkoRequest;
import com.crazytime.backend.model.PachinkoResponse;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PachinkoService {

    private static final int NUMERO_ESITI = 10;

    private final Random random = new Random();

    public PachinkoResponse gioca(PachinkoRequest request) {
        int puntata = validaPuntata(request.puntata());
        int moltiplicatoreTrovato = estraiMoltiplicatore();
        int vincita = moltiplicatoreTrovato * puntata;

        return new PachinkoResponse(
                puntata,
                moltiplicatoreTrovato,
                vincita
        );
    }

    private int validaPuntata(int puntata) {
        if (puntata < 0) {
            throw new IllegalArgumentException("La puntata non può essere negativa.");
        }
        return puntata;
    }

    // Distribuzione uniforme: ciascuno dei 10 esiti ha il 10% di probabilità
    private int estraiMoltiplicatore() {
        int indice = random.nextInt(NUMERO_ESITI);
        return switch (indice) {
            case 0 -> 5;
            case 1 -> 7;
            case 2 -> 10;
            case 3 -> 12;
            case 4 -> 15;
            case 5 -> 20;
            case 6 -> 25;
            case 7 -> 50;
            case 8 -> 75;
            case 9 -> 100;
            default -> throw new IllegalStateException("Indice inatteso: " + indice);
        };
    }
}