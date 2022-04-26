package com.example.kitchen.service;

import com.example.kitchen.exception.BaseException;
import com.example.kitchen.service.impl.OrderServiceImpl;
import com.example.kitchen.service.model.Order;
import org.testng.annotations.Test;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestOrderService {
    @Test(expectedExceptions = BaseException.class)
    public void testFlowControl() {
        Map<String, Order> ongoingOrders = mock(Map.class);
        when(ongoingOrders.size()).thenReturn(100);

        OrderServiceImpl orderService = new OrderServiceImpl();
        orderService.setOngoingOrders(ongoingOrders);
        orderService.setMaxNumberOfOngoingOrders(100);

        Order order = new Order();
        orderService.receiveOrder(order);
    }

    @Test(expectedExceptions = BaseException.class)
    public void testCourierBusy() {
        CourierService courierService = mock(CourierService.class);
        when(courierService.dispatchOrder(any())).thenReturn(null);

        OrderServiceImpl orderService = new OrderServiceImpl();
        orderService.setCourierService(courierService);
        orderService.setMaxNumberOfOngoingOrders(100);

        Order order = new Order();
        orderService.receiveOrder(order);
    }
}
