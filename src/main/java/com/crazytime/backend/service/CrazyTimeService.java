package com.crazytime.backend.service;

import com.crazytime.backend.model.CrazyTimeRequest;
import com.crazytime.backend.model.CrazyTimeResponse;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class CrazyTimeService {

    private static final int TOTALE_SPICCHI_RUOTA = 64;
    private static final int SPICCHI_DOUBLE = 11; // Indici 0..11 (12 spicchi) raddoppiano il moltiplicatore

    private final Random random = new Random();

    private record RisultatoFlapper(int moltiplicatoreBase, int fattoreRaddoppio) {
        int totale() {
            return moltiplicatoreBase * fattoreRaddoppio;
        }
    }

    public CrazyTimeResponse gioca(CrazyTimeRequest request) {
        int puntata = validaPuntata(request.puntata());
        String sceltaUtente = validaESanitizzaScelta(request.coloreScelto());

        int moltiplicatoreVerde = simulaFlapper().totale();
        int moltiplicatoreBlu = simulaFlapper().totale();
        int moltiplicatoreGiallo = simulaFlapper().totale();

        int moltiplicatoreVinto = estraiMoltiplicatoreScelto(
                sceltaUtente,
                moltiplicatoreVerde,
                moltiplicatoreBlu,
                moltiplicatoreGiallo
        );

        int vincita = puntata * moltiplicatoreVinto;

        return new CrazyTimeResponse(
                puntata,
                sceltaUtente,
                moltiplicatoreVerde,
                moltiplicatoreBlu,
                moltiplicatoreGiallo,
                moltiplicatoreVinto,
                vincita
        );
    }

    private RisultatoFlapper simulaFlapper() {
        int moltiplicatoreBase = 0;
        int fattoreRaddoppio = 1;
        boolean inGiro = true;

        while (inGiro) {
            int estrazione = random.nextInt(TOTALE_SPICCHI_RUOTA);

            if (estrazione <= SPICCHI_DOUBLE) {
                fattoreRaddoppio *= 2;
            } else {
                moltiplicatoreBase = assegnaValoreMoltiplicatore(estrazione);
                inGiro = false;
            }
        }

        return new RisultatoFlapper(moltiplicatoreBase, fattoreRaddoppio);
    }

    // Assegna il moltiplicatore base per le estrazioni oltre i settori Double (12..63)
    private int assegnaValoreMoltiplicatore(int valore) {
        if (valore <= 21) return 10;
        if (valore <= 31) return 20;
        if (valore <= 49) return 25;
        if (valore <= 55) return 35;
        if (valore <= 61) return 50;
        return 100;
    }

    private int validaPuntata(int puntata) {
        if (puntata < 0) {
            throw new IllegalArgumentException("La puntata non può essere negativa.");
        }
        return puntata;
    }

    private String validaESanitizzaScelta(String coloreScelto) {
        if (coloreScelto == null || coloreScelto.isBlank()) {
            throw new IllegalArgumentException("Il colore scelto non può essere vuoto o nullo.");
        }
        return coloreScelto.trim().toUpperCase();
    }

    private int estraiMoltiplicatoreScelto(String scelta, int verde, int blu, int giallo) {
        return switch (scelta) {
            case "VERDE" -> verde;
            case "BLU" -> blu;
            case "GIALLO" -> giallo;
            default -> throw new IllegalArgumentException("Colore non valido (usa VERDE, BLU o GIALLO): " + scelta);
        };
    }
}