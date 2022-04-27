package com.example.vendingmachine.common.exception

import java.time.LocalDate

data class ExceptionResponse(val date: LocalDate, val message: String, val details: String)
