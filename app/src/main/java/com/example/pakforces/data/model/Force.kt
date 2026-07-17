package com.example.pakforces.data.model

/** The three forces the user can prepare for. */
enum class Force(val key: String, val display: String) {
    ARMY("army", "Pakistan Army"),
    AIR_FORCE("air_force", "Pakistan Air Force"),
    NAVY("navy", "Pakistan Navy");

    companion object {
        fun fromKey(k: String?) = entries.firstOrNull { it.key == k } ?: ARMY
    }
}

/** Top-level test categories. */
enum class Category(val key: String, val display: String) {
    VERBAL("verbal", "Verbal Intelligence"),
    NON_VERBAL("nonverbal", "Non-Verbal Intelligence"),
    ENGLISH("english", "English"),
    MATH("math", "Mathematics"),
    PAKISTAN_STUDIES("pakistan_studies", "Pakistan Studies"),
    ISLAMIC_STUDIES("islamic_studies", "Islamic Studies"),
    GENERAL_KNOWLEDGE("general_knowledge", "General Knowledge"),
    EVERYDAY_SCIENCE("everyday_science", "Everyday Science");

    companion object {
        fun fromKey(k: String?) = entries.firstOrNull { it.key == k } ?: VERBAL
    }
}

/** Test mode — Timed mimics real screening pressure; Practice shows explanations instantly. */
enum class TestMode(val display: String) {
    TIMED("Timed Test"),
    PRACTICE("Practice Mode")
}
