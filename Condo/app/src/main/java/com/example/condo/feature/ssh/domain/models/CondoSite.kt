package com.example.condo.feature.ssh.domain.models

data class CondoSite(
    val siteName : String,
    val host : String,
    val port : Int,
    val doors : Set<Door> = setOf()
)
