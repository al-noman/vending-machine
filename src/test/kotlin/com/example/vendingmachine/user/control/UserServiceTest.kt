package com.example.vendingmachine.user.control

import com.example.vendingmachine.common.AllowedCurrency.CENTS
import com.example.vendingmachine.common.exception.DepositAmountNotAcceptedException
import com.example.vendingmachine.user.entity.EUser
import com.example.vendingmachine.user.entity.Role
import com.example.vendingmachine.user.entity.Role.BUYER
import com.example.vendingmachine.user.entity.Role.SELLER
import com.example.vendingmachine.user.entity.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class UserServiceTest {
    @InjectMocks
    private lateinit var sut: UserService
    @Mock
    private lateinit var userRepository: UserRepository

    @Test
    fun `deposit should add to the current deposit amount of the buyer and delegate to the repository`() {
        val updatedEUser = createEUser(deposit = ALLOWED_DEPOSIT_AMOUNT + ORIGINAL_DEPOSIT_AMOUNT)

        `when`(userRepository.save(E_USER)).thenReturn(updatedEUser)

        val eUser = sut.deposit(ALLOWED_DEPOSIT_AMOUNT, E_USER)

        assertThat(CENTS).contains(ALLOWED_DEPOSIT_AMOUNT)
        assertThat(E_USER.role).isEqualTo(BUYER)
        assertThat(eUser.deposit).isEqualTo(ALLOWED_DEPOSIT_AMOUNT + ORIGINAL_DEPOSIT_AMOUNT)
    }

    @Test
    fun `deposit should throw DepositAmountNotAcceptedException when trying to deposit non allowed amount`() {
        assertThatThrownBy { sut.deposit(NON_ALLOWED_DEPOSIT_AMOUNT, E_USER) }
            .isInstanceOf(DepositAmountNotAcceptedException::class.java)
            .hasMessageContaining("Can not insert $NON_ALLOWED_DEPOSIT_AMOUNT cent")
    }

    @Test
    fun `deposit should throw UnsupportedOperationException when the depositor does not have BUYER role`() {
        assertThatThrownBy { sut.deposit(ALLOWED_DEPOSIT_AMOUNT, createEUser(role = SELLER)) }
            .isInstanceOf(UnsupportedOperationException::class.java)
            .hasMessageContaining("$SELLER can not deposit")
    }

    @Test
    fun `reset should set the deposit amount to zero`() {
        `when`(userRepository.save(E_USER)).thenReturn(createEUser(deposit = 0))

        sut.resetDeposit(E_USER)

        assertThat(E_USER.deposit).isEqualTo(0)
    }

    companion object {
        private const val ALLOWED_DEPOSIT_AMOUNT = 50
        private const val ORIGINAL_DEPOSIT_AMOUNT = 20
        private const val NON_ALLOWED_DEPOSIT_AMOUNT = 55
        val E_USER = createEUser()

        private fun createEUser(deposit: Int = ORIGINAL_DEPOSIT_AMOUNT, role: Role = BUYER): EUser {
            val eUser = EUser()
            eUser.id = UUID.randomUUID()
            eUser.userName = "USER_NAME"
            eUser.deposit = deposit
            eUser.role = role
            return eUser
        }
    }
}