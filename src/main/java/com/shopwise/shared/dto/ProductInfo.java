package com.shopwise.shared.dto;

import java.math.BigDecimal;

public record ProductInfo(
        Long id,
        String name,
        BigDecimal price,
        Integer stock
) {}