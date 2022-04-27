package com.example.vendingmachine.user.boundary

import com.example.vendingmachine.authentication.control.AuthenticationService
import com.example.vendingmachine.user.control.UserService
import com.example.vendingmachine.user.entity.EUser
import com.example.vendingmachine.user.entity.Role.BUYER
import com.example.vendingmachine.user.entity.User
import com.example.vendingmachine.user.entity.UserMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus.OK
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class UserControllerTest {
    @InjectMocks
    private lateinit var sut: UserController
    @Mock
    private lateinit var userService: UserService
    @Mock
    private lateinit var authenticationService: AuthenticationService
    @Mock
    private lateinit var userMapper: UserMapper

    @Test
    fun `deposit should delegate to service and respond with proper status`() {
        `when`(authenticationService.authenticate(anyString())).thenReturn(E_USER)
        `when`(userService.deposit(DEPOSIT_AMOUNT, E_USER)).thenReturn(E_USER)
        `when`(userMapper.toDto(E_USER)).thenReturn(USER)

        val response = sut.deposit(DEPOSIT_AMOUNT, "COOKIE")

        assertThat(response.statusCode).isEqualTo(OK)
        assertThat(response.body).isEqualTo(USER)
        verify(userService).deposit(DEPOSIT_AMOUNT, E_USER)
    }

    @Test
    fun `reset should delegate to service and respond with proper status`() {
        `when`(authenticationService.authenticate(anyString())).thenReturn(E_USER)
        `when`(userService.resetDeposit(E_USER)).thenReturn(E_USER)
        `when`(userMapper.toDto(E_USER)).thenReturn(USER)

        val response = sut.reset("COOKIE")

        assertThat(response.statusCode).isEqualTo(OK)
        assertThat(response.body).isEqualTo(USER)
        verify(userService).resetDeposit(E_USER)
    }

    companion object {
        private const val DEPOSIT_AMOUNT = 50
        private const val USER_NAME = "USER_NAME"
        val USER = createUser()
        val E_USER = createEUser()

        private fun createUser(): User {
            return User(
                id = null,
                userName = USER_NAME,
                password = "",
                deposit = DEPOSIT_AMOUNT,
                role = BUYER
            )
        }

        private fun createEUser(): EUser {
            val eUser = EUser()
            eUser.id = UUID.randomUUID()
            eUser.userName = USER_NAME
            eUser.deposit = DEPOSIT_AMOUNT
            eUser.role = BUYER
            return eUser
        }
    }
}