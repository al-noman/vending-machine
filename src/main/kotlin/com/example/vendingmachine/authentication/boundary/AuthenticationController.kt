package com.example.vendingmachine.authentication.boundary

import com.example.vendingmachine.authentication.control.AuthenticationService
import com.example.vendingmachine.authentication.entity.UserLoginCredentials
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping
class AuthenticationController (private val authenticationService: AuthenticationService) {

    @PostMapping("/login")
    fun login(@RequestBody @Valid userLoginCredentials: UserLoginCredentials, response: HttpServletResponse)
            : ResponseEntity<String> {
        val cookie: Cookie = authenticationService.login(userLoginCredentials)
        response.addCookie(cookie)
        return ResponseEntity.ok("Logged in successfully")
    }

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<String> {
        authenticationService.logout(response)
        return ResponseEntity.ok("Logged out successfully")
    }
}