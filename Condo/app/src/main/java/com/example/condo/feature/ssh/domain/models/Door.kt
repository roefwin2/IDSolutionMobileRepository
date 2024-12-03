package com.example.condo.feature.ssh.domain.models

import com.example.condo.core.presentation.helper.Resource

data class Door(
    val name : String,
    val isOpen: Resource<Boolean>  ,
    val number : Int
)

