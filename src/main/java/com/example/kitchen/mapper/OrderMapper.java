package com.example.kitchen.mapper;

import com.example.kitchen.dto.OrderDTO;
import com.example.kitchen.service.model.Order;

public interface OrderMapper {
    static Order toOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setId(orderDTO.getId());
        order.setName(orderDTO.getName());
        order.setPrepTime(orderDTO.getPrepTime());

        return order;
    }
}
