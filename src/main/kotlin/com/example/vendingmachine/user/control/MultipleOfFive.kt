package com.example.vendingmachine.user.control

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Constraint(validatedBy = [MultipleOfFiveValidator::class])
annotation class MultipleOfFive(
    val message: String = "value must be multiple of five, i.e [0, 5, 10, 15, ...]",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class MultipleOfFiveValidator : ConstraintValidator<MultipleOfFive, Int> {
    override fun isValid(value: Int?, context: ConstraintValidatorContext?): Boolean {
        if (value != null && value % 5 != 0) {
            return false
        }
        return true
    }
}
