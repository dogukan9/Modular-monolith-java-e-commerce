package com.shopwise.order.application;


import com.shopwise.order.application.dto.CreateOrderRequest;
import com.shopwise.order.application.dto.OrderItemRequest;
import com.shopwise.order.application.dto.OrderResponse;
import com.shopwise.order.domain.Order;
import com.shopwise.order.domain.OrderItem;
import com.shopwise.order.domain.event.OrderCancelledEvent;
import com.shopwise.order.domain.event.OrderCreatedEvent;
import com.shopwise.order.infrastructure.OrderRepository;
import com.shopwise.shared.api.SecurityUtils;
import com.shopwise.shared.dto.AuditUserInfo;
import com.shopwise.shared.dto.ProductInfo;
import com.shopwise.shared.exception.BusinessException;
import com.shopwise.shared.port.ProductLookupPort;
import com.shopwise.shared.port.UserLookupPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductLookupPort productLookupPort;
    private final UserLookupPort userLookupPort;
    private final ApplicationEventPublisher eventPublisher;

    public OrderResponse createOrder(CreateOrderRequest request) {

        // 1. Login olan kullanıcıyı al
        Long userId = SecurityUtils.getCurrentUserId();
        String userEmail = SecurityUtils.getCurrentUserEmail();

        // 2. Tüm productId'leri topla
        Set<Long> productIds = request.items().stream()
                .map(OrderItemRequest::productId)
                .collect(Collectors.toSet());

        // 3. Tek sorguda tüm ürünleri getir
        Map<Long, ProductInfo> productInfoMap =
                productLookupPort.findProductInfoByIds(productIds);

        // 4. Order oluştur
        Order order = Order.create(userId, userEmail);

        // 5. Her item için stok kontrolü yap ve ekle
        List<OrderCreatedEvent.OrderItemStock> stockItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.items()) {
            ProductInfo productInfo = productInfoMap.get(itemRequest.productId());

            // Ürün bulunamadı mı?
            if (productInfo == null) {
                throw new BusinessException("PRODUCT_NOT_FOUND",
                        "Ürün bulunamadı: " + itemRequest.productId());
            }

            // Stok yeterli mi?
            if (productInfo.stock() < itemRequest.quantity()) {
                throw new BusinessException("INSUFFICIENT_STOCK",
                        productInfo.name() + " için yeterli stok yok. " +
                                "Mevcut: " + productInfo.stock());
            }

            // OrderItem oluştur — snapshot olarak kaydet
            OrderItem item = OrderItem.create(
                    order,
                    productInfo.id(),
                    productInfo.name(),
                    productInfo.price(),
                    itemRequest.quantity()
            );

            order.addItem(item);

            // Stok düşme listesine ekle
            stockItems.add(new OrderCreatedEvent.OrderItemStock(
                    productInfo.id(), itemRequest.quantity()));
        }


        Order savedOrder = orderRepository.save(order);

        eventPublisher.publishEvent(
                new OrderCreatedEvent(savedOrder.getId(), stockItems));

        log.info("Order created: {} for user: {}", savedOrder.getId(), userEmail);

        AuditUserInfo createdBy = userLookupPort
                .findAuditInfo(savedOrder.getCreatedBy()).orElse(null);

        return orderMapper.toResponse(savedOrder, createdBy);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = findOrderById(id);

        // Sadece kendi siparişini görebilir
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!order.getUserId().equals(currentUserId)) {
            throw new BusinessException("FORBIDDEN",
                    "Bu siparişe erişim yetkiniz yok");
        }

        AuditUserInfo createdBy = userLookupPort
                .findAuditInfo(order.getCreatedBy()).orElse(null);
        return orderMapper.toResponse(order, createdBy);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders() {
        Long userId = SecurityUtils.getCurrentUserId();
        return orderRepository.findByUserId(userId)
                .stream()
                .map(order -> {
                    AuditUserInfo createdBy = userLookupPort
                            .findAuditInfo(order.getCreatedBy()).orElse(null);
                    return orderMapper.toResponse(order, createdBy);
                })
                .toList();
    }

    public OrderResponse cancelOrder(Long id) {
        Order order = findOrderById(id);

        // Sadece kendi siparişini iptal edebilir
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!order.getUserId().equals(currentUserId)) {
            throw new BusinessException("FORBIDDEN",
                    "Bu siparişe erişim yetkiniz yok");
        }

        OrderCancelledEvent event = order.cancel();
        orderRepository.save(order);

        eventPublisher.publishEvent(event);

        AuditUserInfo createdBy = userLookupPort
                .findAuditInfo(order.getCreatedBy()).orElse(null);
        return orderMapper.toResponse(order, createdBy);
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "ORDER_NOT_FOUND", "Sipariş bulunamadı: " + id));
    }
}