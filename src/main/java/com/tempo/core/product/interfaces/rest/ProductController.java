package com.tempo.core.product.interfaces.rest;

import com.tempo.core.product.application.model.request.CreateProductRequest;
import com.tempo.core.product.application.model.request.UpdateProductRequest;
import com.tempo.core.product.application.model.response.ProductResponse;
import com.tempo.core.product.application.service.ProductReadService;
import com.tempo.core.product.application.service.ProductWriteService;
import com.tempo.core.shared.infrastructure.tenant.TenantId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.UUID;

import static com.tempo.core.shared.domain.security.Permissions.*;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductReadService productReadService;
    private final ProductWriteService productWriteService;

    @PostMapping
    @PreAuthorize(PRODUCT_CREATE)
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@TenantId UUID tenantId,
                                         @Valid @RequestBody CreateProductRequest request) {
        return productWriteService.createProduct(tenantId, request);
    }

    @GetMapping
    @PreAuthorize(PRODUCT_READ)
    public List<ProductResponse> listProducts() {
        return productReadService.getAllProducts();
    }

    @GetMapping("/{id}")
    @PreAuthorize(PRODUCT_READ)
    public ProductResponse getProduct(@PathVariable UUID id) {
        return productReadService.getProductById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize(PRODUCT_UPDATE)
    public ProductResponse updateProduct(@PathVariable UUID id,
                                         @Valid @RequestBody UpdateProductRequest request) {
        return productWriteService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(PRODUCT_DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable UUID id) {
        productWriteService.deleteProduct(id);
    }
}
