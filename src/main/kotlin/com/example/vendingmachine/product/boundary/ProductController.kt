package com.example.vendingmachine.product.boundary

import com.example.vendingmachine.authentication.control.AuthenticationService
import com.example.vendingmachine.product.control.ProductService
import com.example.vendingmachine.product.entity.Product
import com.example.vendingmachine.product.entity.ProductMapper
import com.example.vendingmachine.product.entity.TransactionConfirmation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService,
    private val productMapper: ProductMapper,
    private val authenticationService: AuthenticationService
) {

    @PostMapping
    fun create(@RequestBody @Valid product: Product, @CookieValue("jwt") jwt: String?): ResponseEntity<Product> {
        val authenticatedUser = authenticationService.authenticate(jwt)
        val eProduct = productService.create(productMapper.toEntity(product), authenticatedUser)
        return ResponseEntity.status(HttpStatus.CREATED).body(productMapper.toDto(eProduct))
    }

    @GetMapping
    fun findAll(): ResponseEntity<List<Product>> {
        val eProducts = productService.findAll()
        return ResponseEntity.ok(productMapper.toDtoList(eProducts))
    }

    @GetMapping("/{id}")
    fun findOne(@PathVariable id: UUID): ResponseEntity<Product> {
        val eProduct = productService.findOne(id)
        return ResponseEntity.ok(productMapper.toDto(eProduct))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody @Valid product: Product, @CookieValue("jwt") jwt: String?)
    : ResponseEntity<Product> {
        val authenticatedUser = authenticationService.authenticate(jwt)
        val eProduct = productService.update(id, productMapper.toEntity(product), authenticatedUser)
        return ResponseEntity.ok(productMapper.toDto(eProduct))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID, @CookieValue("jwt") jwt: String?): ResponseEntity<Unit> {
        val authenticatedUser = authenticationService.authenticate(jwt)
        productService.deleteById(id, authenticatedUser)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/buy")
    fun buy(@RequestParam(name = "id") productId: UUID, @RequestParam amount: Int, @CookieValue("jwt") jwt: String?)
    : ResponseEntity<TransactionConfirmation> {
        val authenticatedUser = authenticationService.authenticate(jwt)
        val (totalSpent, purchasedProduct, change) = productService.buy(productId, amount, authenticatedUser)
        return ResponseEntity.ok(
            TransactionConfirmation(
                totalSpent = totalSpent,
                purchasedProduct = productMapper.toDto(purchasedProduct),
                change = change
            )
        )
    }
}