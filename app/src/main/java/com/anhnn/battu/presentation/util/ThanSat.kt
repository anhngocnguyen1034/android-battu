package com.anhnn.battu.presentation.util

/**
 * Phân loại Thần Sát thành Cát tinh (lành) / Hung tinh (dữ).
 *
 * Backend trả tên Thần sát bằng chữ Hán (vd "天乙贵人", "羊刃"). Ở đây chỉ giữ một
 * danh sách các "dấu hiệu Hung" phổ biến; mọi tên không khớp được xem là Cát tinh
 * (thiên về trung tính/lành). Đây là phân loại hiển thị mang tính tham khảo.
 */
object ThanSat {

    /** Các token Hán đặc trưng của Hung tinh (so khớp theo chuỗi con). */
    private val hungMarkers = listOf(
        "羊刃", "劫煞", "灾煞", "亡神", "孤辰", "寡宿", "元辰", "披麻",
        "桃花", "咸池", "红艳", "童子", "天罗", "地网", "空亡", "飞刃",
        "血刃", "流霞", "勾绞", "卷舌", "丧门", "吊客", "白虎", "病符",
        "大耗", "阴煞", "阴阳差错",
    )

    /** true nếu là Cát tinh, false nếu là Hung tinh. */
    fun isGood(name: String): Boolean {
        val n = name.trim()
        if (n.isEmpty()) return true
        return hungMarkers.none { n.contains(it) }
    }
}
