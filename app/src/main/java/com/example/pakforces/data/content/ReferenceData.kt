package com.example.pakforces.data.content

/**
 * Curated reference content shown in dedicated quick-reference screens.
 * All explanations in Roman Urdu.
 */

object MathFormulas {
    data class Formula(val topic: String, val name: String, val formula: String, val note: String)

    val all: List<Formula> = listOf(
        Formula("Arithmetic", "Percentage", "X% of Y = (X/100) × Y", "10% of 200 = 20"),
        Formula("Arithmetic", "Percentage Change", "(New − Old) / Old × 100", "200→250 = +25%"),
        Formula("Arithmetic", "Profit %", "(Profit / Cost) × 100", "Buy 100, sell 125 → 25% profit"),
        Formula("Arithmetic", "Loss %", "(Loss / Cost) × 100", "Buy 100, sell 80 → 20% loss"),
        Formula("Arithmetic", "Simple Interest", "SI = (P × R × T) / 100", "P=principal, R=rate, T=time (years)"),
        Formula("Arithmetic", "Compound Interest", "CI = P(1+R/100)ⁿ − P", "n = number of periods"),
        Formula("Arithmetic", "Average", "Sum of values / count", "Avg of 2,4,6 = 12/3 = 4"),
        Formula("Arithmetic", "Ratio a:b", "a/b", "Dont reduce to lowest terms first"),
        Formula("Arithmetic", "Speed", "Speed = Distance / Time", "60 km in 1 hr = 60 km/h"),
        Formula("Arithmetic", "LCM × HCF", "= Product of two numbers", "LCM(4,6)×HCF(4,6) = 12×2 = 24 = 4×6"),
        Formula("Algebra", "(a+b)²", "a² + 2ab + b²", "Important identity"),
        Formula("Algebra", "(a−b)²", "a² − 2ab + b²", "Important identity"),
        Formula("Algebra", "a² − b²", "(a+b)(a−b)", "Difference of squares"),
        Formula("Algebra", "(a+b)³", "a³ + 3a²b + 3ab² + b³", "Cube expansion"),
        Formula("Algebra", "Quadratic Root", "x = (−b ± √(b²−4ac)) / 2a", "For ax² + bx + c = 0"),
        Formula("Geometry", "Pythagoras", "a² + b² = c²", "Right triangle; c = hypotenuse"),
        Formula("Geometry", "Area of Triangle", "½ × base × height", "Standard triangle"),
        Formula("Geometry", "Area of Square", "side²", "Side × side"),
        Formula("Geometry", "Area of Rectangle", "length × breadth", "l × b"),
        Formula("Geometry", "Area of Circle", "πr²", "r = radius"),
        Formula("Geometry", "Circumference of Circle", "2πr", "r = radius"),
        Formula("Geometry", "Perimeter of Square", "4 × side", ""),
        Formula("Geometry", "Perimeter of Rectangle", "2 × (l + b)", ""),
        Formula("Geometry", "Sum of triangle angles", "180°", "All triangles"),
        Formula("Geometry", "Sum of quad angles", "360°", "All quadrilaterals"),
        Formula("Geometry", "Volume of Cube", "a³", "side³"),
        Formula("Geometry", "Volume of Cuboid", "l × b × h", ""),
        Formula("Geometry", "Volume of Cylinder", "πr²h", "r = radius, h = height"),
        Formula("Geometry", "Volume of Sphere", "(4/3)πr³", ""),
        Formula("Geometry", "Surface area of Sphere", "4πr²", ""),
        Formula("Trigonometry", "sin θ", "opposite / hypotenuse", "SOH"),
        Formula("Trigonometry", "cos θ", "adjacent / hypotenuse", "CAH"),
        Formula("Trigonometry", "tan θ", "opposite / adjacent", "TOA"),
        Formula("Trigonometry", "sin²θ + cos²θ", "= 1", "Identity"),
    )
}

object IslamicQuickReference {
    data class Entry(val category: String, val title: String, val detail: String)

