package com.example.kitchen.controller;

import com.example.kitchen.service.StatisticsService;
import com.example.kitchen.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = Constants.API_ENDPOINT_STATS)
@Slf4j
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;

    @PostMapping("action/print")
    public void receiveOrder() {
        statisticsService.printStatistics();
    }
}
