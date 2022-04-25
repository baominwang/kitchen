package com.example.kitchen.service.policy;

import com.example.kitchen.service.DispatchPolicy;
import com.example.kitchen.service.model.Courier;
import com.example.kitchen.service.model.Order;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatchedDispatchPolicy implements DispatchPolicy {
    private Map<Integer, Courier> waitingCouriers = new ConcurrentHashMap<>();

    @Override
    public void enQueue(Courier courier) {
        waitingCouriers.put(courier.getId(), courier);
    }

    @Override
    public Courier dispatch(Order order) {
        int courierId = order.getCourierId();
        return waitingCouriers.remove(courierId);
    }
}
