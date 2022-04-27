package com.example.vendingmachine.authentication.control

import com.example.vendingmachine.authentication.entity.UserLoginCredentials
import com.example.vendingmachine.user.entity.EUser
import com.example.vendingmachine.user.entity.UserPasswordEncoder
import com.example.vendingmachine.user.entity.UserRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*
import javax.persistence.EntityNotFoundException
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@Component
class AuthenticationService(private val userRepository: UserRepository) {

    @Value("\${jwt.signing.secret.key}")
    private val secretKey: String? = null

    fun login(userLoginCredentials: UserLoginCredentials): Cookie {
        val user = getByUserName(userLoginCredentials.userName)
        if (!comparePassword(userLoginCredentials.password, user.password)) {
            throw SecurityException("Invalid user name or password provided")
        }

        return createCookie(user)
    }

    private fun getByUserName(userName: String) = userRepository.findByUserName(userName) ?: throw EntityNotFoundException(userName)

    private fun comparePassword(providedPassword: String, storedPassword: String) = UserPasswordEncoder.passwordEncoder.matches(providedPassword, storedPassword)

    private fun createCookie(user: EUser): Cookie {
        val cookie = Cookie("jwt", createJWTToken(user))
        cookie.isHttpOnly = true
        return cookie
    }

    private fun createJWTToken(user: EUser): String {
        return Jwts.builder()
            .setIssuer(user.id.toString())
            .setExpiration(Date(System.currentTimeMillis() + (1000 * 60 * 60)))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact()
    }

    fun authenticate(jwt: String?): EUser {
        try {
            if (jwt == null) throw SecurityException("Authentication failure")
            val issuer = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt).body.issuer
            return userRepository.findByIdOrNull(UUID.fromString(issuer)) ?: throw SecurityException("Authentication failure")
        } catch (e: Exception) {
            throw SecurityException("Authentication failure")
        }
    }

    fun logout(response: HttpServletResponse) {
        val cookie = Cookie("jwt", "")
        cookie.maxAge = 0
        response.addCookie(cookie)
    }
}