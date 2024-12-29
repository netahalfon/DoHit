package com.example.dohit.data

import java.util.Locale

enum class TaskCategory(val displayName: String, val displayNameHebrew: String) {
    Hobbies("Hobbies", "תחביבים"),
    Education("Education", "חינוך"),
    Work("Work", "עבודה"),
    Sport("Sport", "ספורט"),
    Urgent("Urgent", "דחוף"),
    Money("Money", "כסף");

    fun getLocalizedDisplayName(locale: String = Locale.getDefault().language): String {
        return if (locale == "he") displayNameHebrew else displayName
    }
}
