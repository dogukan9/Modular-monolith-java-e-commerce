package com.shopwise.shared.port;

import com.shopwise.shared.dto.ProductInfo;

import java.util.Map;
import java.util.Set;

public interface ProductLookupPort {
    ProductInfo findProductInfo(Long productId);

    Map<Long, ProductInfo> findProductInfoByIds(Set<Long> productIds);
}