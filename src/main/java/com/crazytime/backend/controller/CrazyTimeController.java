package com.crazytime.backend.controller;

import com.crazytime.backend.model.CrazyTimeRequest;
import com.crazytime.backend.model.CrazyTimeResponse;
import com.crazytime.backend.service.CrazyTimeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crazy-time")
@CrossOrigin(origins = "*")
public class CrazyTimeController {

    private final CrazyTimeService crazyTimeService;

    public CrazyTimeController(CrazyTimeService crazyTimeService) {
        this.crazyTimeService = crazyTimeService;
    }

    @PostMapping("/gioca")
    public CrazyTimeResponse gioca(@RequestBody CrazyTimeRequest request) {
        return crazyTimeService.gioca(request);
    }
}