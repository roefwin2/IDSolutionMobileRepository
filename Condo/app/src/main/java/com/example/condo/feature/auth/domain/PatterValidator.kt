package com.example.condo.feature.auth.domain

interface PatternValidator {
    fun matches(value: String): Boolean
}