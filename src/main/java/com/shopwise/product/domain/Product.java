package com.shopwise.product.domain;

import com.shopwise.shared.domain.BaseEntity;
import com.shopwise.shared.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @Column(nullable = false)
    private boolean active = true;

    public static Product create(String name, String description,
                                 BigDecimal price, Integer stock, ProductCategory category) {

         if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("INVALID_PRICE",
                    "Fiyat negatif olamaz");
        }
         if (stock < 0) {
            throw new BusinessException("INVALID_STOCK",
                    "Stok negatif olamaz");
        }

        Product product = new Product();
        product.name = name;
        product.description = description;
        product.price = price;
        product.stock = stock;
        product.category = category;
        return product;
    }

    public void update(String name, String description,
                       BigDecimal price, ProductCategory category) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("INVALID_PRICE", "Fiyat negatif olamaz");
        }
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new BusinessException("INVALID_QUANTITY",
                    "Eklenecek miktar pozitif olmalı");
        }
        this.stock += quantity;
    }

    public void deductStock(int quantity) {
        if (quantity <= 0) {
            throw new BusinessException("INVALID_QUANTITY",
                    "Düşülecek miktar pozitif olmalı");
        }
        if (this.stock < quantity) {
            throw new BusinessException("INSUFFICIENT_STOCK",
                    "Yetersiz stok. Mevcut: " + this.stock);
        }
        this.stock -= quantity;
    }

    public void deactivate() {
        if (!this.active) {
            throw new BusinessException("PRODUCT_ALREADY_INACTIVE",
                    "Ürün zaten pasif");
        }
        this.active = false;
    }
}