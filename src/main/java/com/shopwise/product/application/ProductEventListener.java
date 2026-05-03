package com.shopwise.product.application;

import com.shopwise.order.domain.event.OrderCancelledEvent;
import com.shopwise.order.domain.event.OrderCreatedEvent;
import com.shopwise.product.domain.Product;
import com.shopwise.product.infrastructure.ProductRepository;
import com.shopwise.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventListener {

    private final ProductRepository productRepository;

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("OrderCreatedEvent received: {}", event.orderId());

        for (OrderCreatedEvent.OrderItemStock item : event.items()) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new BusinessException(
                            "PRODUCT_NOT_FOUND",
                            "Ürün bulunamadı: " + item.productId()));

            product.deductStock(item.quantity());
            productRepository.save(product);

            log.info("Stock deducted: productId={}, quantity={}",
                    item.productId(), item.quantity());
        }
    }

    @EventListener
    public void onOrderCancelled(OrderCancelledEvent event) {
        log.info("OrderCancelledEvent received: {}", event.orderId());

        for (OrderCreatedEvent.OrderItemStock item : event.items()) {
            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new BusinessException(
                            "PRODUCT_NOT_FOUND",
                            "Ürün bulunamadı: " + item.productId()));

            product.addStock(item.quantity());
            productRepository.save(product);

            log.info("Stock restored: productId={}, quantity={}",
                    item.productId(), item.quantity());
        }
    }
}