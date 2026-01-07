package com.tempo.core.product.application.service;

import com.tempo.core.product.application.model.response.ProductResponse;
import com.tempo.core.product.domain.model.OptionValue;
import com.tempo.core.product.domain.model.Product;
import com.tempo.core.product.domain.model.ProductOption;
import com.tempo.core.product.domain.model.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "options", source = "options")
    @Mapping(target = "variants", source = "variants")
    ProductResponse toProductResponse(Product product);

    List<ProductResponse> toProductResponseList(List<Product> products);

    @Mapping(target = "values", source = "values", qualifiedByName = "optionValuesToStrings")
    ProductResponse.OptionResponse toOptionResponse(ProductOption option);

    ProductResponse.VariantResponse toVariantResponse(ProductVariant variant);

    @Named("optionValuesToStrings")
    default List<String> optionValuesToStrings(Set<OptionValue> values) {
        return values.stream().map(OptionValue::getValue).toList();
    }
}
