package com.example.vendingmachine.user.entity

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

object UserPasswordEncoder {
    val passwordEncoder = BCryptPasswordEncoder()
}