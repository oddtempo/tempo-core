package com.tempo.core.product.application.service;

import com.tempo.core.product.application.model.request.CreateProductRequest;
import com.tempo.core.product.application.model.request.UpdateProductRequest;
import com.tempo.core.product.application.model.response.ProductResponse;
import com.tempo.core.product.domain.model.Product;
import com.tempo.core.product.domain.repository.ProductRepository;
import com.tempo.core.shared.domain.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductWriteService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductResponse createProduct(UUID tenantId, CreateProductRequest request) {

        Product product = Product.create(
                tenantId,
                request.getTitle(),
                request.getDescription()
        );

        if (request.getOptions() != null) {
            request.getOptions().forEach(opt -> product.addOption(opt.getName(), opt.getValues()));
        }

        product.generateVariants(
                Optional.ofNullable(request.getDefaultPrice()).orElse(BigDecimal.ZERO)
        );

        Product saved = productRepository.save(product);
        return productMapper.toProductResponse(saved);
    }

    public ProductResponse updateProduct(UUID id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("product", id));

        product.updateDetails(request.getTitle(), request.getDescription());

        Product saved = productRepository.save(product);
        return productMapper.toProductResponse(saved);
    }

    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("product", id);
        }
        productRepository.deleteById(id);
    }
}
