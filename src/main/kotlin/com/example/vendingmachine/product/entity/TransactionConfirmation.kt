package com.example.vendingmachine.product.entity

data class TransactionConfirmation (
    val totalSpent: Int,
    val purchasedProduct: Product,
    val change: IntArray
)