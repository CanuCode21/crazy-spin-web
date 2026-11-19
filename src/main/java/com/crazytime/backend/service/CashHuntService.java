package com.crazytime.backend.service;

import com.crazytime.backend.model.CashHuntRequest;
import com.crazytime.backend.model.CashHuntResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CashHuntService {

    private static final int RIGHE = 12;
    private static final int COLONNE = 9;
    private static final int TOTALE_CELLE = RIGHE * COLONNE; // 108 bersagli

    public CashHuntResponse gioca(CashHuntRequest request) {
        int puntata = validaPuntata(request.puntata());
        int bersaglioScelto = normalizzaBersaglio(request.bersaglioScelto());

        List<Integer> moltiplicatori = generaMoltiplicatori();
        Collections.shuffle(moltiplicatori);

        int[][] matrice = creaMatrice(moltiplicatori);

        // Converte l'indice lineare (0..TOTALE_CELLE - 1) in coordinate riga/colonna
        int rigaScelta = bersaglioScelto / COLONNE;
        int colonnaScelta = bersaglioScelto % COLONNE;
        int moltiplicatoreTrovato = matrice[rigaScelta][colonnaScelta];

        int vincita = moltiplicatoreTrovato * puntata;

        return new CashHuntResponse(
                bersaglioScelto,
                moltiplicatoreTrovato,
                puntata,
                vincita,
                matrice
        );
    }

    private int validaPuntata(int puntata) {
        if (puntata < 0) {
            throw new IllegalArgumentException("La puntata non può essere negativa.");
        }
        return puntata;
    }

    private int normalizzaBersaglio(int bersaglio) {
        if (bersaglio < 0) {
            return 0;
        }
        return Math.min(bersaglio, TOTALE_CELLE - 1);
    }

    private int[][] creaMatrice(List<Integer> moltiplicatori) {
        int[][] matrice = new int[RIGHE][COLONNE];
        int indice = 0;

        for (int riga = 0; riga < RIGHE; riga++) {
            for (int colonna = 0; colonna < COLONNE; colonna++) {
                matrice[riga][colonna] = moltiplicatori.get(indice++);
            }
        }

        return matrice;
    }

    private List<Integer> generaMoltiplicatori() {
        List<Integer> lista = new ArrayList<>(TOTALE_CELLE);
        aggiungiMoltiplicatori(lista, 5, 29);
        aggiungiMoltiplicatori(lista, 7, 25);
        aggiungiMoltiplicatori(lista, 10, 20);
        aggiungiMoltiplicatori(lista, 12, 12);
        aggiungiMoltiplicatori(lista, 15, 8);
        aggiungiMoltiplicatori(lista, 20, 6);
        aggiungiMoltiplicatori(lista, 25, 4);
        aggiungiMoltiplicatori(lista, 50, 2);
        aggiungiMoltiplicatori(lista, 75, 1);
        aggiungiMoltiplicatori(lista, 100, 1);
        return lista;
    }

    private void aggiungiMoltiplicatori(List<Integer> lista, int moltiplicatore, int quantita) {
        lista.addAll(Collections.nCopies(quantita, moltiplicatore));
    }
}