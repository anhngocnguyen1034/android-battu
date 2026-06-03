package com.anhnn.battu.presentation.util

/**
 * Hán-Việt transliteration for Bazi terms.
 *
 * Bazi vocabulary (Heavenly Stems, Earthly Branches, Ten Gods, Nayin, twelve
 * life stages, Shensha…) is composed of Sino-Vietnamese morphemes, so a
 * character-by-character map reproduces the conventional Vietnamese reading
 * for almost every term. A small [overrides] table handles the few compounds
 * where a character's reading differs from its standalone branch reading
 * (e.g. 辰 = "Thìn" as a branch but "Thần" in 孤辰).
 *
 * Display-only — the canonical Chinese data is never mutated.
 */
object BaziVi {

    private val charMap: Map<Char, String> = buildMap {
        // 天干 — Heavenly Stems
        put('甲', "Giáp"); put('乙', "Ất"); put('丙', "Bính"); put('丁', "Đinh")
        put('戊', "Mậu"); put('己', "Kỷ"); put('庚', "Canh"); put('辛', "Tân")
        put('壬', "Nhâm"); put('癸', "Quý")

        // 地支 — Earthly Branches
        put('子', "Tý"); put('丑', "Sửu"); put('寅', "Dần"); put('卯', "Mão")
        put('辰', "Thìn"); put('巳', "Tỵ"); put('午', "Ngọ"); put('未', "Mùi")
        put('申', "Thân"); put('酉', "Dậu"); put('戌', "Tuất"); put('亥', "Hợi")

        // 五行 — Five Elements
        put('金', "Kim"); put('木', "Mộc"); put('水', "Thủy"); put('火', "Hỏa"); put('土', "Thổ")

        // 十神 — Ten Gods
        put('比', "Tỷ"); put('肩', "Kiên"); put('劫', "Kiếp"); put('财', "Tài")
        put('食', "Thực"); put('神', "Thần"); put('伤', "Thương"); put('官', "Quan")
        put('偏', "Thiên"); put('正', "Chính"); put('七', "Thất"); put('杀', "Sát")
        put('印', "Ấn"); put('日', "Nhật"); put('主', "Chủ"); put('元', "Nguyên")

        // 十二长生 — Twelve life stages
        put('长', "Trường"); put('生', "Sinh"); put('沐', "Mộc"); put('浴', "Dục")
        put('冠', "Quan"); put('带', "Đới"); put('临', "Lâm"); put('帝', "Đế")
        put('旺', "Vượng"); put('衰', "Suy"); put('病', "Bệnh"); put('死', "Tử")
        put('墓', "Mộ"); put('绝', "Tuyệt"); put('胎', "Thai"); put('养', "Dưỡng")

        // 纳音 — Nayin syllables
        put('路', "Lộ"); put('旁', "Bàng"); put('白', "Bạch"); put('蜡', "Lạp")
        put('杨', "Dương"); put('柳', "Liễu"); put('海', "Hải"); put('中', "Trung")
        put('炉', "Lô"); put('大', "Đại"); put('林', "Lâm"); put('剑', "Kiếm")
        put('锋', "Phong"); put('山', "Sơn"); put('头', "Đầu"); put('涧', "Giản")
        put('下', "Hạ"); put('城', "Thành"); put('泉', "Tuyền"); put('屋', "Ốc")
        put('上', "Thượng"); put('霹', "Tích"); put('雳', "Lịch"); put('松', "Tùng")
        put('柏', "Bách"); put('流', "Lưu"); put('沙', "Sa"); put('平', "Bình")
        put('地', "Địa"); put('壁', "Bích"); put('箔', "Bạc"); put('覆', "Phúc")
        put('灯', "Đăng"); put('天', "Thiên"); put('河', "Hà"); put('驿', "Dịch")
        put('钗', "Thoa"); put('钏', "Xuyến"); put('桑', "Tang"); put('柘', "Đố")
        put('溪', "Khê"); put('石', "Thạch"); put('榴', "Lựu")

        // 格局 — Pattern terms
        put('格', "Cách"); put('身', "Thân"); put('弱', "Nhược"); put('强', "Cường")
        put('从', "Tòng"); put('化', "Hóa"); put('真', "Chân"); put('假', "Giả")
        put('局', "Cục"); put('令', "Lệnh"); put('透', "Thấu"); put('干', "Can")
        put('位', "Vị"); put('月', "Nguyệt")

        // 刑冲合害 — Inter-branch relations
        put('冲', "Xung"); put('合', "Hợp"); put('刑', "Hình"); put('害', "Hại")
        put('破', "Phá"); put('三', "Tam"); put('会', "Hội"); put('六', "Lục")
        put('半', "Bán"); put('年', "Niên"); put('时', "Thì")

        // 神煞 — Symbolic stars (common)
        put('将', "Tướng"); put('星', "Tinh"); put('华', "Hoa"); put('盖', "Cái")
        put('贵', "Quý"); put('人', "Nhân"); put('桃', "Đào"); put('花', "Hoa")
        put('马', "Mã"); put('文', "Văn"); put('昌', "Xương"); put('羊', "Dương")
        put('刃', "Nhận"); put('禄', "Lộc"); put('煞', "Sát"); put('灾', "Tai")
        put('亡', "Vong"); put('孤', "Cô"); put('寡', "Quả"); put('宿', "Tú")
        put('红', "Hồng"); put('艳', "Diễm"); put('舆', "Dư"); put('国', "Quốc")
        put('德', "Đức"); put('医', "Y"); put('学', "Học"); put('堂', "Đường")
        put('罗', "La"); put('网', "Võng"); put('披', "Phi"); put('麻', "Ma")
        put('咸', "Hàm"); put('池', "Trì"); put('童', "Đồng"); put('喜', "Hỷ")
        put('鸾', "Loan"); put('太', "Thái"); put('极', "Cực"); put('福', "Phúc")
        put('厨', "Trù"); put('空', "Không"); put('阳', "Dương"); put('阴', "Âm")
        put('刚', "Cương"); put('禄', "Lộc")
    }

