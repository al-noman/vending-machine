package com.example.vendingmachine.user.entity

import com.example.vendingmachine.user.control.MultipleOfFive
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY
import com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.PositiveOrZero

data class User (
    @get:JsonProperty(access = READ_ONLY) var id: UUID?,
    @get:NotBlank val userName: String,
    @get:NotBlank @JsonProperty(access = WRITE_ONLY) val password: String,
    @get:PositiveOrZero @MultipleOfFive val deposit: Int,
    @get:NotNull val role: Role
)