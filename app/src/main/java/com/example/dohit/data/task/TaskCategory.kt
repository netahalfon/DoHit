package com.example.dohit.data.task

import java.util.Locale

enum class TaskCategory(val displayName: String, val displayNameHebrew: String) {
    Education("Education", "חינוך"),
    Hobbies("Hobbies", "תחביבים"),
    Sport("Sport", "ספורט"),
    Work("Work", "עבודה"),
    Money("Money", "כסף"),
    Urgent("Urgent", "דחוף");

    fun getLocalizedDisplayName(locale: String = Locale.getDefault().language): String {
        return if (locale == "iw") displayNameHebrew else displayName
    }

}



