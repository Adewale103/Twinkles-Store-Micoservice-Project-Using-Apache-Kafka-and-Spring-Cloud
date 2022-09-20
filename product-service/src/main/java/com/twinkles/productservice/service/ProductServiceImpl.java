package com.twinkles.productservice.service;

import com.twinkles.productservice.dto.ProductRequest;
import com.twinkles.productservice.dto.ProductResponse;
import com.twinkles.productservice.exceptions.ProductException;
import com.twinkles.productservice.model.Product;
import com.twinkles.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        validateProduct(productRequest);

        Product product = Product.builder()
                .description(productRequest.getDescription())
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .build();
        productRepository.save(product);
        return ProductResponse.productResponseBuild(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream().map(ProductResponse::productResponseBuild).collect(Collectors.toList());
    }

    private void validateProduct(ProductRequest productRequest){
        Optional<Product> product = productRepository.findProductByName(productRequest.getName());
        if(product.isPresent()){
            throw new ProductException("product "+product.get().getName()+" already exist!");
        }

    }
}
