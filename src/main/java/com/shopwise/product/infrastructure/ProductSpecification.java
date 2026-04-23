package com.shopwise.product.infrastructure;

import com.shopwise.product.application.dto.ProductFilterRequest;
import com.shopwise.product.domain.Product;
import com.shopwise.product.domain.ProductCategory;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {


    private ProductSpecification() {}

    public static Specification<Product> nameContains(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return null;
            return cb.like(cb.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Product> hasCategory(ProductCategory category) {
        return (root, query, cb) -> {
            if (category == null) return null;
            return cb.equal(root.get("category"), category);
        };
    }

    public static Specification<Product> priceBetween(
            BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min == null) return cb.lessThanOrEqualTo(root.get("price"), max);
            if (max == null) return cb.greaterThanOrEqualTo(root.get("price"), min);
            return cb.between(root.get("price"), min, max);
        };
    }

    public static Specification<Product> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) return null;
            return cb.equal(root.get("active"), active);
        };
    }


    public static Specification<Product> build(ProductFilterRequest filter) {
        return Specification
                .where(nameContains(filter.getName()))
                .and(hasCategory(filter.getCategory()))
                .and(priceBetween(filter.getMinPrice(), filter.getMaxPrice()))
                .and(isActive(filter.getActive()));
    }
}
