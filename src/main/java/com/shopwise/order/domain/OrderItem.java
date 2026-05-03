package com.shopwise.order.domain;

import com.shopwise.shared.domain.BaseEntity;
import com.shopwise.shared.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Long productId;


    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private BigDecimal unitPrice;


    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    public static OrderItem create(Order order, Long productId,
                                   String productName, BigDecimal unitPrice, Integer quantity) {

        if (quantity <= 0) {
            throw new BusinessException("INVALID_QUANTITY",
                    "Miktar pozitif olmalı");
        }

        OrderItem item = new OrderItem();
        item.order = order;
        item.productId = productId;
        item.productName = productName;
        item.unitPrice = unitPrice;
        item.quantity = quantity;
        item.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return item;
    }
}