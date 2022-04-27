package com.example.vendingmachine.user.boundary

import com.example.vendingmachine.authentication.control.AuthenticationService
import com.example.vendingmachine.user.control.UserService
import com.example.vendingmachine.user.entity.EUser
import com.example.vendingmachine.user.entity.User
import com.example.vendingmachine.user.entity.UserMapper
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/users")
class UserController (
    private val authenticationService: AuthenticationService,
    private val userService: UserService,
    private val userMapper: UserMapper
){
    @PostMapping
    fun create(@RequestBody @Valid user: User): ResponseEntity<User> {
        val eUser = userService.create(userMapper.toEntity(user))
        return ResponseEntity.status(CREATED).body(userMapper.toDto(eUser))
    }

    @GetMapping("/{id}")
    fun findOne(@PathVariable id: UUID, @CookieValue("jwt") jwt: String?): ResponseEntity<User> {
        authenticationService.authenticate(jwt)
        val eUser = userService.findOne(id)
        return ResponseEntity.ok(userMapper.toDto(eUser))
    }

    @GetMapping
    fun findAll(@CookieValue("jwt") jwt: String?): ResponseEntity<List<User>> {
        authenticationService.authenticate(jwt)
        val eUsers = userService.findAll()
        return ResponseEntity.ok(userMapper.toDtoList(eUsers))
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody @Valid user: User, @CookieValue("jwt") jwt: String?)
    : ResponseEntity<User> {
        authenticationService.authenticate(jwt)
        val eUser = userService.update(id, userMapper.toEntity(user))
        return ResponseEntity.ok(userMapper.toDto(eUser))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID, @CookieValue("jwt") jwt: String?): ResponseEntity<Unit> {
        authenticationService.authenticate(jwt)
        userService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/deposit")
    fun deposit(@RequestParam amount: Int, @CookieValue("jwt") jwt: String?): ResponseEntity<User> {
        val authenticatedUser = authenticationService.authenticate(jwt)
        val eUser = userService.deposit(amount, authenticatedUser)
        return ResponseEntity.ok(userMapper.toDto(eUser))
    }

    @PostMapping("/reset")
    fun reset(@CookieValue("jwt") jwt: String?): ResponseEntity<User> {
        val authenticatedUser = authenticationService.authenticate(jwt)
        val eUser: EUser = userService.resetDeposit(authenticatedUser)
        return ResponseEntity.ok(userMapper.toDto(eUser))
    }
}