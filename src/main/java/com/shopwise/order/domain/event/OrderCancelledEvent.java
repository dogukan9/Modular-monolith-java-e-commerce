package com.shopwise.order.domain.event;

import java.util.List;

public record OrderCancelledEvent(
        Long orderId,
        List<OrderCreatedEvent.OrderItemStock> items
) {}