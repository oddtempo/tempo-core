package com.tempo.core.product.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tempo.core.auth.infrastructure.LoginRateLimitFilter;
import com.tempo.core.auth.infrastructure.RedisAuthenticationFilter;
import com.tempo.core.auth.infrastructure.SecurityConfig;
import com.tempo.core.config.WebConfig;
import com.tempo.core.product.application.model.request.CreateProductRequest;
import com.tempo.core.product.application.model.request.UpdateProductRequest;
import com.tempo.core.product.application.model.response.ProductResponse;
import com.tempo.core.product.application.service.ProductReadService;
import com.tempo.core.product.application.service.ProductWriteService;
import com.tempo.core.shared.domain.exception.EntityNotFoundException;
import com.tempo.core.shared.infrastructure.tenant.HibernateTenantFilter;
import com.tempo.core.shared.infrastructure.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ProductController.
 * Tests all 5 REST endpoints with proper HTTP status codes, authorization, and
 * validation.
 */
@WebMvcTest(ProductController.class)
@Import({ WebConfig.class, SecurityConfig.class })
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductWriteService productWriteService;

    @MockitoBean
    private ProductReadService productReadService;

    @MockitoBean
    private RedisAuthenticationFilter redisAuthenticationFilter;

    @MockitoBean
    private LoginRateLimitFilter loginRateLimitFilter;

    @MockitoBean
    private HibernateTenantFilter hibernateTenantFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID tenantId;
    private UUID productId;

    @BeforeEach
    void setUp() throws Exception {
        tenantId = UUID.randomUUID();
        productId = UUID.randomUUID();
        TenantContext.setCurrentTenantId(tenantId);

        // Mock filters to pass through
        doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(redisAuthenticationFilter).doFilter(any(), any(), any());

        doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(loginRateLimitFilter).doFilter(any(), any(), any());

        doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(hibernateTenantFilter).doFilter(any(), any(), any());
    }

    // ============================================
    // AC1: All endpoints return proper HTTP status codes
    // ============================================

    @Nested
    @DisplayName("POST /api/products - Create Product")
    class CreateProduct {

        @Test
        @WithMockUser(authorities = "product:create")
        @DisplayName("should return 201 Created when product is created successfully")
        void createProduct_ReturnsCreated() throws Exception {
            CreateProductRequest request = new CreateProductRequest();
            request.setTitle("Test Product");
            request.setDescription("Test Description");
            request.setDefaultPrice(BigDecimal.valueOf(100));

            ProductResponse response = new ProductResponse(
                    productId, "Test Product", "Test Description",
                    List.of(), List.of());

            when(productWriteService.createProduct(any(UUID.class), any(CreateProductRequest.class)))
                    .thenReturn(response);

            mockMvc.perform(post("/api/products")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(productId.toString()))
                    .andExpect(jsonPath("$.title").value("Test Product"));

            verify(productWriteService).createProduct(eq(tenantId), any(CreateProductRequest.class));
        }

        @Test
        @WithMockUser(authorities = "product:create")
        @DisplayName("should return 400 Bad Request for validation errors (RFC 7807)")
        void createProduct_ValidationError_ReturnsBadRequest() throws Exception {
            CreateProductRequest request = new CreateProductRequest();
            request.setTitle(""); // blank title - validation error
            request.setDescription("Test");

            mockMvc.perform(post("/api/products")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(authorities = "other:permission")
        @DisplayName("should return 403 Forbidden for unauthorized user")
        void createProduct_Unauthorized_ReturnsForbidden() throws Exception {
            CreateProductRequest request = new CreateProductRequest();
            request.setTitle("Test Product");

            mockMvc.perform(post("/api/products")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(productWriteService);
        }
    }

    @Nested
    @DisplayName("GET /api/products - List Products")
    class ListProducts {

        @Test
        @WithMockUser(authorities = "product:read")
        @DisplayName("should return 200 OK with list of products")
        void listProducts_ReturnsOk() throws Exception {
            List<ProductResponse> products = List.of(
                    new ProductResponse(UUID.randomUUID(), "Product 1", "Desc 1", List.of(), List.of()),
                    new ProductResponse(UUID.randomUUID(), "Product 2", "Desc 2", List.of(), List.of()));

            when(productReadService.getAllProducts()).thenReturn(products);

            mockMvc.perform(get("/api/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].title").value("Product 1"))
                    .andExpect(jsonPath("$[1].title").value("Product 2"));
        }

        @Test
        @WithMockUser(authorities = "other:permission")
        @DisplayName("should return 403 Forbidden for unauthorized user")
        void listProducts_Unauthorized_ReturnsForbidden() throws Exception {
            mockMvc.perform(get("/api/products"))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(productReadService);
        }
    }

    @Nested
    @DisplayName("GET /api/products/{id} - Get Product Detail")
    class GetProduct {

        @Test
        @WithMockUser(authorities = "product:read")
        @DisplayName("should return 200 OK with product details")
        void getProduct_ReturnsOk() throws Exception {
            ProductResponse response = new ProductResponse(
                    productId, "Test Product", "Test Desc", List.of(), List.of());

            when(productReadService.getProductById(productId)).thenReturn(response);

            mockMvc.perform(get("/api/products/{id}", productId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(productId.toString()))
                    .andExpect(jsonPath("$.title").value("Test Product"));
        }

        @Test
        @WithMockUser(authorities = "product:read")
        @DisplayName("should return 404 Not Found when product doesn't exist")
        void getProduct_NotFound_Returns404() throws Exception {
            UUID nonExistentId = UUID.randomUUID();
            when(productReadService.getProductById(nonExistentId))
                    .thenThrow(new EntityNotFoundException("Product", nonExistentId));

            mockMvc.perform(get("/api/products/{id}", nonExistentId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/products/{id} - Update Product")
    class UpdateProduct {

        @Test
        @WithMockUser(authorities = "product:update")
        @DisplayName("should return 200 OK when product is updated")
        void updateProduct_ReturnsOk() throws Exception {
            UpdateProductRequest request = new UpdateProductRequest("Updated Title", "Updated Desc");
            ProductResponse response = new ProductResponse(
                    productId, "Updated Title", "Updated Desc", List.of(), List.of());

            when(productWriteService.updateProduct(eq(productId), any(UpdateProductRequest.class)))
                    .thenReturn(response);

            mockMvc.perform(put("/api/products/{id}", productId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Updated Title"));
        }

        @Test
        @WithMockUser(authorities = "product:update")
        @DisplayName("should return 404 Not Found when product doesn't exist")
        void updateProduct_NotFound_Returns404() throws Exception {
            UUID nonExistentId = UUID.randomUUID();
            UpdateProductRequest request = new UpdateProductRequest("Title", "Desc");

            when(productWriteService.updateProduct(eq(nonExistentId), any(UpdateProductRequest.class)))
                    .thenThrow(new EntityNotFoundException("Product", nonExistentId));

            mockMvc.perform(put("/api/products/{id}", nonExistentId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(authorities = "other:permission")
        @DisplayName("should return 403 Forbidden for unauthorized user")
        void updateProduct_Unauthorized_ReturnsForbidden() throws Exception {
            UpdateProductRequest request = new UpdateProductRequest("Title", "Desc");

            mockMvc.perform(put("/api/products/{id}", productId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(productWriteService);
        }
    }

    @Nested
    @DisplayName("DELETE /api/products/{id} - Delete Product")
    class DeleteProduct {

        @Test
        @WithMockUser(authorities = "product:delete")
        @DisplayName("should return 204 No Content when product is deleted")
        void deleteProduct_ReturnsNoContent() throws Exception {
            doNothing().when(productWriteService).deleteProduct(productId);

            mockMvc.perform(delete("/api/products/{id}", productId)
                    .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(productWriteService).deleteProduct(productId);
        }

        @Test
        @WithMockUser(authorities = "product:delete")
        @DisplayName("should return 404 Not Found when product doesn't exist")
        void deleteProduct_NotFound_Returns404() throws Exception {
            UUID nonExistentId = UUID.randomUUID();
            doThrow(new EntityNotFoundException("Product", nonExistentId))
                    .when(productWriteService).deleteProduct(nonExistentId);

            mockMvc.perform(delete("/api/products/{id}", nonExistentId)
                    .with(csrf()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(authorities = "product:create") // wrong permission
        @DisplayName("should return 403 Forbidden with wrong permission")
        void deleteProduct_WrongPermission_ReturnsForbidden() throws Exception {
            mockMvc.perform(delete("/api/products/{id}", productId)
                    .with(csrf()))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(productWriteService);
        }
    }
}
