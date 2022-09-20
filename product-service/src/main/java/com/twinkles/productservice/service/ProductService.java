package com.twinkles.productservice.service;

import com.twinkles.productservice.dto.ProductRequest;
import com.twinkles.productservice.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest productRequest);
    List<ProductResponse> getAllProducts();
}
