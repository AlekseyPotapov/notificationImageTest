package ru.taptap.testglide

import android.content.Context
import android.preference.PreferenceManager

class PreferencesSource(context: Context) {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var imageUri by publicProperty("", "pref_image_uri")

    private fun <T> publicProperty(default: T, key: String) =
        PreferenceProperty(default, key, sharedPreferences)
}
