package com.example.vendingmachine.product.boundary

import com.example.vendingmachine.authentication.control.AuthenticationService
import com.example.vendingmachine.product.control.ProductService
import com.example.vendingmachine.product.entity.EProduct
import com.example.vendingmachine.product.entity.Product
import com.example.vendingmachine.product.entity.ProductMapper
import com.example.vendingmachine.user.boundary.UserControllerTest.Companion.E_USER
import com.example.vendingmachine.user.boundary.UserControllerTest.Companion.USER
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class ProductControllerTest {
    @InjectMocks
    private lateinit var sut: ProductController
    @Mock
    private lateinit var productService: ProductService
    @Mock
    private lateinit var productMapper: ProductMapper
    @Mock
    private lateinit var authenticationService: AuthenticationService

    @Test
    fun `buy should delegate to service and respond with proper status`() {
        `when`(authenticationService.authenticate(anyString())).thenReturn(E_USER)
        `when`(productService.buy(
            PRODUCT_ID,
            BUYING_AMOUNT,
            E_USER
        )).thenReturn(
            Triple(TOTAL_SPENT, E_PRODUCT, CHANGE)
        )
        `when`(productMapper.toDto(E_PRODUCT)).thenReturn(PRODUCT)

        val response = sut.buy(PRODUCT_ID, BUYING_AMOUNT, "COOKIE")

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).hasFieldOrPropertyWithValue("totalSpent", TOTAL_SPENT)
        assertThat(response.body).hasFieldOrPropertyWithValue("purchasedProduct", PRODUCT)
        assertThat(response.body).hasFieldOrPropertyWithValue("change", CHANGE)
        verify(productService).buy(PRODUCT_ID, BUYING_AMOUNT, E_USER)
    }

    companion object {
        private const val TOTAL_SPENT = 165
        private const val BUYING_AMOUNT = 3
        private val CHANGE = intArrayOf(20, 20)
        private val PRODUCT_ID = UUID.randomUUID()

        private val E_PRODUCT = createEProduct()
        private val PRODUCT = createProduct()

        private fun createEProduct(): EProduct {
            val eProduct = EProduct()
            eProduct.id = PRODUCT_ID
            eProduct.amountAvailable = 10
            eProduct.cost = 55
            return eProduct
        }

        private fun createProduct(): Product {
            return Product(
                amountAvailable = 10,
                cost = 55,
                productName = "RANDOM_NAME",
                sellerId = UUID.randomUUID(),
                id = PRODUCT_ID,
                seller = USER
            )
        }
    }
}