    val all: List<Entry> = listOf(
        Entry("Pillars", "5 Pillars of Islam", "Shahada (kalima), Salah (namaz), Zakat, Sawm (roza), Hajj"),
        Entry("Pillars", "Number of daily prayers", "5 — Fajr, Zuhr, Asr, Maghrib, Isha"),
        Entry("Pillars", "Zakat rate", "2.5% on savings above Nisab (gold/silver/cash)"),
        Entry("Pillars", "Fard rakats per prayer", "Fajr 2, Zuhr 4, Asr 4, Maghrib 3, Isha 4"),

        Entry("Quran", "Total Surahs", "114"),
        Entry("Quran", "Total verses (approx)", "6,236"),
        Entry("Quran", "First Surah", "Al-Fatihah (7 verses)"),
        Entry("Quran", "Longest Surah", "Al-Baqarah (286 verses)"),
        Entry("Quran", "Shortest Surah", "Al-Kawthar (3 verses)"),
        Entry("Quran", "Revelation period", "~23 years (13 Makki + 10 Madani)"),
        Entry("Quran", "First revelation", "Cave Hira, Surah Al-Alaq verses 1-5"),
        Entry("Quran", "Compiled in book form by", "Hazrat Abu Bakr (RA)"),
        Entry("Quran", "Standardized by", "Hazrat Uthman (RA)"),
        Entry("Quran", "Juz (parah) count", "30"),
        Entry("Quran", "Heart of the Quran", "Surah Yaseen"),

        Entry("Prophets", "Total prophets mentioned", "25"),
        Entry("Prophets", "First Prophet", "Hazrat Adam (AS)"),
        Entry("Prophets", "Last Prophet", "Hazrat Muhammad (PBUH)"),
        Entry("Prophets", "Khalilullah", "Hazrat Ibrahim (AS) — Friend of Allah"),
        Entry("Prophets", "Kalimullah", "Hazrat Musa (AS) — Spoke to Allah"),
        Entry("Prophets", "Ruhullah", "Hazrat Isa (AS) — Spirit of Allah"),
        Entry("Prophets", "Dhul-Nun", "Hazrat Yunus (AS) — swallowed by fish"),
        Entry("Prophets", "Builder of Kaaba", "Hazrat Ibrahim (AS) + Hazrat Ismail (AS)"),
        Entry("Prophets", "Given Zabur (Psalms)", "Hazrat Dawud (AS)"),
        Entry("Prophets", "Given Injil (Gospel)", "Hazrat Isa (AS)"),
        Entry("Prophets", "Given Tawrat (Torah)", "Hazrat Musa (AS)"),
        Entry("Prophets", "Spoke to animals", "Hazrat Sulaiman (AS)"),
        Entry("Prophets", "Built the Ark", "Hazrat Nuh (AS)"),

        Entry("Ghazwaat", "First Ghazwa", "Badr (17 Ramadan 2 AH) — 313 Muslims vs 1000 Quraish"),
        Entry("Ghazwaat", "Battle of Uhud", "3 AH — Hazrat Hamza (RA) martyred"),
        Entry("Ghazwaat", "Battle of Khandaq (Trench)", "5 AH — also called Ahzab"),
        Entry("Ghazwaat", "Treaty of Hudaybiyyah", "6 AH"),
        Entry("Ghazwaat", "Conquest of Makkah", "8 AH, Ramadan"),
        Entry("Ghazwaat", "Battle of Hunain", "8 AH — against Hawazin & Thaqif"),
        Entry("Ghazwaat", "Last Ghazwa", "Tabuk (9 AH)"),
        Entry("Ghazwaat", "Flag-bearer at Badr", "Mus'ab ibn Umair (RA); Ali (RA) at other battles"),

        Entry("Khulafa-e-Rashideen", "First Caliph", "Hazrat Abu Bakr (RA) (632-634 CE)"),
        Entry("Khulafa-e-Rashideen", "Second Caliph", "Hazrat Umar (RA) (634-644 CE)"),
        Entry("Khulafa-e-Rashideen", "Third Caliph", "Hazrat Uthman (RA) (644-656 CE)"),
        Entry("Khulafa-e-Rashideen", "Fourth Caliph", "Hazrat Ali (RA) (656-661 CE)"),

        Entry("Important Numbers", "Number of wives allowed", "4 (with justice)"),
        Entry("Important Numbers", "Days in Ramadan", "29 or 30"),
        Entry("Important Numbers", "Tawaaf circuits", "7"),
        Entry("Important Numbers", "Sa'i (Safa-Marwah)", "7 trips"),
        Entry("Important Numbers", "Jamarat (stoning)", "3 pillars"),
    )
}

