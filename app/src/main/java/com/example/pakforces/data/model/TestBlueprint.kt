package com.example.pakforces.data.model

/**
 * Real-world test patterns based on AS&RC, PAF I&S Centre, and NRSC initial
 * screening tests. These figures reflect the actual number of questions and
 * time per section, so Simulation Mode mimics the real exam pressure.
 */
data class TestSection(
    val category: Category,
    val questionCount: Int,
    val timeMinutes: Int,
    val description: String
)

data class TestBlueprint(
    val force: Force,
    val displayName: String,
    val sections: List<TestSection>
)

object TestBlueprints {

    val ARMY_INITIAL = TestBlueprint(
        force = Force.ARMY,
        displayName = "Army Initial Test (AS&RC)",
        sections = listOf(
            TestSection(Category.VERBAL, 85, 30, "Verbal Intelligence"),
            TestSection(Category.NON_VERBAL, 85, 30, "Non-Verbal Intelligence"),
            TestSection(Category.ENGLISH, 35, 15, "English"),
            TestSection(Category.MATH, 25, 15, "Mathematics"),
            TestSection(Category.PAKISTAN_STUDIES, 20, 10, "Pakistan Studies"),
            TestSection(Category.ISLAMIC_STUDIES, 20, 10, "Islamic Studies"),
            TestSection(Category.GENERAL_KNOWLEDGE, 20, 10, "General Knowledge"),
        )
    )

    val AIR_FORCE_INITIAL = TestBlueprint(
        force = Force.AIR_FORCE,
        displayName = "PAF Initial Test (I&S Centre)",
        sections = listOf(
            TestSection(Category.VERBAL, 70, 25, "Verbal Intelligence"),
            TestSection(Category.NON_VERBAL, 30, 15, "Non-Verbal Intelligence"),
            TestSection(Category.ENGLISH, 40, 20, "English"),
            TestSection(Category.EVERYDAY_SCIENCE, 35, 20, "Physics / Science"),
        )
    )

    val NAVY_INITIAL = TestBlueprint(
        force = Force.NAVY,
        displayName = "Navy Initial Test (NRSC)",
        sections = listOf(
            TestSection(Category.VERBAL, 60, 20, "Verbal Intelligence"),
            TestSection(Category.NON_VERBAL, 40, 15, "Non-Verbal Intelligence"),
            TestSection(Category.ENGLISH, 30, 15, "English"),
            TestSection(Category.MATH, 30, 15, "Mathematics"),
            TestSection(Category.GENERAL_KNOWLEDGE, 30, 10, "General Knowledge"),
        )
    )

    fun forForce(force: Force): TestBlueprint = when (force) {
        Force.ARMY -> ARMY_INITIAL
        Force.AIR_FORCE -> AIR_FORCE_INITIAL
        Force.NAVY -> NAVY_INITIAL
    }
}

/**
 * Practice mode lengths — separate from simulation mode.
 * Practice lets the user pick how many questions per session.
 */
object PracticeLengths {
    const val QUICK = 10
    const val STANDARD = 25
    const val FULL = 50
    val ALL = listOf(QUICK to "Quick (10 Q)", STANDARD to "Standard (25 Q)", FULL to "Full (50 Q)")
}
