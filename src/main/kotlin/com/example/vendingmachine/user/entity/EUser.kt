package com.example.vendingmachine.user.entity

import com.example.vendingmachine.user.entity.UserPasswordEncoder.passwordEncoder
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "USER")
class EUser {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var id: UUID? = null
    var userName = ""
    var password: String = ""
        set(value) {
            field = passwordEncoder.encode(value)
        }
    var deposit = 0

    @Enumerated(EnumType.STRING)
    var role: Role? = null
}