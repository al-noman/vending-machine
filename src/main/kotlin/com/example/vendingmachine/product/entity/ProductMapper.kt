package com.example.vendingmachine.product.entity

import org.mapstruct.Mapper
import org.mapstruct.Mapping


@Mapper(componentModel = "spring")
interface ProductMapper {
    fun toEntity(product: Product): EProduct
    fun toEntityList(products: List<Product>): List<EProduct>

    @Mapping(target = "sellerId", source = "seller.id")
    fun toDto(product: EProduct): Product
    fun toDtoList(products: List<EProduct>): List<Product>
}