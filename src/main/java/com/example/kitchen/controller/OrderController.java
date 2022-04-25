package com.example.kitchen.controller;

import com.example.kitchen.dto.OrderDTO;
import com.example.kitchen.mapper.OrderMapper;
import com.example.kitchen.service.OrderService;
import com.example.kitchen.service.model.Order;
import com.example.kitchen.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = Constants.API_ENDPOINT_ORDER)
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * The entry point for the kitchen system.
     *
     * @param orderDto the received order
     */

    @PostMapping
    public void receiveOrder(@RequestBody @Valid OrderDTO orderDto) {
        log.info("Kitchen system: Receive an order: (id - {}, name - {}, prepTime - {}).", orderDto.getId(), orderDto.getName(), orderDto.getPrepTime());

        Order order = OrderMapper.toOrder(orderDto);
        orderService.receiveOrder(order);
    }
}
