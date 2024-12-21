package com.example.voip.voip.presenter.contacts

import androidx.lifecycle.ViewModel
import com.example.voip.voip.domain.ICondoVoip

data class Contact(
    val name: String,
    val number: String
)

class ContactsViewModel(
    private val voip: ICondoVoip
) : ViewModel() {
    val contacts = mutableListOf(
        Contact("Service Sécurité", "0123456789"),
        Contact("Maintenance", "0987654321")
    )

    fun callNumber(number: String) {
        voip.outgoingCall("sip:+33651690406@sip.linphone.org")
    }
}