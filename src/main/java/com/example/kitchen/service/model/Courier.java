package com.example.kitchen.service.model;

import com.example.kitchen.service.CourierService;
import com.example.kitchen.service.OrderService;
import com.example.kitchen.service.StatisticsService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Data
@Slf4j
public class Courier implements Delayed {
    @Autowired
    private CourierService courierService;

    @Autowired
    private StatisticsService statisticsService;

    private OrderService orderService;

    private int id;

    // The unit is second
    private long arrivalTime;

    // The unit is second
    private long pickupTime;

    private long scheduleTime;

    // the unit is second
    public void setScheduleTime(long delay) {
        this.scheduleTime = System.currentTimeMillis() + delay * 1000;
    }

    public void deliveryOrder(Order order) {
        log.info("Courier: (id - {}) starts to delivery the order(id - {}).", id, order.getId());

        // deliver the order
        orderService.deliveryOrder(order);

        // The courier comes back for a rest after the delivery
        courierService.rest(this);

        // cumulate the wait time
        statisticsService.cumulateFoodWaitTime(order);
        statisticsService.cumulateCourierWaitTime(this);

        // print the statistics
        statisticsService.printStatistics();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(scheduleTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (scheduleTime - ((Courier) o).scheduleTime);
    }
}
