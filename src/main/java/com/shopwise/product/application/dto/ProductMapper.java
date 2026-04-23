package com.shopwise.product.application.dto;


import com.shopwise.product.domain.Product;
import com.shopwise.shared.dto.AuditUserInfo;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product,
                                      AuditUserInfo createdBy,
                                      AuditUserInfo updatedBy) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory().name())
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .createdBy(createdBy)
                .updatedBy(updatedBy)
                .build();
    }
}