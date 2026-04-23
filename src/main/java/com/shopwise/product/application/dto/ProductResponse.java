package com.shopwise.product.application.dto;

import com.shopwise.shared.dto.AuditUserInfo;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String category;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private AuditUserInfo createdBy;
    private AuditUserInfo updatedBy;
}