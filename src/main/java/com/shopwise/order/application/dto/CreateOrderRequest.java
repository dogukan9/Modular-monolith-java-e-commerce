package com.shopwise.order.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
        @NotEmpty(message = "Sipariş en az 1 ürün içermeli")
        @Valid
        List<OrderItemRequest> items
) {}