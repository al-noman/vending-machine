package com.example.vendingmachine.user.entity

import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface UserMapper {
    fun toEntity(user: User): EUser
    fun toEntityList(users: List<User>): List<EUser>

    fun toDto(user: EUser): User
    fun toDtoList(users: List<EUser>): List<User>
}