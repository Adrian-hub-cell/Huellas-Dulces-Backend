package com.huellasdulces.mapper;

import com.huellasdulces.dto.ProductRequest;
import com.huellasdulces.dto.ProductResponse;
import com.huellasdulces.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "available", constant = "true")
    Product toEntity(ProductRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "available", ignore = true)
    void updateProductFromRequest(ProductRequest request, @MappingTarget Product product);
}
