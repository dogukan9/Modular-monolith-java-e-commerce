package com.shopwise.order.domain.event;


import java.util.List;

public record OrderCreatedEvent(
        Long orderId,
        List<OrderItemStock> items
) {
    public record OrderItemStock(Long productId, Integer quantity) {}
}