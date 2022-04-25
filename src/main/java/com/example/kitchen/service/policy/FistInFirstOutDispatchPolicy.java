package com.example.kitchen.service.policy;

import com.example.kitchen.service.DispatchPolicy;
import com.example.kitchen.service.model.Courier;
import com.example.kitchen.service.model.Order;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component("firstInFirstOut")
public class FistInFirstOutDispatchPolicy implements DispatchPolicy {
    private final List<Courier> waitingCouriers = new LinkedList<>();

    private final transient ReentrantLock waitingCouriersLock = new ReentrantLock();

    @Override
    public void enQueue(Courier courier) {
        final ReentrantLock lock = this.waitingCouriersLock;
        lock.lock();
        try {
            waitingCouriers.add(waitingCouriers.size(), courier);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Courier dispatch(Order order) {
        final ReentrantLock lock = this.waitingCouriersLock;
        lock.lock();
        try {
            if (waitingCouriers.size() == 0) {
                return null;
            }

            return waitingCouriers.remove(0);
        } finally {
            lock.unlock();
        }
    }
}
