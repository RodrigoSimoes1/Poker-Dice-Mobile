package com.example.myapplication.login.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.myapplication.login.domain.AuthInfo
import com.example.myapplication.login.domain.AuthInfoRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Implementation of AuthInfoRepo that uses SharedPreferences to store authentication information.
 */
class AuthInfoPreferencesRepo(
    private val store: DataStore<Preferences>,
) : AuthInfoRepo {
    private val userEmailKey: Preferences.Key<String> = stringPreferencesKey(name = "user_email")
    private val authTokenKey: Preferences.Key<String> = stringPreferencesKey(name = "auth_token")
    private val userIdKey: Preferences.Key<Int> = intPreferencesKey(name = "user_id")

    override val authInfo: Flow<AuthInfo?>
        get() =
            store.data.map { preferences ->
                preferences.toAuthInfo()
            }

    override suspend fun saveAuthInfo(authInfo: AuthInfo) {
        store.edit { preferences ->
            preferences[userIdKey] = authInfo.userId
            preferences[userEmailKey] = authInfo.userEmail
            preferences[authTokenKey] = authInfo.authToken
        }
    }

    override suspend fun getAuthInfo(): AuthInfo? {
        val preferences: Preferences = store.data.first()
        return preferences.toAuthInfo()
    }

    override suspend fun clearAuthInfo() {
        store.edit { it.clear() }
    }

    fun Preferences.toAuthInfo(): AuthInfo? =
        this[userEmailKey]?.let {
            val email = it
            val token = this[authTokenKey] ?: return null
            val id = this[userIdKey] ?: return null
            AuthInfo(userEmail = email, authToken = token, userId = id)
        }
}
