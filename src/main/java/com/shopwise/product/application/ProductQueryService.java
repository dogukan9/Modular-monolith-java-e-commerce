package com.shopwise.product.application;

import com.shopwise.product.domain.Product;
import com.shopwise.product.infrastructure.ProductRepository;
import com.shopwise.shared.dto.ProductInfo;
import com.shopwise.shared.exception.BusinessException;
import com.shopwise.shared.port.ProductLookupPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryService implements ProductLookupPort {

    private final ProductRepository productRepository;

    @Override
    public ProductInfo findProductInfo(Long productId) {
        return productRepository.findById(productId)
                .filter(Product::isActive)
                .map(p -> new ProductInfo(
                        p.getId(),
                        p.getName(),
                        p.getPrice(),
                        p.getStock()))
                .orElseThrow(() -> new BusinessException(
                        "PRODUCT_NOT_FOUND", "Ürün bulunamadı: " + productId));
    }

    @Override
    public Map<Long, ProductInfo> findProductInfoByIds(Set<Long> productIds) {
        return productRepository.findAllById(productIds)
                .stream()
                .filter(Product::isActive)
                .collect(Collectors.toMap(
                        Product::getId,
                        p -> new ProductInfo(
                                p.getId(),
                                p.getName(),
                                p.getPrice(),
                                p.getStock())
                ));
    }
}
