package com.example.kitchen.service.impl;

import com.example.kitchen.service.CourierService;
import com.example.kitchen.service.KitchenService;
import com.example.kitchen.service.OrderService;
import com.example.kitchen.service.model.Courier;
import com.example.kitchen.service.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Provider;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class CourierServiceImpl implements CourierService {
    @Value("${courierService.numberOfCouriers}")
    private int numberOfCouriers;

    @Autowired
    private KitchenService kitchenService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private Provider<Courier> courierProvider;

    // the courierMonitor is responsible for monitoring whether the courier arrives
    // the kitchen. If one courier arrives, the courierMonitor informs the KitchenService
    private final ExecutorService courierMonitor = Executors.newSingleThreadExecutor();

    private final List<Courier> idleCouriers = new LinkedList<>();

    private final transient ReentrantLock idleCouriersLock = new ReentrantLock();

    private final DelayQueue<Courier> onTheWayCouriers = new DelayQueue<>();

    @PostConstruct
    public void init() {
        final ReentrantLock lock = this.idleCouriersLock;
        lock.lock();
        try {
            int idGenerator = 1;
            for (int i = 0; i < numberOfCouriers; i++) {
                Courier courier = courierProvider.get();
                courier.setId(idGenerator);
                courier.setOrderService(orderService);
                idleCouriers.add(idleCouriers.size(), courier);

                idGenerator++;
            }
        } finally {
            lock.unlock();
        }

        courierMonitor.submit(new CourierMonitor(kitchenService, onTheWayCouriers));
    }

    @Override
    public int dispatchOrder(Order order) {
        final ReentrantLock lock = this.idleCouriersLock;
        lock.lock();
        Courier courier;
        try {
            if (idleCouriers.size() == 0) {
                log.warn("Courier Service: There is no idle courier");
                return -1;
            }

            // choose the first courier
            courier = idleCouriers.remove(0);
        } finally {
            lock.unlock();
        }

        // calculate the time on the way
        Random random = new Random();
        long delay = random.nextInt(13) + 3;
        courier.setScheduleTime(delay);
        onTheWayCouriers.add(courier);
        log.info("Courier Service: Courier(id - {}) accepts the Order(id - {}). He will arrive the kitchen {} seconds later.",
                courier.getId(), order.getId(), delay);

        return courier.getId();
    }

    @Override
    public void rest(Courier courier) {
        final ReentrantLock lock = this.idleCouriersLock;
        lock.lock();
        try {
            idleCouriers.add(idleCouriers.size(), courier);
        } finally {
            lock.unlock();
        }

        log.info("Courier Service: Courier(id - {}) is idle now.", courier.getId());
    }

    public static class CourierMonitor implements Runnable {
        private final KitchenService kitchenService;

        private final DelayQueue<Courier> onTheWayCouriers;

        public CourierMonitor(KitchenService kitchenService, DelayQueue<Courier> onTheWayCouriers) {
            this.kitchenService = kitchenService;
            this.onTheWayCouriers = onTheWayCouriers;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Courier courier = onTheWayCouriers.take();
                    kitchenService.arriveKitchen(courier);
                } catch (InterruptedException ex) {
                    // if the courier monitor is interrupted, continue the procedure
                }
            }
        }
    }
}
