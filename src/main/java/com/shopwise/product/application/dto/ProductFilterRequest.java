package com.shopwise.product.application.dto;

import com.shopwise.product.domain.ProductCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ProductFilterRequest {

    private String name;
    private ProductCategory category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean active;

    private int page = 0;
    private int size = 20;
    private String sortBy = "createdAt";
    private String sortDir = "desc";
}