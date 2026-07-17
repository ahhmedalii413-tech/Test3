package com.example.pakforces.data.model

/**
 * Learning material organized by category — short tricks, tips, and worked examples
 * to teach the user how to solve each question type.
 *
 * Stored as in-app data (no external file needed) so the user can read offline.
 */
data class Lesson(
    val id: String,
    val category: Category,
    val title: String,
    val summary: String,
    val steps: List<String>,
    val example: String,
    val tip: String
)

object LearningContent {

    val lessons: List<Lesson> = listOf(
        Lesson(
            id = "l_verbal_1",
            category = Category.VERBAL,
            title = "Series (Number) — Trick",
            summary = "Number series ko solve karne ka 4-step method.",
            steps = listOf(
                "Pehle differences check karein (har term ke beech ka gap).",
                "Agar differences constant hain → arithmetic series (a + nd).",
                "Agar differences barh rahe hain → quadratic ya cubic series.",
                "Agar ratio constant hai → geometric series (× ya ÷).",
                "Square/cube patterns bhi check karein (1,4,9,16 ya 1,8,27,64)."
            ),
            example = "2, 6, 12, 20, 30, ? → Differences: 4, 6, 8, 10 → next diff 12 → answer 30+12 = 42",
            tip = "Agar simple pattern nahi mil raha, pehle 5 terms ka difference nikal lijiye — 80% series isi se solve ho jati hain."
        ),
        Lesson(
            id = "l_verbal_2",
            category = Category.VERBAL,
            title = "Letter Series — Trick",
            summary = "Alphabet series solve karne ka shortcut.",
            steps = listOf(
                "Har letter ko number assign karein (A=1, B=2, ..., Z=26).",
                "Consecutive letters ke beech ka difference nikalein.",
                "Agar difference same hai → simple skip pattern.",
                "Agar difference barh raha hai → accelerate pattern (1,2,3,4...).",
                "Reverse direction (Z, X, V, T) bhi check karein."
            ),
            example = "A, D, I, P, ? → A(1), D(4), I(9), P(16) → Differences: 3, 5, 7 → next diff 9 → 16+9 = 25 = Y",
            tip = "Letter ko number mein convert karte hi pattern clear ho jata hai. Practice se speed aati hai."
        ),
        Lesson(
            id = "l_verbal_3",
            category = Category.VERBAL,
            title = "Coding-Decoding — Trick",
            summary = "Coding questions mein pattern pehchane ka method.",
            steps = listOf(
                "Letter positions likhein (A=1 se Z=26).",
                "Original aur coded letters ke beech ka difference check karein.",
                "Common patterns: +1, -1, +2, -2, reverse, ya position-sum.",
                "Number coding ke liye letters ke position numbers jor kar dekhein.",
                "Reverse coding mein coded ko wapas original mein convert karein."
            ),
            example = "ROAD → URDG: R(18)+3=U, O(15)+3=R, A(1)+3=D, D(4)+3=G. Pattern: har letter +3.",
            tip = "Agar +3 pattern hai to answer wahi pattern follow karega. Always verify with one extra letter."
        ),
        Lesson(
            id = "l_verbal_4",
            category = Category.VERBAL,
            title = "Blood Relations — Trick",
            summary = "Blood relation puzzles ko solve karne ka tree-method.",
            steps = listOf(
                "Question ko dhyan se padhein aur relation chain likhein.",
                "Tree diagram banayein (boxes mein persons aur arrows mein relations).",
                "'My grandfather's only son' = mera father (most cases).",
                "'Only daughter' usually means speaker khud (agar female).",
                "Gender check karein — 'sister/brother' batata hai male/female."
            ),
            example = "'His mother is the only daughter of my mother' → woman's mother's only daughter = woman herself → woman is his mother",
            tip = "Tree banane se 90% confusion door ho jata hai. Pen-paper use karein."
        ),
        Lesson(
            id = "l_verbal_5",
            category = Category.VERBAL,
            title = "Direction Sense — Trick",
            summary = "Direction questions ke liye coordinate method.",
            steps = listOf(
                "Coordinate system imagine karein: N=+y, S=−y, E=+x, W=−x.",
                "Har move ke baad net displacement calculate karein.",
                "Final distance = √(x² + y²) (Pythagoras).",
                "Right turn = 90° clockwise, Left turn = 90° anti-clockwise.",
                "180° turn = opposite direction."
            ),
            example = "3 km N, 4 km E → distance = √(3² + 4²) = √25 = 5 km",
            tip = "Pythagoras triads yaad rakhein: 3-4-5, 5-12-13, 8-15-17, 7-24-25."
        ),
        Lesson(
            id = "l_verbal_6",
            category = Category.VERBAL,
            title = "Analogy — Trick",
            summary = "Analogy questions mein relationship pehchanne ka method.",
            steps = listOf(
                "Pehle dono words ke relationship ko sentence mein likhein.",
                "Wahi sentence dusre pair par lagayein.",
                "Common relations: tool-worker, cause-effect, part-whole, synonym-antonym.",
                "Agar answer clear nahi, elimination use karein.",
                "Hamesha strongest relationship wala option chunein."
            ),
            example = "Doctor : Hospital :: Teacher : School → 'X works at Y'",
            tip = "Relationship ko ek sentence mein likhna sabse important step hai."
        ),
        Lesson(
            id = "l_nv_1",
            category = Category.NON_VERBAL,
            title = "Figure Series — Trick",
            summary = "Figure series mein pattern pehchane ka method.",
            steps = listOf(
                "Pehle shape ki type note karein (circle, square, triangle).",
                "Phir fill note karein (solid, hollow, half).",
                "Rotation check karein (0°, 45°, 90°, 180°...).",
                "Position ya count changes bhi check karein (dots barh rahe? ).",
                "Pattern ko identify karke next term predict karein."
            ),
            example = "●, ○, ●, ○, ? → Alternating solid/hollow → next hollow ○",
            tip = "4-5 attributes separately check karein (shape, fill, rotation, position, count). Pattern usually 2 attributes mein hota hai."
        ),
        Lesson(
            id = "l_nv_2",
            category = Category.NON_VERBAL,
            title = "Mirror Image — Trick",
            summary = "Mirror images banane ka shortcut.",
            steps = listOf(
                "Mirror left-right flip karta hai (top-bottom same rehta hai).",
                "Letters jinke mirror same hain: A, H, I, M, O, T, U, V, W, X, Y.",
                "Numbers: 0 aur 8 mirror same. 6 mirror 9 ban jata hai.",
                "Curve direction ult ho jati hai (R-half curve left ban jata hai).",
                "Practice se dimagh mirror image banana seekh jata hai."
            ),
            example = "'b' ka mirror image 'd' jaisa dikhta hai (bulge ult side).",
            tip = "Mirror = left-right flip. Water image = top-bottom flip. Difference yaad rakhein."
        ),
        Lesson(
            id = "l_nv_3",
            category = Category.NON_VERBAL,
            title = "Rotation — Trick",
            summary = "Rotation questions solve karne ka shortcut.",
            steps = listOf(
                "Angle note karein (90°, 180°, 270°).",
                "Clock-wise vs anti-clock-wise identify karein.",
                "Symmetric shapes (Z, N, S) 180° par same rehte hain.",
                "360° ke baad original par wapas aa jata hai.",
                "Har step ka rotation angle constant hota hai."
            ),
            example = "L → 90° CW → backward Γ. 4 rotations (4×90°=360°) par wapas L.",
            tip = "Symmetric shapes (Z, N, X, O, H, I) 180° par wahi rehte hain — yeh ek common trick hai."
        ),
        Lesson(
            id = "l_e_1",
            category = Category.ENGLISH,
            title = "Synonyms/Antonyms — Trick",
            summary = "Vocabulary questions improve karne ka method.",
            steps = listOf(
                "Prefix aur suffix se meaning guess karein (un- = not, re- = again).",
                "Root word yaad rakhein (chron = time, bio = life, graph = write).",
                "Daily 10 naye words likhein aur unke synonyms/antonyms.",
                "Word family banayein (happy → joyful, glad, cheerful).",
                "Context se meaning guess karein agar word nahi pata."
            ),
            example = "Benign = kind (synonyms: gentle, mild; antonyms: cruel, harsh)",
            tip = "Roz 5 words likhein. Ek mahine mein 150 naye words — huge improvement."
        ),
        Lesson(
            id = "l_m_1",
            category = Category.MATH,
            title = "Percentage Shortcuts",
            summary = "Percentage questions solve karne ke shortcuts.",
            steps = listOf(
                "X% of Y = (X/100) × Y.",
                "Increase/decrease % = (Difference / Original) × 100.",
                "Successive %: A% + B% net = A + B + (A×B/100).",
                "Fraction to % yaad rakhein: 1/2=50%, 1/4=25%, 1/5=20%, 1/8=12.5%.",
                "Decimals bhi yaad rakhein: 0.5=50%, 0.25=25%."
            ),
            example = "25% of 200 = 25/100 × 200 = 50",
            tip = "Fraction-decimal-percent table yaad rakhein — bohat helpful hai."
        ),
        Lesson(
            id = "l_pk_1",
            category = Category.PAKISTAN_STUDIES,
            title = "Important Dates — Memory Trick",
            summary = "Pakistan history ke important dates yaad rakhne ka method.",
            steps = listOf(
                "Timeline banayein — ek line par saal likhein.",
                "Categories mein divide karein: Pre-1947, 1947-71, Post-1971.",
                "Important saal yaad rakhein: 1906, 1930, 1940, 1947, 1956, 1962, 1971, 1973.",
                "Co-relation banayein (1940 Resolution → 1947 Pakistan).",
                "Story method se dates ko narrative mein jor dein."
            ),
            example = "1906 (Muslim League) → 1930 (Allahabad Address) → 1940 (Lahore Resolution) → 1947 (Pakistan)",
            tip = "Story banayein — 1906 mein party bani, 1930 mein idea aya, 1940 mein demand hua, 1947 mein mil gaya."
        ),
        Lesson(
            id = "l_is_1",
            category = Category.ISLAMIC_STUDIES,
            title = "Ghazwaat — Memory Trick",
            summary = "Battles of Islam yaad rakhne ka easy method.",
            steps = listOf(
                "Order yaad rakhein: Badr → Uhud → Khandaq → Hunain → Makkah → Tabuk.",
                "Years: Badr 2 AH, Uhud 3 AH, Khandaq 5 AH, Makkah 8 AH, Tabuk 9 AH.",
                "Locations yaad rakhein (Badr = wells of Badr, Uhud = mountain).",
                "Opponents yaad rakhein (Badr = Quraish, Khaybar = Jews).",
                "Key lessons/sunnah har battle se connect karein."
            ),
            example = "Badr (2 AH) — 313 Muslims vs 1000 Quraish → Victory for Muslims.",
            tip = "Sequence + year + opponent = complete memory peg."
        ),
        Lesson(
            id = "l_gk_1",
            category = Category.GENERAL_KNOWLEDGE,
            title = "Current Affairs — Daily Update",
            summary = "Current affairs strong karne ka method.",
            steps = listOf(
                "Daily 15 min news par spend karein.",
                "Pakistan leaders yaad rakhein (PM, President, COAS, CAS, CNS, CJP).",
                "International organizations ke heads yaad rakhein (UN, OIC, IMF).",
                "Recent sports events (World Cup, Olympics, etc.).",
                "National days aur treaties yaad rakhein."
            ),
            example = "PM = Mian Muhammad Shehbaz Sharif (March 2024 se), President = Asif Ali Zardari (March 2024 se).",
            tip = "Test ke 1 hafte pehle current affairs zyaada revise karein."
        ),
        Lesson(
            id = "l_sc_1",
            category = Category.EVERYDAY_SCIENCE,
            title = "Units & Formulas — Memory Trick",
            summary = "Physics units aur formulas yaad rakhne ka method.",
            steps = listOf(
                "Force = mass × acceleration (F = ma), unit = Newton.",
                "Work = force × distance, unit = Joule.",
                "Power = work / time, unit = Watt.",
                "Pressure = force / area, unit = Pascal.",
                "Speed = distance / time, unit = m/s."
            ),
            example = "Ohm's law: V = IR (Voltage = Current × Resistance)",
            tip = "Formula + unit + symbol yaad rakhein — teeno ek saath."
        ),
    )

    fun forCategory(c: Category): List<Lesson> = lessons.filter { it.category == c }
}
