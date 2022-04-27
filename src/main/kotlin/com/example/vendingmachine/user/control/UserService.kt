package com.example.vendingmachine.user.control

import com.example.vendingmachine.common.AllowedCurrency.CENTS
import com.example.vendingmachine.common.exception.DepositAmountNotAcceptedException
import com.example.vendingmachine.user.entity.EUser
import com.example.vendingmachine.user.entity.Role
import com.example.vendingmachine.user.entity.Role.BUYER
import com.example.vendingmachine.user.entity.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
class UserService (private val userRepository: UserRepository) {

    fun create(eUser: EUser) = userRepository.save(eUser)

    fun findOne(id: UUID) = userRepository.findByIdOrNull(id) ?: throw EntityNotFoundException("$id")

    fun findAll(): List<EUser> = userRepository.findAll()

    fun update(id: UUID, eUser: EUser): EUser {
        return userRepository.findByIdOrNull(id)?.let {
            updateProperties(it, eUser)
            userRepository.save(it)
        } ?: throw EntityNotFoundException("$id")
    }

    private fun updateProperties(currentUser: EUser, updatedUser: EUser) {
        currentUser.userName = updatedUser.userName
        currentUser.password = updatedUser.password
        currentUser.deposit = updatedUser.deposit
        currentUser.role = updatedUser.role
    }

    fun delete(id: UUID) = userRepository.deleteById(id)

    fun deposit(amount: Int, authenticatedUser: EUser): EUser {
        validateDepositAmount(amount)
        validateBuyerRole(authenticatedUser)

        authenticatedUser.deposit += amount
        return userRepository.save(authenticatedUser)
    }

    fun resetDeposit(authenticatedUser: EUser): EUser {
        validateBuyerRole(authenticatedUser)
        authenticatedUser.deposit = 0
        return userRepository.save(authenticatedUser)
    }

    private fun validateBuyerRole(authenticatedUser: EUser) {
        if (authenticatedUser.role != BUYER) {
            throw UnsupportedOperationException("${Role.SELLER} can not deposit")
        }
    }

    private fun validateDepositAmount(amount: Int) {
        if (amount !in CENTS) {
            throw DepositAmountNotAcceptedException("Can not insert $amount cent")
        }
    }
}
