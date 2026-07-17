package com.example.pakforces.data.model

/**
 * Gamified officer rank progression — user starts as Sepoy and ranks up
 * by accumulating XP. Different rank ladder per force for authenticity.
 */
data class OfficerRank(
    val order: Int,
    val title: String,
    val xpRequired: Int,
    val insignia: String  // emoji/text representation
)

object Ranks {
    val ARMY: List<OfficerRank> = listOf(
        OfficerRank(1, "Sepoy", 0, " ›"),
        OfficerRank(2, "Lance Naik", 100, " ›"),
        OfficerRank(3, "Naik", 250, " ‹›"),
        OfficerRank(4, "Havildar", 500, " ‹•›"),
        OfficerRank(5, "2nd Lieutenant", 1000, " ★"),
        OfficerRank(6, "Lieutenant", 1800, " ★★"),
        OfficerRank(7, "Captain", 3000, " ★★★"),
        OfficerRank(8, "Major", 5000, " ★"),
        OfficerRank(9, "Lt. Colonel", 8000, " ★★"),
        OfficerRank(10, "Colonel", 12000, " ★★★"),
        OfficerRank(11, "Brigadier", 18000, " ✦"),
        OfficerRank(12, "Major General", 25000, " ✦✦"),
        OfficerRank(13, "Lieutenant General", 35000, " ✦✦✦"),
        OfficerRank(14, "General", 50000, " ★✦"),
    )

    val AIR_FORCE: List<OfficerRank> = listOf(
        OfficerRank(1, "Airman", 0, " ›"),
        OfficerRank(2, "Leading Aircraftman", 100, " ›"),
        OfficerRank(3, "Corporal", 250, " ‹›"),
        OfficerRank(4, "Sergeant", 500, " ‹•›"),
        OfficerRank(5, "Pilot Officer", 1000, " ★"),
        OfficerRank(6, "Flying Officer", 1800, " ★★"),
        OfficerRank(7, "Flight Lieutenant", 3000, " ★★★"),
        OfficerRank(8, "Squadron Leader", 5000, " ★"),
        OfficerRank(9, "Wing Commander", 8000, " ★★"),
        OfficerRank(10, "Group Captain", 12000, " ★★★"),
        OfficerRank(11, "Air Commodore", 18000, " ✦"),
        OfficerRank(12, "Air Vice Marshal", 25000, " ✦✦"),
        OfficerRank(13, "Air Marshal", 35000, " ✦✦✦"),
        OfficerRank(14, "Air Chief Marshal", 50000, " ★✦"),
    )

    val NAVY: List<OfficerRank> = listOf(
        OfficerRank(1, "Sailor", 0, " ›"),
        OfficerRank(2, "Leading Seaman", 100, " ›"),
        OfficerRank(3, "Petty Officer", 250, " ‹›"),
        OfficerRank(4, "Chief Petty Officer", 500, " ‹•›"),
        OfficerRank(5, "Midshipman", 1000, " ★"),
        OfficerRank(6, "Sub Lieutenant", 1800, " ★★"),
        OfficerRank(7, "Lieutenant", 3000, " ★★★"),
        OfficerRank(8, "Lt. Commander", 5000, " ★"),
        OfficerRank(9, "Commander", 8000, " ★★"),
        OfficerRank(10, "Captain", 12000, " ★★★"),
        OfficerRank(11, "Commodore", 18000, " ✦"),
        OfficerRank(12, "Rear Admiral", 25000, " ✦✦"),
        OfficerRank(13, "Vice Admiral", 35000, " ✦✦✦"),
        OfficerRank(14, "Admiral", 50000, " ★✦"),
    )

    fun forForce(force: Force): List<OfficerRank> = when (force) {
        Force.ARMY -> ARMY
        Force.AIR_FORCE -> AIR_FORCE
        Force.NAVY -> NAVY
    }

    fun current(xp: Int, force: Force): OfficerRank {
        val ranks = forForce(force)
        return ranks.last { xp >= it.xpRequired }
    }

    fun next(xp: Int, force: Force): OfficerRank? {
        val ranks = forForce(force)
        return ranks.firstOrNull { it.xpRequired > xp }
    }

    /** Progress 0..1 toward next rank. */
    fun progress(xp: Int, force: Force): Float {
        val cur = current(xp, force)
        val nxt = next(xp, force) ?: return 1f
        return (xp - cur.xpRequired).toFloat() / (nxt.xpRequired - cur.xpRequired)
    }
}
