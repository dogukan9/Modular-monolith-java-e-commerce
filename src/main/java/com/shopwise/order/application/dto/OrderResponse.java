package com.shopwise.order.application.dto;

import com.shopwise.order.domain.OrderStatus;
import com.shopwise.shared.dto.AuditUserInfo;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private String userEmail;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<OrderItemResponse> items;
    private AuditUserInfo createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}