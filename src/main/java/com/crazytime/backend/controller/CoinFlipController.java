package com.crazytime.backend.controller;

import com.crazytime.backend.model.CoinFlipRequest;
import com.crazytime.backend.model.CoinFlipResponse;
import com.crazytime.backend.service.CoinFlipService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coin-flip")
@CrossOrigin(origins = "*")
public class CoinFlipController {

    private final CoinFlipService coinFlipService;

    public CoinFlipController(CoinFlipService coinFlipService) {
        this.coinFlipService = coinFlipService;
    }

    @PostMapping("/gioca")
    public CoinFlipResponse gioca(@RequestBody CoinFlipRequest request) {
        return coinFlipService.gioca(request);
    }
}