    /** Whole-token overrides where char-by-char reading is wrong/awkward. */
    private val overrides: Map<String, String> = mapOf(
        "孤辰" to "Cô Thần",
        "元辰" to "Nguyên Thần",
        "童子" to "Đồng Tử",
    )

    private val stemElementChar: Map<Char, Char> = buildMap {
        listOf('甲', '乙').forEach { put(it, '木') }
        listOf('丙', '丁').forEach { put(it, '火') }
        listOf('戊', '己').forEach { put(it, '土') }
        listOf('庚', '辛').forEach { put(it, '金') }
        listOf('壬', '癸').forEach { put(it, '水') }
    }

    /** Hán-Việt reading of a single glyph (e.g. '庚' → "Canh"). */
    fun char(c: Char?): String = c?.let { charMap[it] ?: it.toString() } ?: ""

    /** Vietnamese element name of a stem (e.g. '庚' → "Kim"). */
    fun stemElement(c: Char?): String = char(stemElementChar[c])

    /** Translate a single term, honouring [overrides] then falling back to chars. */
    fun term(token: String): String {
        val t = token.trim()
        if (t.isEmpty()) return ""
        return overrides[t] ?: translate(t)
    }

    /** Translate a whitespace-separated list of terms, joined with " · ". */
    fun tokens(text: String): String =
        text.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
            .joinToString(" · ") { term(it) }

    /**
     * Transliterate free-form text character-by-character, leaving Latin /
     * digits / punctuation intact and normalising full-width brackets.
     */
    fun translate(text: String): String {
        val sb = StringBuilder()
        var prevReading = false
        for (c in text) {
            when (c) {
                '（', '(' -> {
                    if (prevReading) sb.append(' ')
                    sb.append('(')
                    prevReading = false
                }
                '）', ')' -> { sb.append(')'); prevReading = false }
                else -> {
                    val r = charMap[c]
                    if (r != null) {
                        if (prevReading) {
                            sb.append(' ')
                        } else if (sb.isNotEmpty() && sb.last() != '(' && sb.last() != ' ') {
                            sb.append(' ')
                        }
                        sb.append(r)
                        prevReading = true
                    } else {
                        sb.append(c)
                        prevReading = false
                    }
                }
            }
        }
        return sb.toString().trim()
    }

    /** Localised gender label from the backend's Chinese gender string. */
    fun gender(raw: String): String = when {
        raw.contains('乾') -> "Nam (Càn tạo)"
        raw.contains('坤') -> "Nữ (Khôn tạo)"
        else -> translate(raw)
    }
}
