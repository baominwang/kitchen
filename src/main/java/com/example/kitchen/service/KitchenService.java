package com.example.kitchen.service;

import com.example.kitchen.service.model.Courier;
import com.example.kitchen.service.model.Order;

public interface KitchenService {
    /**
     * prepare the order
     *
     * @param order the incoming order
     */
    void prepareOrder(Order order);

    /**
     * When the courier arrives the kitchen, Kitchen Service enqueues the courier.
     *
     * @param courier the incoming order
     */
    void arriveKitchen(Courier courier);
}
