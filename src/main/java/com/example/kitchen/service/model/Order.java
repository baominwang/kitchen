package com.example.kitchen.service.model;

import lombok.Data;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Data
public class Order implements Delayed {
    private String id;

    private String name;

    private int prepTime;

    private int courierId;

    // The unit is second
    private long readyTime;

    // The unit is second
    private long pickupTime;

    private long scheduleTime;

    // the unit is second
    public void setScheduleTime(long delay) {
        this.scheduleTime = System.currentTimeMillis() + delay * 1000;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(scheduleTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (scheduleTime - ((Order) o).scheduleTime);
    }
}