object PakistanTimeline {
    data class Event(val year: String, val title: String, val detail: String)

    val all: List<Event> = listOf(
        Event("712 AD", "Muhammad bin Qasim arrives", "Sindh conquered; first Muslim foothold in subcontinent"),
        Event("1526", "Mughal Empire founded", "Babur won First Battle of Panipat"),
        Event("1707", "Death of Aurangzeb", "Decline of Mughal Empire begins"),
        Event("1857", "War of Independence", "Failed mutiny; British Raj begins"),
        Event("1817", "Sir Syed born", "Born 17 Oct 1817 in Delhi"),
        Event("1875", "MAO College founded", "Sir Syed Ahmad Khan established Muhammadan Anglo-Oriental College (later AMU)"),
        Event("1906", "All-India Muslim League founded", "30 December 1906 in Dhaka"),
        Event("1909", "Minto-Morley Reforms", "Separate electorate for Muslims"),
        Event("1916", "Lucknow Pact", "Congress + Muslim League joint demands"),
        Event("1930", "Allahabad Address", "Allama Iqbal proposed separate Muslim state (29 Dec 1930)"),
        Event("1933", "Name 'Pakistan' coined", "Chaudhry Rahmat Ali in 'Now or Never' pamphlet"),
        Event("1940", "Lahore Resolution", "23 March 1940 — Pakistan Resolution passed"),
        Event("1947", "Pakistan independent", "14 August 1947 — Quaid-e-Azam first Governor-General"),
        Event("1948", "Quaid-e-Azam died", "11 September 1948"),
        Event("1949", "Objective Resolution", "12 March 1949 — laid foundation of constitution"),
        Event("1951", "Liaquat Ali Khan assassinated", "16 October 1951 in Rawalpindi"),
        Event("1954", "Anti-Ahmaddiya riots", "Munir Report published"),
        Event("1956", "First Constitution", "Pakistan declared Islamic Republic (23 March 1956)"),
        Event("1958", "First Martial Law", "Ayub Khan imposed (8 October 1958)"),
        Event("1962", "Second Constitution", "Promoted by Ayub Khan"),
        Event("1965", "Indo-Pak War", "17 days war; Tashkent Declaration (10 Jan 1966)"),
        Event("1969", "Yahya Khan takes over", "Second martial law"),
        Event("1970", "First general elections", "Awami League won majority in East Pakistan"),
        Event("1971", "Bangladesh secession", "16 December 1971 — Dhaka fell"),
        Event("1972", "Simla Agreement", "2 July 1972 — Bhutto + Indira Gandhi"),
        Event("1973", "Third Constitution", "Promulgated 14 August 1973 — Bhutto became PM"),
        Event("1974", "Islamic Summit", "Lahore — OIC recognized Pakistan"),
        Event("1977", "Zia-ul-Haq martial law", "5 July 1977 — Bhutto overthrown"),
        Event("1979", "Bhutto executed", "4 April 1979"),
        Event("1979", "Hudood Ordinances", "Zia's Islamization laws"),
        Event("1988", "Benazir Bhutto PM", "First female PM of Pakistan"),
        Event("1990", "Nawaz Sharif PM", "First term"),
        Event("1998", "Nuclear tests", "28 May 1998 — Youm-e-Takbir"),
        Event("1999", "Kargil conflict", "May-July 1999"),
        Event("1999", "Musharraf coup", "12 October 1999 — fourth martial law"),
        Event("2002", "3rd Constitution amendment via LFO", "Musharraf era"),
        Event("2007", "Benazir assassinated", "27 December 2007 in Rawalpindi"),
        Event("2008", "Musharraf resigned", "18 August 2008"),
        Event("2010", "18th Amendment", "Major provincial autonomy; PM Gilani"),
        Event("2013", "First democratic transition", "Nawaz Sharif third term"),
        Event("2018", "Imran Khan PM", "First time"),
        Event("2018", "25th Amendment", "FATA-KPK merger"),
        Event("2022", "No-confidence motion", "Imran Khan removed; Shehbaz Sharif PM"),
        Event("2023", "August — dissolution", "National Assembly dissolved early"),
        Event("2024", "Shehbaz Sharif PM again", "March 2024 — second term"),
        Event("2024", "Asif Zardari President", "March 2024 — second term"),
    )
}

