package com.example.vendingmachine.product.control

import com.example.vendingmachine.common.exception.TradingError.INSUFFICIENT_DEPOSIT_AMOUNT
import com.example.vendingmachine.common.exception.TradingError.INSUFFICIENT_PRODUCT_AMOUNT
import com.example.vendingmachine.product.entity.EProduct
import com.example.vendingmachine.product.entity.ProductRepository
import com.example.vendingmachine.user.entity.EUser
import com.example.vendingmachine.user.entity.Role
import com.example.vendingmachine.user.entity.Role.BUYER
import com.example.vendingmachine.user.entity.Role.SELLER
import com.example.vendingmachine.user.entity.UserRepository
import liquibase.repackaged.org.apache.commons.lang3.SystemUtils.USER_NAME
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class ProductServiceTest {
    @InjectMocks
    private lateinit var sut: ProductService
    @Mock
    private lateinit var productRepository: ProductRepository
    @Mock
    private lateinit var userRepository: UserRepository

    @Test
    fun `buy should return proper result`() {
        `when`(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(E_PRODUCT))
        `when`(productRepository.save(E_PRODUCT)).thenReturn(E_PRODUCT)
        `when`(userRepository.save(E_USER)).thenReturn(E_USER)

        val (totalSpent, eProduct, change) = sut.buy(PRODUCT_ID, BUYING_AMOUNT, E_USER)

        assertThat(totalSpent).isEqualTo(E_PRODUCT.cost * BUYING_AMOUNT)
        assertThat(eProduct.amountAvailable).isEqualTo(PRODUCT_AMOUNT_BEFORE_BUYING - BUYING_AMOUNT)
        assertThat(change).isEqualTo(CHANGE)
    }

    @Test
    fun `buy should throw UnsupportedOperationException for seller role`() {
        `when`(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(E_PRODUCT))

        assertThatThrownBy { sut.buy(PRODUCT_ID, BUYING_AMOUNT , createEUser(role = SELLER)) }
            .isInstanceOf(UnsupportedOperationException::class.java)
            .hasMessageContaining("$SELLER can not buy")
    }

    @Test
    fun `buy should throw IllegalArgumentException for insufficient amount of product`() {
        `when`(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(E_PRODUCT))

        assertThatThrownBy { sut.buy(PRODUCT_ID, (PRODUCT_AMOUNT_BEFORE_BUYING + 1), E_USER) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("$INSUFFICIENT_PRODUCT_AMOUNT")
    }

    @Test
    fun `buy should throw IllegalArgumentException for insufficient deposit amount in buyer account`() {
        `when`(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(E_PRODUCT))

        assertThatThrownBy { sut.buy(PRODUCT_ID, BUYING_AMOUNT, createEUser(deposit = 50)) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("$INSUFFICIENT_DEPOSIT_AMOUNT")
    }

    companion object {
        private const val BUYING_AMOUNT = 3
        private const val PRODUCT_AMOUNT_BEFORE_BUYING = 10
        private const val PRODUCT_COST = 55
        private const val BUYER_DEPOSIT = 225
        private val CHANGE = intArrayOf(50, 10)
        private val PRODUCT_ID = UUID.randomUUID()

        private val E_USER = createEUser()
        private val E_PRODUCT = createEProduct()

        private fun createEProduct(): EProduct {
            val eProduct = EProduct()
            eProduct.id = PRODUCT_ID
            eProduct.amountAvailable = PRODUCT_AMOUNT_BEFORE_BUYING
            eProduct.cost = PRODUCT_COST
            eProduct.seller = E_USER
            return eProduct
        }

        private fun createEUser(role: Role = BUYER, deposit: Int = BUYER_DEPOSIT): EUser {
            val eUser = EUser()
            eUser.id = UUID.randomUUID()
            eUser.userName = USER_NAME
            eUser.deposit = deposit
            eUser.role = role
            return eUser
        }
    }
}