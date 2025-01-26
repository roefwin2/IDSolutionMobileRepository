package com.example.condo.feature.ssh.presenter.sites

import com.example.condo.feature.ssh.domain.models.CondoSite

data class CondoSitesState(
    val sites: List<CondoSite> = emptyList(),
)