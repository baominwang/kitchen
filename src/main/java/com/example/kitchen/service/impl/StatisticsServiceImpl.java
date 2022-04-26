package com.example.kitchen.service.impl;

import com.example.kitchen.service.StatisticsService;
import com.example.kitchen.service.model.Courier;
import com.example.kitchen.service.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReentrantLock;

/*
 * To simplify the implementation of the statistics service, we did not consider the overflow of these fields.
 */

@Service
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {
    // The unit is second
    private long totalFoodWaitTime = 0;

    private long totalFoodNumbers = 0;

    private final transient ReentrantLock foodWaitLock = new ReentrantLock();

    // The unit is second
    private long totalCourierWaitTime = 0;

    private long totalCourierNumbers = 0;

    private final transient ReentrantLock courierWaitLock = new ReentrantLock();

    @Override
    public void cumulateFoodWaitTime(Order order) {
        final ReentrantLock lock = this.foodWaitLock;
        lock.lock();
        try {
            totalFoodWaitTime += order.getPickupTime() - order.getReadyTime();
            totalFoodNumbers++;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void cumulateCourierWaitTime(Courier courier) {
        final ReentrantLock lock = this.courierWaitLock;
        lock.lock();
        try {
            totalCourierWaitTime += courier.getPickupTime() - courier.getArrivalTime();
            totalCourierNumbers++;
        } finally {
            lock.unlock();
        }
    }

    /* This function is used to print the statistics. We don't need to maintain the consistence between
       averageFoodWaitTime and averageCourierWaitTime. */
    @Override
    public void printStatistics() {
        int averageFoodWaitTime;
        foodWaitLock.lock();
        try {
            averageFoodWaitTime = (int)(totalFoodWaitTime/totalFoodNumbers);
        } finally {
            foodWaitLock.unlock();
        }

        int averageCourierWaitTime;
        courierWaitLock.lock();
        try {
            averageCourierWaitTime = (int)(totalCourierWaitTime/totalCourierNumbers);
        } finally {
            courierWaitLock.unlock();
        }
        String newLine = System.getProperty("line.separator");

        // build the statistics result to avoid the inter-leave issue
        StringBuilder sb = new StringBuilder();
        sb.append(newLine);
        sb.append("********************* Total Statistics Start *********************");
        sb.append("averageFoodWaitTime = " + averageFoodWaitTime + "(seconds)");
        sb.append("averageCourierWaitTime = " + averageCourierWaitTime + "(seconds)");
        sb.append("*********************  Total Statistics End  *********************");
        log.info(sb.toString());
    }
}