object MedicalStandards {
    data class Standard(val force: String, val category: String, val detail: String)

    val all: List<Standard> = listOf(
        // Army (PMA / SSC / LCC)
        Standard("Army (PMA Long Course)", "Height (male)", "5'4\" (162.5 cm) minimum"),
        Standard("Army (PMA Long Course)", "Height (female)", "5' (152.4 cm) minimum"),
        Standard("Army (PMA Long Course)", "Weight", "As per BMI (18.5 – 25) — age-adjusted"),
        Standard("Army (PMA Long Course)", "Chest", "Male: 31\"-33\" (78-79 cm) — expandable 1.5\""),
        Standard("Army (PMA Long Course)", "Vision", "6/6 with or without glasses; no color blindness"),
        Standard("Army (PMA Long Course)", "Running (1.6 km)", "8 minutes (male), 9.5 min (female)"),
        Standard("Army (PMA Long Course)", "Push-ups", "15 in 2 min (male), 5 (female)"),
        Standard("Army (PMA Long Course)", "Sit-ups", "20 in 2 min"),
        Standard("Army (PMA Long Course)", "Chin-ups", "3 minimum"),
        Standard("Army (PMA Long Course)", "Ditch crossing", "7'4\" × 3' (2.21m × 0.91m)"),

        // PAF
        Standard("PAF (GD Pilot)", "Height (male)", "5'4\" (163 cm) minimum"),
        Standard("PAF (GD Pilot)", "Height (female)", "5'2\" (157 cm)"),
        Standard("PAF (GD Pilot)", "Vision", "6/6 WITHOUT glasses (strict); no color blindness"),
        Standard("PAF (GD Pilot)", "Weight", "Per BMI 18-25; bone structure ratio"),
        Standard("PAF (GD Pilot)", "Hearing", "Normal — whisper test 6m both ears"),
        Standard("PAF (GD Pilot)", "Teeth", "Complete set, no major dental issues"),
        Standard("PAF (Engineering Branch)", "Height", "5'4\" (163 cm) minimum"),
        Standard("PAF (Engineering Branch)", "Vision", "6/6 with glasses allowed"),
        Standard("PAF (Air Defence)", "Height", "5'4\" (163 cm) minimum"),

        // Navy
        Standard("Navy (Operations Branch)", "Height (male)", "5'4\" (163 cm)"),
        Standard("Navy (Operations Branch)", "Vision", "6/6 without glasses (for some branches)"),
        Standard("Navy (Engineering)", "Height", "5'4\" (163 cm)"),
        Standard("Navy (Engineering)", "Vision", "6/6 with glasses permitted"),
        Standard("Navy (Supply)", "Height", "5'4\" (163 cm)"),
        Standard("Navy (Supply)", "Vision", "6/9 with glasses"),

        // Common disqualifications
        Standard("Common Disqualifications", "Flat feet", "Disqualifying for combat roles"),
        Standard("Common Disqualifications", "Knock knees", "Disqualifying"),
        Standard("Common Disqualifications", "Color blindness", "Disqualifying"),
        Standard("Common Disqualifications", "Hernia", "Disqualifying (unless treated 6+ months prior)"),
        Standard("Common Disqualifications", "Asthma", "Disqualifying"),
        Standard("Common Disqualifications", "Epilepsy", "Disqualifying"),
        Standard("Common Disqualifications", "Heart disease", "Disqualifying"),
        Standard("Common Disqualifications", "Hepatitis B/C", "Disqualifying"),
        Standard("Common Disqualifications", "Diabetes", "Disqualifying"),
        Standard("Common Disqualifications", " Tattoo on face/neck", "Disqualifying (covered elsewhere OK)"),
        Standard("Common Disqualifications", "Wearing glasses", "Allowed for non-combat branches only"),
    )
}

