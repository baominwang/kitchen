package com.example.kitchen.service;

import com.example.kitchen.service.model.Courier;
import com.example.kitchen.service.model.Order;

public interface CourierService {
    /**
     * Dispatch the order to an idle courier
     *
     * @param order the received order
     * @return the courier id, or -1 if none
     */
    int dispatchOrder(Order order);

    /**
     * When the courier finishes the delivery, he/she comes back into the idle queues
     *
     * @param courier the courier finishes the delivery
     */
    void rest(Courier courier);
}
