package io.github.ifa.glancewidget.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.CoroutineScope

fun createDataStore(
    coroutineScope: CoroutineScope,
    context: Context,
    producePath: () -> String,
): DataStore<Preferences> = PreferenceDataStoreFactory.create(
    corruptionHandler = ReplaceFileCorruptionHandler(
        produceNewData = { emptyPreferences() }
    ),
    migrations = emptyList(),
    scope = coroutineScope,
    produceFile = { context.preferencesDataStoreFile(producePath()) },
)
