package com.example.vendingmachine.authentication.entity

import javax.validation.constraints.NotBlank

data class UserLoginCredentials (
    @get:NotBlank val userName: String,
    @get:NotBlank val password: String
)
