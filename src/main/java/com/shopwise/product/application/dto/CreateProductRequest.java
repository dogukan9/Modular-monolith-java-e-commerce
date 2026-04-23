package com.shopwise.product.application.dto;

import com.shopwise.product.domain.ProductCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateProductRequest(

        @NotBlank(message = "Ürün adı boş olamaz")
        String name,

        String description,

        @NotNull(message = "Fiyat boş olamaz")
        @DecimalMin(value = "0.0", message = "Fiyat negatif olamaz")
        BigDecimal price,

        @NotNull(message = "Stok boş olamaz")
        @Min(value = 0, message = "Stok negatif olamaz")
        Integer stock,

        @NotNull(message = "Kategori boş olamaz")
        ProductCategory category
) {}