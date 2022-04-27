package com.example.vendingmachine.product.entity

import com.example.vendingmachine.user.entity.EUser
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "PRODUCT")
class EProduct {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var id: UUID? = null
    var amountAvailable = 0
    var cost = 0
    var productName = ""

    @ManyToOne
    @JoinColumn(name = "SELLER")
    var seller: EUser? = null

    @Transient
    lateinit var sellerId: UUID
}