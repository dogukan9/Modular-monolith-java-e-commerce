package com.shopwise.product.application;

import com.shopwise.product.application.dto.*;
import com.shopwise.product.domain.Product;
import com.shopwise.product.infrastructure.ProductRepository;
import com.shopwise.product.infrastructure.ProductSpecification;
import com.shopwise.shared.dto.AuditUserInfo;
import com.shopwise.shared.exception.BusinessException;
import com.shopwise.shared.port.UserLookupPort;
import com.shopwise.shared.api.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final UserLookupPort userLookupPort;
    public ProductResponse createProduct(CreateProductRequest request) {

        if (productRepository.existsByName(request.name())) {
            throw new BusinessException("PRODUCT_NAME_EXISTS",
                    "Bu isimde ürün zaten var: " + request.name());
        }

        Product product = Product.create(
                request.name(),
                request.description(),
                request.price(),
                request.stock(),
                request.category()
        );

        Product saved = productRepository.save(product);
        return toResponseWithAudit(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = findProductById(id);
        return toResponseWithAudit(product);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getProducts(ProductFilterRequest filter) {

        Sort.Direction direction = filter.getSortDir().equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(
                filter.getPage(), filter.getSize(),
                Sort.by(direction, filter.getSortBy()));

        Specification<Product> spec = ProductSpecification.build(filter);
        Page<Product> productPage = productRepository.findAll(spec, pageable);

        // her ürün için ayrı sorgu yapmak yerine
        // tüm createdBy id'lerini topla, tek sorguda getir
        Set<Long> userIds = productPage.getContent().stream()
                .flatMap(p -> Stream.of(p.getCreatedBy(), p.getUpdatedBy()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, AuditUserInfo> auditUsers =
                userLookupPort.findAuditInfoByIds(userIds);

        Page<ProductResponse> responsePage = productPage.map(product ->
                productMapper.toResponse(
                        product,
                        auditUsers.get(product.getCreatedBy()),
                        auditUsers.get(product.getUpdatedBy())
                ));

        return PageResponse.of(responsePage);
    }


    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = findProductById(id);
        product.update(request.name(), request.description(),
                request.price(), request.category());
        return toResponseWithAudit(productRepository.save(product));
    }

    public void deactivateProduct(Long id) {
        Product product = findProductById(id);
        product.deactivate();
        productRepository.save(product);
    }

    private ProductResponse toResponseWithAudit(Product product) {
        AuditUserInfo createdBy = userLookupPort
                .findAuditInfo(product.getCreatedBy()).orElse(null);
        AuditUserInfo updatedBy = userLookupPort
                .findAuditInfo(product.getUpdatedBy()).orElse(null);
        return productMapper.toResponse(product, createdBy, updatedBy);
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "PRODUCT_NOT_FOUND", "Ürün bulunamadı: " + id));
    }
}
