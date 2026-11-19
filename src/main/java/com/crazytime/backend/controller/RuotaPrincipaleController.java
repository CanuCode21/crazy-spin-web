package com.crazytime.backend.controller;

import com.crazytime.backend.model.PuntateRequest;
import com.crazytime.backend.model.RuotaPrincipaleResponse;
import com.crazytime.backend.service.RuotaPrincipaleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ruota-principale")
@CrossOrigin(origins = "*")
public class RuotaPrincipaleController {

    private final RuotaPrincipaleService ruotaPrincipaleService;

    public RuotaPrincipaleController(RuotaPrincipaleService ruotaPrincipaleService) {
        this.ruotaPrincipaleService = ruotaPrincipaleService;
    }

    @PostMapping("/gira")
    public RuotaPrincipaleResponse giraRuota(@RequestBody PuntateRequest puntateRequest) {
        return ruotaPrincipaleService.elaboraGiro(puntateRequest);
    }
}