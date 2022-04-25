package com.example.kitchen.service;

import com.example.kitchen.service.model.Order;

public interface OrderService {
    /**
     * The order service receives an order. It triggers two actions:
     * 1. Dispatch the order to a courier
     * 2. Send the order to the Kitchen for preparing
     *
     * @param order the received order
     */
    void receiveOrder(Order order);

    /**
     * When the order is delivered, the order is removed from the Order Service
     *
     * @param order the received order
     */
    void deliveryOrder(Order order);
}
