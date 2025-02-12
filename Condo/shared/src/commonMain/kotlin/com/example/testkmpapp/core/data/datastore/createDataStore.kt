package com.example.testkmpapp.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

fun createDataStore(producePath : () -> String) : DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath.invoke().toPath() })
}

internal const val DATA_STORE_FILE_NAME  = "prefs.preference.pb"