package com.example.kitchen;

import com.alibaba.fastjson2.JSON;
import com.example.kitchen.service.OrderService;
import com.example.kitchen.service.StatisticsService;
import com.example.kitchen.service.model.Order;
import com.example.kitchen.utils.ResourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
@Slf4j
public class TestKitchenApplication extends AbstractTestNGSpringContextTests {
    @Value("classpath:dispatch_orders.json")
    private Resource dispatchOrdersResource;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StatisticsService statisticsService;

    @Test
    public void testBatchOrders() throws Exception {
        String content = ResourceUtils.asString(dispatchOrdersResource);
        List<Order> orders = JSON.parseArray(content, Order.class);

        long lastOrderReadyTime = 0;
        Set<String> ongoingOrders = new HashSet<>();
        for (Order order : orders) {
            // record the ongoingOrders for later check
            ongoingOrders.add(order.getId());

            // set the last order ready time
            long orderReadyTime = System.currentTimeMillis() / 1000 + order.getPickupTime();
            if (orderReadyTime > lastOrderReadyTime) {
                lastOrderReadyTime = orderReadyTime;
            }

            // inform the order service
            orderService.receiveOrder(order);

            Thread.sleep(500);
        }

        // This is possible last courier arrival time
        long lastCourierArrivalTime = System.currentTimeMillis() / 1000 + 15;

        // calculate the last delivery time. Add 1 more second for time skew
        long lastDeliveryTime = (lastCourierArrivalTime > lastOrderReadyTime) ? lastCourierArrivalTime : lastOrderReadyTime;
        lastDeliveryTime += 1;

        while (lastDeliveryTime > System.currentTimeMillis()/1000) {
            Set<String> newOngoingOrders = new HashSet<>();
            for (String ongoingOrderId: ongoingOrders) {
                if (!orderService.isOrderDelivered(ongoingOrderId)) {
                    newOngoingOrders.add(ongoingOrderId);
                    Thread.sleep(1);
                }
            }

            ongoingOrders = newOngoingOrders;
        }

        // check whether all the orders are delivered within the expected time
        Assert.assertEquals(ongoingOrders.size(), 0);

        statisticsService.printStatistics();
    }
}
