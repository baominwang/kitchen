package com.example.kitchen.service.impl;

import com.example.kitchen.exception.BaseException;
import com.example.kitchen.exception.ErrorCode;
import com.example.kitchen.service.CourierService;
import com.example.kitchen.service.KitchenService;
import com.example.kitchen.service.OrderService;
import com.example.kitchen.service.model.Courier;
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

    private final Map<String, Order> ongoingOrders = new ConcurrentHashMap<>();

    // In real product, the delivered orders are persisted in database. Here we just
    // use Map to store it. We don't want to consider OutOfMemory issue here.
    private final Map<String, Order> deliveredOrders = new ConcurrentHashMap<>();

    @Override
    public void receiveOrder(Order order) {
        log.info("Order Service: Receive an order: (id - {}, name - {}, prepTime - {}).", order.getId(), order.getName(), order.getPrepTime());

        // control the maximum ongoing orders. In concurrent situation, numberOfOngoingOrders could
        // exceed the threshold. This is not a big issue. For flow control, this design is accepted.
        int numberOfOngoingOrders = ongoingOrders.size();
        if (numberOfOngoingOrders >= maxNumberOfOngoingOrders) {
            log.warn("There are {} ongoing orders. Can't serve more orders", numberOfOngoingOrders);
            throw new BaseException(ErrorCode.ReachOrderLimit);
        }

        // dispatch the order to the courier
        Courier courier = courierService.dispatchOrder(order);
        if (courier == null) {
            log.warn("Order Service: There is no idle courier for Order(id - {}). The order is rejected", order.getId());
            throw new BaseException(ErrorCode.NoIdleCourier);
        }
        order.setCourierId(courier.getId());

        // send the order to the kitchen
        kitchenService.prepareOrder(order);

        // record the incoming order into the orders
        ongoingOrders.put(order.getId(), order);
    }

    @Override
    public void deliveryOrder(Order order) {
        ongoingOrders.remove(order.getId());
        deliveredOrders.put(order.getId(), order);
        log.info("Order Service: Order(id - {}) is delivered.", order.getId());
    }

    @Override
    public boolean isOrderDelivered(String orderId) {
        return deliveredOrders.containsKey(orderId);
    }
}
