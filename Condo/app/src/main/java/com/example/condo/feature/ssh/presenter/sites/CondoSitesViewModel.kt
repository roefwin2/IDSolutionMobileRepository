package com.example.condo.feature.ssh.presenter.sites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.condo.core.data.networking.Result
import com.example.condo.core.presentation.helper.Loading
import com.example.condo.core.presentation.helper.Resource
import com.example.condo.core.presentation.helper.Success
import com.example.condo.feature.ssh.domain.CondoSSHRepository
import com.example.condo.feature.ssh.domain.models.CondoSite
import com.example.condo.feature.ssh.domain.usecases.OpenDoorUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CondoSitesViewModel(
    private val openDoorUseCase: OpenDoorUseCase,
    private val condoSSHRepository: CondoSSHRepository
) : ViewModel() {

    private val _state: MutableStateFlow<CondoSitesState> = MutableStateFlow(CondoSitesState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val result = condoSSHRepository.domains()
            if (result is Result.Success) {
                _state.update {
                    CondoSitesState(result.data)
                }
            }
        }
    }

    fun onDoorChange(condoSite: CondoSite, doorNumber: Int, open: Boolean) {
        val loadingCondoSites = updateCondoSites(condoSite, doorNumber, Loading())
        _state.update {
            it.copy(sites = loadingCondoSites)
        }
        viewModelScope.launch {
            val result = openDoorUseCase.invoke(condoSite, doorNumber)
            if (result is Result.Success) {
                val updatedCondoSites = updateCondoSites(condoSite, doorNumber, Success(open))
                _state.update {
                    it.copy(sites = updatedCondoSites)
                }
            }
        }
    }

    private fun updateCondoSites(
        condoSite: CondoSite,
        doorNumber: Int,
        statusRes: Resource<Boolean>
    ): List<CondoSite> {
        val updatedCondoSite = updateDoorStatus(
            condoSite = condoSite,
            doorNumber = doorNumber,
            newStatus = statusRes
        )
        val updatedCondoSites = state.value.sites.map {
            if (condoSite.siteName == it.siteName) updatedCondoSite else it
        }
        return updatedCondoSites
    }

    private fun updateDoorStatus(
        condoSite: CondoSite,
        doorNumber: Int,
        newStatus: Resource<Boolean>
    ): CondoSite {
        // Trouver et modifier la porte cible
        val updatedDoors = condoSite.doors.map { door ->
            if (door.number == doorNumber) door.copy(isOpen = newStatus) else door
        }.toSet()

        // Retourner un nouvel objet CondoSite avec les portes mises à jour
        return condoSite.copy(doors = updatedDoors)
    }
}