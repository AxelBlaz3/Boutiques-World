package com.boutiquesworld.util

import android.content.Context
import com.boutiquesworld.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val KEY_BOUTIQUES_SHARED_PREFERENCES = BuildConfig.APPLICATION_ID
private const val KEY_SESSION = "${BuildConfig.APPLICATION_ID}.KEY_SESSION"

/**
 * Util class to maintain the login session of the retailer
 */
@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {
    private val boutiquesPreferences =
        context.getSharedPreferences(KEY_BOUTIQUES_SHARED_PREFERENCES, Context.MODE_PRIVATE)

    /**
     * Save the current session of user.
     * @param isLoggedIn: Specifies whether the login was successful or not.
     * If [isLoggedIn] is true, then upon next launch of the app, ProductsFragment
     * is opened. LoginFragment otherwise.
     */
    fun saveSession(isLoggedIn: Boolean) =
        boutiquesPreferences.edit().putBoolean(KEY_SESSION, isLoggedIn).apply()

    /**
     * Get the saved session if any.
     * @return: true if previously logged in, false otherwise.
     */
    fun getSession(): Boolean = boutiquesPreferences.getBoolean(KEY_SESSION, false)
}
