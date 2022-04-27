package com.example.vendingmachine.common.exception

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDate
import javax.persistence.EntityNotFoundException


@ControllerAdvice
class ApplicationExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val response = ExceptionResponse(LocalDate.now(), "Validation failed", ex.bindingResult.toString())
        return ResponseEntity.badRequest().body(response)
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        return ResponseEntity.badRequest().body(ExceptionResponse(
            date = LocalDate.now(),
            message = "JSON parse error: Cannot deserialize value",
            details = ex.message?: "Http message not readable"
        ))
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: EntityNotFoundException, request: WebRequest): ResponseEntity<ExceptionResponse> {
        val response = ExceptionResponse(LocalDate.now(), ex.message?: "Entity not found", request.getDescription(false))
        return ResponseEntity.status(NOT_FOUND).body(response)
    }

    @ExceptionHandler(UnsupportedOperationException::class)
    fun handleUnsupportedOperationException(ex: UnsupportedOperationException, request: WebRequest): ResponseEntity<ExceptionResponse> {
        val response = ExceptionResponse(LocalDate.now(), ex.message?: "Unauthorized", request.getDescription(false))
        return ResponseEntity.status(FORBIDDEN).body(response)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException, request: WebRequest): ResponseEntity<ExceptionResponse> {
        val response = ExceptionResponse(LocalDate.now(), ex.message?: "Unauthorized", request.getDescription(false))
        return ResponseEntity.badRequest().body(response)
    }

    @ExceptionHandler(SecurityException::class)
    fun handleSecurityException(ex: SecurityException, request: WebRequest): ResponseEntity<ExceptionResponse> {
        val response = ExceptionResponse(LocalDate.now(), ex.message?: "Authentication failure", request.getDescription(false))
        return ResponseEntity.status(UNAUTHORIZED).body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllOtherException(ex: Exception, request: WebRequest): ResponseEntity<ExceptionResponse> {
        val response = ExceptionResponse(LocalDate.now(), ex.message?: "Internal server error", request.getDescription(false))
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response)
    }
}