object DocumentsChecklist {
    val items: List<String> = listOf(
        "Original CNIC + 3 photocopies",
        "Original Matric Certificate + 3 photocopies (attested)",
        "Original Intermediate Certificate + 3 photocopies (attested)",
        "Bachelor/Master degree (if applicable) + 3 copies",
        "Detailed marks sheets (all certificates)",
        "Domicile certificate (original + 2 copies)",
        "Character certificate from last institution",
        "Recent passport-size photographs (6-8, white background)",
        "Father/Guardian CNIC photocopy",
        "NADRA-verified family registration certificate (FRC)",
        "Proof of residence (utility bill or similar)",
        "Medical fitness certificate (issued by MH/CMH if requested)",
        "No-objection certificate (NOC) — for in-service candidates only",
        "Sport/extra-curricular certificates (for bonus marks in some forces)",
        "Attested copy of B-form (if under 18)",
        "Self-addressed envelopes with stamps (3-5, if requested)",
        "Exam fee receipt (if applicable)",
        "Call letter printout (if received)",
    )
}

object DayInLife {
    data class Entry(val force: String, val title: String, val detail: String)

    val all: List<Entry> = listOf(
        Entry("Army (PMA Kakul)", "Daily Routine", "Fajr prayer, then P.T. at 5:30 AM. Breakfast at 7. Classes 8-12. Lunch 12:30. Afternoon sports/drill 3-5. Dinner 7:30. Lights out 10."),
        Entry("Army (PMA Kakul)", "Training Duration", "2 years for Long Course; 1 year for SSC. Cadets graduate as 2nd Lieutenant."),
        Entry("Army (PMA Kakul)", "Subjects", "Military tactics, weapons, map reading, leadership, physical training, signalling, military law, academics."),
        Entry("Army (PMA Kakul)", "Term System", "4 terms: Junior, Senior, Intermediate, Final. Each ~6 months. Increased difficulty."),
        Entry("Army (PMA Kakul)", "Famous Quote", "'Men at their best — Pakistan Army' — motto of PMA"),

        Entry("PAF (Academy Risalpur)", "Daily Routine", "Fajr, P.T. 5:30. Breakfast 7. Ground training 8-12. Lunch 12:30. Academic classes 2-5. Sports 5-6:30. Dinner 8. Lights out 10:30."),
        Entry("PAF (Academy Risalpur)", "Training Duration", "GD Pilot: 3-4 years (incl. flying phase). Engineering: 3 years. SSC: 6 months."),
        Entry("PAF (Academy Risalpur)", "Flying Phases", "Phase 1: T-37 (basic). Phase 2: T-37 advanced. Phase 3: K-8 or FT-5. Phase 4: F-7PG or Mirage conversion."),
        Entry("PAF (Academy Risalpur)", "Wings Ceremony", "After successful flying phase, cadets earn 'Wings' and commission as Pilot Officers."),
        Entry("PAF (Academy Risalpur)", "Motto", "'Pride of the Nation'"),

        Entry("Navy (PNS Bahadur)", "Daily Routine", "Fajr, P.T. 5:30. Breakfast 7. Classes/sea training 8-12. Lunch. Afternoon practicals/sports. Dinner 8. Lights out 10:30."),
        Entry("Navy (PNS Bahadur)", "Training Duration", "Operations: 2 years. Engineering: 3 years (incl. university degree). SSC: 6 months."),
        Entry("Navy (PNS Bahadur)", "Sea Phase", "Midshipmen spend 6-12 months on ships at sea — practical navigation, gunnery, engineering experience."),
        Entry("Navy (PNS Bahadur)", "Submarine specialization", "Voluntary; only top performers qualify. Extra 1 year training."),
        Entry("Navy (PNS Bahadur)", "Motto", "'A Chain of Honor'"),
    )
}

