package com.anhnn.battu.presentation.util

import androidx.compose.runtime.Immutable

/**
 * Nội dung luận giải TĨNH (offline) cho từng Thiên Can và Địa Chi.
 *
 * Đây là kiến thức Tứ Trụ phổ thông dùng để hiển thị khi người dùng bấm vào
 * một ô Can/Chi trên bảng lá số. Nội dung mang tính tham khảo, không thay thế
 * phần luận giải động (geju / wuxing context) từ backend hay AI Chat sau này.
 *
 * Tên Hán-Việt và tên Ngũ Hành lấy lại từ [BaziVi] để tránh trùng lặp dữ liệu;
 * file này chỉ bổ sung phần âm/dương, hình tượng và mô tả tính cách.
 */
object BaziMeaning {

    /** Một mục giải nghĩa cho một glyph Can/Chi. */
    @Immutable
    data class CellMeaning(
        val glyph: Char,
        /** Hán-Việt, vd "Giáp". */
        val viName: String,
        /** Tên Ngũ Hành tiếng Việt, vd "Mộc". */
        val element: String,
        /** "Dương" hoặc "Âm". */
        val yinYang: String,
        /** Con giáp (chỉ Địa Chi), null với Thiên Can. */
        val zodiac: String?,
        /** Khung giờ (chỉ Địa Chi), vd "23h–1h"; null với Thiên Can. */
        val hour: String?,
        /** Hình tượng ngắn gọn, vd "Cây đại thụ". */
        val image: String,
        /** Mô tả tính cách / ý nghĩa (1–3 câu). */
        val detail: String,
    )

    private val YANG = "Dương"
    private val YIN = "Âm"

    private val stems: Map<Char, CellMeaning> = mapOf(
        '甲' to stem('甲', YANG, "Cây đại thụ",
            "Chính trực, có chí tiến thủ và tố chất lãnh đạo, luôn vươn lên như cây cổ thụ. Mặt trái là đôi khi cứng nhắc, khó uốn nắn."),
        '乙' to stem('乙', YIN, "Hoa cỏ, dây leo",
            "Mềm mại, uyển chuyển, thích nghi và khéo léo trong ứng xử. Cần lưu ý xu hướng thiếu quyết đoán, hay dựa dẫm."),
        '丙' to stem('丙', YANG, "Mặt trời",
            "Nhiệt tình, hào phóng, tỏa sáng và truyền cảm hứng cho người xung quanh. Dễ nóng vội và đôi khi phô trương."),
        '丁' to stem('丁', YIN, "Ngọn đèn, ánh nến",
            "Ấm áp, tinh tế, nội tâm sâu sắc và tận tụy. Nhạy cảm, hay suy nghĩ nhiều và dễ chịu ảnh hưởng cảm xúc."),
        '戊' to stem('戊', YANG, "Núi cao, đất dày",
            "Vững vàng, bao dung, đáng tin cậy và kiên định với mục tiêu. Mặt trái là bảo thủ, chậm thay đổi."),
        '己' to stem('己', YIN, "Đất ruộng vườn",
            "Bao dung, nuôi dưỡng, tỉ mỉ và thực tế. Cần để ý xu hướng đa nghi và ôm đồm quá nhiều việc."),
        '庚' to stem('庚', YANG, "Kim loại thô, đao kiếm",
            "Cương nghị, quyết đoán, trọng nghĩa khí và mạnh mẽ trong hành động. Dễ cứng rắn và bốc đồng."),
        '辛' to stem('辛', YIN, "Châu ngọc, kim khí tinh",
            "Tinh tế, cầu toàn, sang trọng và nhạy bén với cái đẹp. Mặt trái là dễ kiêu và quá để ý tiểu tiết."),
        '壬' to stem('壬', YANG, "Sông biển mênh mông",
            "Bao la, thông minh, linh hoạt và giàu ý tưởng. Cần tiết chế xu hướng phóng túng, thiếu kiên định."),
        '癸' to stem('癸', YIN, "Mưa móc, sương khói",
            "Ôn hòa, kín đáo, trực giác tốt và nhẫn nại. Dễ ưu tư, do dự khi phải lựa chọn."),
    )

