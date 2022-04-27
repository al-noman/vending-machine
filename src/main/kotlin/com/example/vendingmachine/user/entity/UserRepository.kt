package com.example.vendingmachine.user.entity

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository: JpaRepository<EUser, UUID> {
    fun findByUserName(userName: String): EUser?
}