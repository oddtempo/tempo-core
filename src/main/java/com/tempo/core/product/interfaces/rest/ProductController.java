package com.tempo.core.product.interfaces.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductController {
    @PreAuthorize("hasAuthority('product:read')")
    @GetMapping
    public String hello() {
        return "Hello, World!";
    }
}
