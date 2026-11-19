package com.crazytime.backend.service;

import com.crazytime.backend.model.PuntateRequest;
import com.crazytime.backend.model.RuotaPrincipaleResponse;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RuotaPrincipaleService {

    // Sequenza di 54 spicchi della ruota principale
    private static final String[] RUOTA_PRINCIPALE = {
            "1", "2", "5", "1", "2", "10", "1", "2", "1", "5", "1", "2", "COIN_CLASH",
            "1", "2", "5", "1", "2", "10", "1", "2", "1", "TARGET_HUNT", "1", "2", "5",
            "1", "2", "PACHIN_SPIN", "1", "2", "1", "5", "1", "2", "10", "1", "2", "1",
            "5", "1", "2", "COIN_CLASH", "1", "2", "5", "1", "2", "TARGET_HUNT", "1",
            "2", "PACHIN_SPIN", "1", "5", "CRAZY_SPIN"
    };

    private final Random random = new Random();

    private final CoinFlipService coinFlipService;
    private final CrazyTimeService crazyTimeService;
    private final CashHuntService cashHuntService;
    private final PachinkoService pachinkoService;

    public RuotaPrincipaleService(
            CoinFlipService coinFlipService,
            CrazyTimeService crazyTimeService,
            CashHuntService cashHuntService,
            PachinkoService pachinkoService
    ) {
        this.coinFlipService = coinFlipService;
        this.crazyTimeService = crazyTimeService;
        this.cashHuntService = cashHuntService;
        this.pachinkoService = pachinkoService;
    }

    private record PuntateProcessate(
            int p1,
            int p2,
            int p5,
            int p10,
            int pCF,
            int pPC,
            int pCH,
            int pCT,
            int spesaEffettiva
    ) {
    }

    public RuotaPrincipaleResponse elaboraGiro(PuntateRequest request) {
        validaRequest(request);

        int saldoIniziale = request.saldoCorrente();
        PuntateProcessate puntate = calcolaPuntateEffettive(request, saldoIniziale);
        String messaggio = verificaAvvisoSaldo(request, saldoIniziale);

        int indiceUscito = random.nextInt(RUOTA_PRINCIPALE.length);
        String settoreUscito = RUOTA_PRINCIPALE[indiceUscito];

        int vincita = calcolaVincita(settoreUscito, puntate);
        int nuovoSaldo = saldoIniziale - puntate.spesaEffettiva() + vincita;

        return new RuotaPrincipaleResponse(
                settoreUscito,
                indiceUscito,
                vincita,
                nuovoSaldo,
                messaggio
        );
    }

    private void validaRequest(PuntateRequest request) {
        if (request.saldoCorrente() < 0) {
            throw new IllegalArgumentException("Il saldo corrente non può essere negativo.");
        }
        if (request.puntataUno() < 0 || request.puntataDue() < 0 ||
                request.puntataCinque() < 0 || request.puntataDieci() < 0 ||
                request.puntataCF() < 0 || request.puntataPC() < 0 ||
                request.puntataCH() < 0 || request.puntataCT() < 0) {
            throw new IllegalArgumentException("Le puntate non possono essere negative.");
        }
    }

    private PuntateProcessate calcolaPuntateEffettive(PuntateRequest request, int saldoDisponibile) {
        int saldo = saldoDisponibile;

        int p1 = Math.min(request.puntataUno(), saldo);
        saldo -= p1;
        int p2 = Math.min(request.puntataDue(), saldo);
        saldo -= p2;
        int p5 = Math.min(request.puntataCinque(), saldo);
        saldo -= p5;
        int p10 = Math.min(request.puntataDieci(), saldo);
        saldo -= p10;
        int pCF = Math.min(request.puntataCF(), saldo);
        saldo -= pCF;
        int pPC = Math.min(request.puntataPC(), saldo);
        saldo -= pPC;
        int pCH = Math.min(request.puntataCH(), saldo);
        saldo -= pCH;
        int pCT = Math.min(request.puntataCT(), saldo);
        saldo -= pCT;

        int spesaEffettiva = saldoDisponibile - saldo;
        return new PuntateProcessate(p1, p2, p5, p10, pCF, pPC, pCH, pCT, spesaEffettiva);
    }

    private String verificaAvvisoSaldo(PuntateRequest request, int saldoDisponibile) {
        int spesaTotaleRichiesta = sommaPuntateRichieste(request);
        if (saldoDisponibile < spesaTotaleRichiesta) {
            return "Saldo esaurito: alcune puntate sono state ridotte o non coperte!";
        }
        return null;
    }

    private int sommaPuntateRichieste(PuntateRequest request) {
        return request.puntataUno() + request.puntataDue() +
                request.puntataCinque() + request.puntataDieci() +
                request.puntataCF() + request.puntataPC() +
                request.puntataCH() + request.puntataCT();
    }

    // Restituisce il payout lordo (puntata + vincita netta: es. "1" -> puntata * (1 + 1))
    private int calcolaVincita(String settore, PuntateProcessate p) {
        return switch (settore) {
            case "1" -> p.p1() * 2;
            case "2" -> p.p2() * 3;
            case "5" -> p.p5() * 6;
            case "10" -> p.p10() * 11;
            // I mini-giochi bonus vengono risolti tramite i rispettivi endpoint dedicati
            case "COIN_CLASH", "PACHIN_SPIN", "TARGET_HUNT", "CRAZY_SPIN" -> 0;
            default -> 0;
        };
    }
}