package com.shopwise.order.domain;

public enum OrderStatus {
    PENDING,    // Sipariş oluşturuldu
    CONFIRMED,  // Onaylandı
    SHIPPED,    // Kargoya verildi
    DELIVERED,  // Teslim edildi
    CANCELLED   // İptal edildi
}
