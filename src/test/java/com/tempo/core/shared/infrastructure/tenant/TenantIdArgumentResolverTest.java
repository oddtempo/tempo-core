package com.tempo.core.shared.infrastructure.tenant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TenantIdArgumentResolverTest {

    private TenantIdArgumentResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new TenantIdArgumentResolver();
        TenantContext.clear();
    }

    @Test
    @DisplayName("Should support UUID parameters annotated with @TenantId")
    void supportsParameter() {
        MethodParameter parameter = mock(MethodParameter.class);
        when(parameter.hasParameterAnnotation(TenantId.class)).thenReturn(true);
        when(parameter.getParameterType()).thenReturn((Class) UUID.class);

        assertTrue(resolver.supportsParameter(parameter));
    }

    @Test
    @DisplayName("Should resolve tenantId from TenantContext")
    void resolveArgument() throws Exception {
        UUID expectedTenantId = UUID.randomUUID();
        TenantContext.setCurrentTenantId(expectedTenantId);

        Object result = resolver.resolveArgument(
                mock(MethodParameter.class),
                mock(ModelAndViewContainer.class),
                mock(NativeWebRequest.class),
                mock(WebDataBinderFactory.class));

        assertEquals(expectedTenantId, result);
    }

    @Test
    @DisplayName("Should throw IllegalStateException if TenantContext is empty")
    void resolveArgumentNull() throws Exception {
        assertThrows(IllegalStateException.class, () -> resolver.resolveArgument(
                mock(MethodParameter.class),
                mock(ModelAndViewContainer.class),
                mock(NativeWebRequest.class),
                mock(WebDataBinderFactory.class)));
    }
}
