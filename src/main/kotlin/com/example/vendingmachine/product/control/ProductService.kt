package com.example.vendingmachine.product.control

import com.example.vendingmachine.common.AllowedCurrency
import com.example.vendingmachine.common.exception.TradingError.INSUFFICIENT_DEPOSIT_AMOUNT
import com.example.vendingmachine.common.exception.TradingError.INSUFFICIENT_PRODUCT_AMOUNT
import com.example.vendingmachine.product.entity.EProduct
import com.example.vendingmachine.product.entity.ProductRepository
import com.example.vendingmachine.user.entity.EUser
import com.example.vendingmachine.user.entity.Role.BUYER
import com.example.vendingmachine.user.entity.Role.SELLER
import com.example.vendingmachine.user.entity.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
class ProductService (private val productRepository: ProductRepository, private val userRepository: UserRepository) {

    fun create(eProduct: EProduct, authenticatedUser: EUser): EProduct {
        checkAuthorization(eProduct, authenticatedUser)
        eProduct.seller = findSellerById(eProduct.sellerId)
        return productRepository.save(eProduct)
    }

    private fun findSellerById(sellerId: UUID?): EUser {
        val seller = userRepository.findByIdOrNull(sellerId) ?: throw EntityNotFoundException("$sellerId")
        if (seller.role != SELLER) throw UnsupportedOperationException("User does not have Seller role")
        return seller
    }

    fun findAll(): List<EProduct> = productRepository.findAll()

    fun findOne(id: UUID): EProduct {
        val eProduct = productRepository.findByIdOrNull(id) ?: throw EntityNotFoundException("$id")
        eProduct.sellerId = eProduct.seller?.id!!
        return eProduct
    }

    fun update(id: UUID, eProduct: EProduct, authenticatedUser: EUser): EProduct {
        return findOne(id).let {
            checkAuthorization(it, authenticatedUser)
            updateProperties(it, eProduct)
            productRepository.save(it)
        }
    }

    private fun updateProperties(currentProduct: EProduct, updatedProduct: EProduct) {
        currentProduct.seller = findSellerById(updatedProduct.sellerId)
        currentProduct.amountAvailable = updatedProduct.amountAvailable
        currentProduct.cost = updatedProduct.cost
        currentProduct.productName = updatedProduct.productName
    }

    fun deleteById(id: UUID, authenticatedUser: EUser) {
        val eProduct = findOne(id)
        checkAuthorization(eProduct, authenticatedUser)
        productRepository.deleteById(id)
    }

    private fun checkAuthorization(eProduct: EProduct, authenticatedUser: EUser) {
        if (eProduct.sellerId != authenticatedUser.id) {
            throw SecurityException("Access denied!")
        }
    }

    fun buy(productId: UUID, amount: Int, authenticatedUser: EUser): Triple<Int, EProduct, IntArray> {
        var eProduct = findOne(productId)
        val requiredBalance = eProduct.cost * amount

        validateTrading(
            authenticatedUser = authenticatedUser,
            amount = amount,
            eProduct = eProduct,
            requiredBalance = requiredBalance
        )

        eProduct.amountAvailable -= amount
        eProduct = productRepository.save(eProduct)

        val change = authenticatedUser.deposit - requiredBalance
        val changeItems: IntArray? = createArrayOfChangeItems(change, AllowedCurrency.CENTS)

        authenticatedUser.deposit = change
        userRepository.save(authenticatedUser)

        return Triple(requiredBalance, eProduct, changeItems ?: intArrayOf())
    }

    private fun validateTrading(
        authenticatedUser: EUser,
        amount: Int,
        eProduct: EProduct,
        requiredBalance: Int
    ) {
        if (authenticatedUser.role != BUYER) {
            throw UnsupportedOperationException("$SELLER can not buy")
        }

        if (amount > eProduct.amountAvailable) {
            throw IllegalArgumentException("$INSUFFICIENT_PRODUCT_AMOUNT")
        }

        if (requiredBalance > authenticatedUser.deposit) {
            throw IllegalArgumentException("$INSUFFICIENT_DEPOSIT_AMOUNT")
        }
    }

    private fun createArrayOfChangeItems(targetChangeAmount: Int, cents: IntArray): IntArray? {
        val table = arrayOfNulls<IntArray>(targetChangeAmount + 1)
        table[0] = intArrayOf()
        for (i in table.indices) {
            if (table[i] != null) {
                for (cent in cents) {
                    val targetIndex = i + cent
                    if (targetIndex <= targetChangeAmount) {
                        if (table[targetIndex] == null || table[i]!!.size + 1 < table[targetIndex]!!.size) {
                            table[targetIndex] = table[i]?.let { createNewArray(it, cent) }
                        }
                    }
                }
            }
        }
        return table[targetChangeAmount]
    }

    private fun createNewArray(existing: IntArray, n: Int): IntArray {
        val newArray = IntArray(existing.size + 1)
        newArray[0] = n
        System.arraycopy(existing, 0, newArray, 1, newArray.size - 1)
        return newArray
    }
}
