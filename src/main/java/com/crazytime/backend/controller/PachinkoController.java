package com.crazytime.backend.controller;

import com.crazytime.backend.model.PachinkoRequest;
import com.crazytime.backend.model.PachinkoResponse;
import com.crazytime.backend.service.PachinkoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pachinko")
@CrossOrigin(origins = "*")
public class PachinkoController {

    private final PachinkoService pachinkoService;

    public PachinkoController(PachinkoService pachinkoService) {
        this.pachinkoService = pachinkoService;
    }

    @PostMapping("/gioca")
    public PachinkoResponse gioca(@RequestBody PachinkoRequest request) {
        return pachinkoService.gioca(request);
    }
}