object DailyQuotes {
    val all: List<String> = listOf(
        "\"Aap apni taqdeer khud banate hain, kismat nahi.\" — Gen. Raheel Sharif (paraphrased)",
        "\"Imandari aur mehnat se behtar koi dosti nahi.\" — Quaid-e-Azam",
        "\"Allah un logon ki madad karta hai jo khud apni madad karte hain.\" — Hazrat Ali (RA)",
        "\"Himmat-e-marda, madad-e-Khuda.\" — Persian proverb",
        "\"Jab tak koshish nahi karoge, kamyab kaise hoge?\"",
        "\"Girtay hain shah sawar hi maidan-e-jung mein. Woh tifl kya girega jo ghutno ke bal chale.\" — Iqbal",
        "\"Mushkilein mujh par padhi, magar main jhuka nahi.\" — Self-help",
        "\"Aaj ki mehnat = kal ki kamyabi.\"",
        "\"Aim high — par aim karne ke baad mehnat bhi karo.\"",
        "\"Patthar bar bar thokar kha kar hi pathar (diamond) banta hai.\"",
        "\"Discipline = freedom.\" — Pakistani military ethos",
        "\"Subah ka bhoola sham ko wapas aaya to usey maaf kar dena.\" — Hazrat Muhammad (PBUH)",
        "\"The only easy day was yesterday.\" — Navy SEAL motto",
        "\"Khudi ko kar buland itna ke har taqdeer se pehle. Khuda bande se khud pooche, bata teri raza kya hai.\" — Iqbal",
        "\"Tum se mohabbat karna asaan tha, tumhe bhoolna mushkil.\" — Just kidding, get back to prep!",
    )
}

object MissionTemplates {
    /**
     * Daily mission templates. Each day, system generates 4 random goals.
     * User earns XP by completing each. Bonus XP for all-4.
     */
    data class Goal(val id: String, val title: String, val xp: Int, val target: Int, val type: String)

    val pool: List<Goal> = listOf(
        Goal("daily_challenge", "Daily Challenge complete karein", 50, 1, "challenge"),
        Goal("test_one", "1 test attempt karein (koi bhi mode)", 30, 1, "test_count"),
        Goal("questions_20", "20 questions solve karein", 40, 20, "questions"),
        Goal("questions_50", "50 questions solve karein", 80, 50, "questions"),
        Goal("correct_15", "15 sahi jawab dein", 35, 15, "correct"),
        Goal("flashcards_10", "10 flashcards review karein", 30, 10, "flashcards"),
        Goal("bookmark_3", "3 sawaal bookmark karein", 20, 3, "bookmark"),
        Goal("learning_2", "2 lessons parhein (Learning Section)", 25, 2, "learning"),
        Goal("practice_25", "25 questions practice mode mein", 45, 25, "practice"),
        Goal("simulation_1", "1 simulation section attempt karein", 60, 1, "simulation"),
        Goal("verbal_15", "15 verbal intelligence questions", 35, 15, "category_verbal"),
        Goal("math_10", "10 math questions", 30, 10, "category_math"),
        Goal("english_15", "15 english questions", 35, 15, "category_english"),
        Goal("gk_10", "10 GK questions", 25, 10, "category_gk"),
    )

    /** Pick 4 random goals for today. */
    fun pickForDay(dayKey: String): List<Goal> {
        val seed = dayKey.hashCode()
        val rand = kotlin.random.Random(seed)
        return pool.shuffled(rand).take(4).sortedBy { it.id }
    }
}
