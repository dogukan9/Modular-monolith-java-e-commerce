package com.shopwise.order.domain;

import com.shopwise.order.domain.event.OrderCancelledEvent;
import com.shopwise.order.domain.event.OrderCreatedEvent;
import com.shopwise.shared.domain.BaseEntity;
import com.shopwise.shared.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String userEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    public static Order create(Long userId, String userEmail) {
        Order order = new Order();
        order.userId = userId;
        order.userEmail = userEmail;
        order.status = OrderStatus.PENDING;
        order.totalAmount = BigDecimal.ZERO;
        return order;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        this.totalAmount = this.totalAmount.add(item.getTotalPrice());
    }

    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new BusinessException("INVALID_STATUS",
                    "Sadece PENDING siparişler onaylanabilir");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public OrderCancelledEvent cancel() {
        if (this.status == OrderStatus.DELIVERED) {
            throw new BusinessException("INVALID_STATUS",
                    "Teslim edilmiş sipariş iptal edilemez");
        }
        if (this.status == OrderStatus.CANCELLED) {
            throw new BusinessException("INVALID_STATUS",
                    "Sipariş zaten iptal edilmiş");
        }
        this.status = OrderStatus.CANCELLED;

        // İptal edilen item'ları döndür ve stok geri arttırır
        return new OrderCancelledEvent(
                this.getId(),
                this.items.stream()
                        .map(item -> new OrderCreatedEvent.OrderItemStock(
                                item.getProductId(),
                                item.getQuantity()))
                        .toList()
        );
    }
}