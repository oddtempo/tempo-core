package com.tempo.core.product.application.service;

import com.tempo.core.product.application.model.response.ProductResponse;
import com.tempo.core.product.domain.model.Product;
import com.tempo.core.product.domain.repository.ProductRepository;
import com.tempo.core.shared.domain.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductReadService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ProductResponse> getAllProducts() {
        return productMapper.toProductResponseList(productRepository.findAll());
    }

    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findByIdWithOptions(id)
                .orElseThrow(() -> new EntityNotFoundException("Product", id));
        return productMapper.toProductResponse(product);
    }

    public List<ProductResponse> searchProducts(String title) {
        return productMapper.toProductResponseList(productRepository.findByTitleContaining(title));
    }
}
