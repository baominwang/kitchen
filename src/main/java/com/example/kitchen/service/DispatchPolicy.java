package com.example.kitchen.service;

import com.example.kitchen.service.model.Courier;
import com.example.kitchen.service.model.Order;

public interface DispatchPolicy {
    /**
     * Different dispatch policy must use different data structure to queue the incoming courier
     * - For Matched dispatch policy, the Map is used.
     * - For FirstInFirstOut dispatch policy, the List is used.
     *
     * @param courier the incoming courier
     */
    void enQueue(Courier courier);

    /**
     * Find the matching courier for the order
     *
     * @param order the incoming order
     * @return the matching courier, or null if none
     */
    Courier dispatch(Order order);
}
