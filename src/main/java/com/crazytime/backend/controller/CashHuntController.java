package com.crazytime.backend.controller;

import com.crazytime.backend.model.CashHuntRequest;
import com.crazytime.backend.model.CashHuntResponse;
import com.crazytime.backend.service.CashHuntService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cash-hunt")
@CrossOrigin(origins = "*") // Permette la comunicazione con il frontend
public class CashHuntController {

    @Autowired
    private CashHuntService cashHuntService;

    @PostMapping("/gioca")
    public CashHuntResponse gioca(@RequestBody CashHuntRequest request) {
        return cashHuntService.gioca(request);
    }
}