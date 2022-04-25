package com.example.kitchen.service;

import com.example.kitchen.service.model.Courier;
import com.example.kitchen.service.model.Order;

public interface StatisticsService {
    void cumulateFoodWaitTime(Order order);

    void cumulateCourierWaitTime(Courier courier);

    void printStatistics();
}
