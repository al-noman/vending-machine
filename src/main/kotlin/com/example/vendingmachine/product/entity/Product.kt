package com.example.vendingmachine.product.entity

import com.example.vendingmachine.user.control.MultipleOfFive
import com.example.vendingmachine.user.entity.User
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY
import com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

data class Product (
    @get:JsonProperty(access = READ_ONLY) var id: UUID?,
    @get:NotNull @Positive val amountAvailable: Int,
    @get:NotNull @Positive @MultipleOfFive val cost: Int,
    @get:NotBlank val productName: String,
    @get:NotNull @JsonProperty(access = WRITE_ONLY) val sellerId: UUID,
    @JsonProperty(access = READ_ONLY) val seller: User?
)