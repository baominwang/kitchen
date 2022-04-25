package com.example.kitchen.service.impl;

import com.example.kitchen.exception.BaseException;
import com.example.kitchen.exception.ErrorCode;
import com.example.kitchen.service.CourierService;
import com.example.kitchen.service.KitchenService;
import com.example.kitchen.service.OrderService;
import com.example.kitchen.service.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Value("${orderService.maxNumberOfOngoingOrders}")
    private int maxNumberOfOngoingOrders;

    @Autowired
    private KitchenService kitchenService;

    @Autowired
    private CourierService courierService;

    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    @Override
    public void receiveOrder(Order order) {
        // control the maximum ongoing orders. In concurrent situation, numberOfOngoingOrders could
        // exceed the threshold. This is not a big issue. For flow control, this design is accepted.
        int numberOfOngoingOrders = orders.size();
        if (numberOfOngoingOrders >= maxNumberOfOngoingOrders) {
            log.warn("There are {} ongoing orders. Can't serve more orders", numberOfOngoingOrders);
            throw new BaseException(ErrorCode.ReachOrderLimit);
        }

        // dispatch the order to the courier
        int courierId = courierService.dispatchOrder(order);
        if (courierId == -1) {
            log.warn("Order Service: There is no idle courier for Order(id - {}). The order is rejected", order.getId());
            throw new BaseException(ErrorCode.NoIdleCourier);
        }
        order.setCourierId(courierId);

        // send the order to the kitchen
        kitchenService.prepareOrder(order);

        // record the incoming order into the orders
        orders.put(order.getId(), order);
    }

    @Override
    public void deliveryOrder(Order order) {
        orders.remove(order.getId());
        log.info("Order Service: Order(id - {}) is delivered.", order.getId());
    }
}
