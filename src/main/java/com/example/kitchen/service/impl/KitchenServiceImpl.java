package com.example.kitchen.service.impl;

import com.example.kitchen.service.DispatchPolicy;
import com.example.kitchen.service.KitchenService;
import com.example.kitchen.service.model.Courier;
import com.example.kitchen.service.model.Order;
import com.example.kitchen.service.policy.FistInFirstOutDispatchPolicy;
import com.example.kitchen.service.policy.MatchedDispatchPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class KitchenServiceImpl implements KitchenService {
    @Value("${kitchenService.dispatchPolicy}")
    private String dispatchPolicyStr;

    // the orderMonitor is responsible for monitoring whether the order is prepared
    // If the order is prepared, the orderMonitor dispatches the order
    private final ExecutorService orderMonitor = Executors.newSingleThreadExecutor();

    private DispatchPolicy dispatchPolicy;

    private final DelayQueue<Order> preparingOrders = new DelayQueue<>();

    @PostConstruct
    public void init() {
        if (dispatchPolicyStr.equals("matched")) {
            dispatchPolicy = new MatchedDispatchPolicy();
        } else {
            dispatchPolicy = new FistInFirstOutDispatchPolicy();
        }

        orderMonitor.submit(new OrderMonitor(preparingOrders, dispatchPolicy));
    }

    @Override
    public void prepareOrder(Order order) {
        order.setScheduleTime(order.getPrepTime());
        preparingOrders.put(order);

        log.info("Kitchen Service: Order(id - {}) is preparing.", order.getId());
    }

    @Override
    public void arriveKitchen(Courier courier) {
        log.info("Kitchen Service: Courier(id - {}) arrives the kitchen.", courier.getId());

        // set the arrival time for the courier
        courier.setArrivalTime(System.currentTimeMillis()/1000);

        dispatchPolicy.enQueue(courier);
    }

    public static class OrderMonitor implements Runnable {
        private final DelayQueue<Order> preparingOrders;

        private final DispatchPolicy dispatchPolicy;

        public OrderMonitor(DelayQueue<Order> preparingOrders, DispatchPolicy dispatchPolicy) {
            this.preparingOrders = preparingOrders;
            this.dispatchPolicy = dispatchPolicy;
        }

        @Override
        public void run() {
            // readyOrders is only used the OrderMonitor. It is stack constrained. There is no concurrent issue.
            Set<String> readyOrders = new HashSet<>();

            while (true) {
                try {
                    Order order = preparingOrders.take();
                    if (!readyOrders.contains(order.getId())) {
                        // set the ready time when the order is ready
                        order.setReadyTime(System.currentTimeMillis()/1000);

                        readyOrders.add(order.getId());
                        log.info("Kitchen Service: Order(id - {}) is ready for pick up.", order.getId());
                    }

                    Courier courier = dispatchPolicy.dispatch(order);
                    if (courier == null) {
                        order.setScheduleTime(1);
                        preparingOrders.add(order);
                        continue;
                    }

                    readyOrders.remove(order.getId());

                    // set the pickup time for the order
                    order.setPickupTime(System.currentTimeMillis()/1000);

                    // set the pickup time for the courier
                    courier.setPickupTime(System.currentTimeMillis()/1000);

                    courier.deliveryOrder(order);
                } catch (InterruptedException ex) {
                    // if the courier monitor is interrupted, continue the procedure
                }
            }
        }
    }
}
