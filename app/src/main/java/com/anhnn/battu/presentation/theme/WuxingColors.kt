package com.anhnn.battu.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * Wuxing (Five Elements) color palette, ported from the FOR-BAZI web frontend
 * (frontend/src/lib/wuxing-colors.ts) so the Android chart matches the web look.
 *
 *   Wood (木) = Jade Green · Fire (火) = Crimson · Earth (土) = Gold
 *   Metal (金) = Silver    · Water (水) = Azure Blue
 */
object WuxingColors {

    val Wood = Color(0xFF50C878)
    val Fire = Color(0xFFE94560)
    val Earth = Color(0xFFD4AF37)
    val Metal = Color(0xFFC0C0C0)
    val Water = Color(0xFF4A90D9)

    /** Signature gold accent used for labels / headings on the web. */
    val Gold = Color(0xFFD4AF37)

    private val charColors: Map<Char, Color> = buildMap {
        // Wood (木)
        listOf('甲', '乙', '寅', '卯').forEach { put(it, Wood) }
        // Fire (火)
        listOf('丙', '丁', '巳', '午').forEach { put(it, Fire) }
        // Earth (土)
        listOf('戊', '己', '辰', '戌', '丑', '未').forEach { put(it, Earth) }
        // Metal (金)
        listOf('庚', '辛', '申', '酉').forEach { put(it, Metal) }
        // Water (水)
        listOf('壬', '癸', '亥', '子').forEach { put(it, Water) }
    }

    private val elementColors: Map<String, Color> = mapOf(
        "金" to Metal,
        "木" to Wood,
        "水" to Water,
        "火" to Fire,
        "土" to Earth,
    )

    /** Color for a single stem/branch character (falls back to [fallback]). */
    fun charColor(char: Char, fallback: Color): Color = charColors[char] ?: fallback

    /** Color for a wuxing element name 金/木/水/火/土 (falls back to [fallback]). */
    fun elementColor(element: String, fallback: Color): Color =
        elementColors[element] ?: fallback
}
