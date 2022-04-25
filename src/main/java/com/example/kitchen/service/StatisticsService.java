package com.example.kitchen.service;

import com.example.kitchen.service.model.Courier;
import com.example.kitchen.service.model.Order;

public interface StatisticsService {
    /**
     * cumulate the food wait time
     *
     * @param order the received order
     */
    void cumulateFoodWaitTime(Order order);

    /**
     * cumulate the courier wait time
     *
     * @param courier the courier
     */
    void cumulateCourierWaitTime(Courier courier);

    /**
     * print the statistics for the food/courier wait time
     */
    void printStatistics();
}