    private val branches: Map<Char, CellMeaning> = mapOf(
        '子' to branch('子', YANG, "Chuột", "23h–1h",
            "Thông minh, nhanh nhạy, giỏi thích nghi và nắm bắt cơ hội. Khởi đầu của vòng tuần hoàn, ẩn tàng sức sống."),
        '丑' to branch('丑', YIN, "Trâu", "1h–3h",
            "Cần cù, bền bỉ, đáng tin và giàu sức chịu đựng. Tích lũy âm thầm, làm việc chắc chắn."),
        '寅' to branch('寅', YANG, "Hổ", "3h–5h",
            "Dũng cảm, có uy lực và tinh thần tiên phong. Khí dương sinh sôi, thích dẫn đầu và hành động."),
        '卯' to branch('卯', YIN, "Mèo", "5h–7h",
            "Ôn nhu, yêu cái đẹp, nhạy cảm và có thiên hướng nghệ thuật. Mềm mỏng nhưng dẻo dai."),
        '辰' to branch('辰', YANG, "Rồng", "7h–9h",
            "Hoài bão lớn, có uy lực và khả năng biến hóa. Kho chứa của nhiều ngũ hành, đa tài đa năng."),
        '巳' to branch('巳', YIN, "Rắn", "9h–11h",
            "Trí tuệ, sâu sắc, có chiều sâu nội tâm và đôi phần huyền bí. Quan sát kỹ trước khi hành động."),
        '午' to branch('午', YANG, "Ngựa", "11h–13h",
            "Nhiệt huyết, năng động, yêu tự do và giàu sức sống. Khí dương cực thịnh, lan tỏa và sôi nổi."),
        '未' to branch('未', YIN, "Dê", "13h–15h",
            "Hiền hòa, giàu tình cảm và có khiếu nghệ thuật. Trọng hòa khí, biết nhường nhịn."),
        '申' to branch('申', YANG, "Khỉ", "15h–17h",
            "Lanh lợi, đa tài, linh hoạt và giỏi xoay xở. Ưa khám phá, đầu óc nhạy bén."),
        '酉' to branch('酉', YIN, "Gà", "17h–19h",
            "Cẩn thận, cầu toàn, sắc sảo và có nguyên tắc. Chú trọng chi tiết, kỷ luật rõ ràng."),
        '戌' to branch('戌', YANG, "Chó", "19h–21h",
            "Trung thành, chính trực và trọng nghĩa khí. Đáng tin, bảo vệ người thân và lẽ phải."),
        '亥' to branch('亥', YIN, "Heo", "21h–23h",
            "Hiền lành, bao dung, phúc hậu và an nhiên. Khép lại vòng tuần hoàn, tàng chứa sinh cơ."),
    )

    private fun stem(c: Char, yinYang: String, image: String, detail: String) =
        CellMeaning(
            glyph = c,
            viName = BaziVi.char(c),
            element = BaziVi.stemElement(c),
            yinYang = yinYang,
            zodiac = null,
            hour = null,
            image = image,
            detail = detail,
        )

    private fun branch(c: Char, yinYang: String, zodiac: String, hour: String, detail: String) =
        CellMeaning(
            glyph = c,
            viName = BaziVi.char(c),
            element = branchElement(c),
            yinYang = yinYang,
            zodiac = zodiac,
            hour = hour,
            image = "$zodiac · $hour",
            detail = detail,
        )

    private val branchElementChar: Map<Char, Char> = buildMap {
        listOf('寅', '卯').forEach { put(it, '木') }
        listOf('巳', '午').forEach { put(it, '火') }
        listOf('辰', '戌', '丑', '未').forEach { put(it, '土') }
        listOf('申', '酉').forEach { put(it, '金') }
        listOf('亥', '子').forEach { put(it, '水') }
    }

    private fun branchElement(c: Char): String = BaziVi.char(branchElementChar[c])

    /** Tra giải nghĩa cho một glyph Can hoặc Chi; null nếu không nhận diện được. */
    fun of(glyph: Char?): CellMeaning? =
        glyph?.let { stems[it] ?: branches[it] }
}
