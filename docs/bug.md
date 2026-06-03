Để tạo lá Bát Tự, frontend chỉ cần gọi một endpoint duy nhất:

📤 Gửi lên: POST /api/v1/chart

Chỉ cần 2 trường (backend/schemas/chart.py:16):

{
"datetime_str": "1990-05-15 14:30",
"gender": "乾造 (Male)"
}

┌──────────────┬────────┬──────────────────────────────────────────────────┐
│    Trường    │  Kiểu  │                    Ràng buộc                     │
├──────────────┼────────┼──────────────────────────────────────────────────┤
│ datetime_str │ string │ Giờ sinh dương lịch, format YYYY-MM-DD HH:MM     │
│              │        │ hoặc YYYY-MM-DD HH:MM:SS, max 25 ký tự           │
├──────────────┼────────┼──────────────────────────────────────────────────┤
│              │        │ Bắt buộc đúng chính xác 1 trong 2 chuỗi: "乾造   │
│ gender       │ string │ (Male)" (nam) hoặc "坤造 (Female)" (nữ) — sai là │
│              │        │  bị 400/422                                      │
└──────────────┴────────┴──────────────────────────────────────────────────┘

⚠️  Lưu ý cho UI Android: người dùng chọn nam/nữ thì app phải map sang đúng
chuỗi tiếng Trung trên, không gửi "male"/"nam".

📥 Nhận về: ChartResponse — đầy đủ dữ liệu lá số trong 1 response

Không cần gọi thêm endpoint nào khác, mọi thứ về lá số nằm trong 3 khối
(backend/schemas/common.py:44):

{
"chart": {                          // ← LÁ SỐ GỐC (luôn có)
"gender": "乾造 (Male)",
"pillars": ["庚午","辛巳","庚寅","癸未"],   // Tứ trụ [năm, tháng, ngày,
giờ]
"day_master": "庚",                        // Nhật chủ
"tg_gan": ["比肩","劫财","日主","伤官"],     // Thập thần thiên can
"tg_zhi": ["...","...","...","..."],       // Thập thần tàng can địa chi
"nayin": ["路旁土","白蜡金","松柏木","杨柳木"], // Nạp âm
"wuxing": {"金":2,"木":1,"水":1,"火":2,"土":2}, // Đếm ngũ hành
"wuxing_str": "金火金火金木水土",
"dayun": [                                  // Đại vận
{"start_age": 3, "start_year": 1993, "ganzhi": "壬午"}, ...
],
"minggong": "...",  "taiyuan": "...",       // Mệnh cung, Thai nguyên
"taixi": "...",     "shengong": "...",      // Thai tức, Thân cung
"dishi": ["沐浴","长生","绝","冠带"],          // Thập nhị trường sinh
"xunkong": ["戌亥","申酉","午未","申酉"],      // Tuần không
"xingchong": {                              // Hình-Xung-Hợp-Hại
"冲": [...], "合": [...], "刑": [...], "害": [...],
"破": [...], "三合": [...], "三会": [...], "半三合": [...]
},
"shensha": ["将星","天乙贵人"],               // Thần sát
"shensha_detail": {"0": ["将星"], ...}       // Thần sát theo từng trụ
(key = index trụ)
},
"wuxing_power": {                   // ← PHÂN TÍCH LỰC NGŨ HÀNH (có thể
null)
"power": {"金": 3.8, "木": 14.4, ...},
"strong": ["火"], "weak": ["金"],
"balanced": false,
"context": "mô tả chữ"
},
"geju": {                           // ← CÁCH CỤC (có thể null, key tiếng
Trung)
"格局类型": "...", "格局名称": "...",
"月令": "...", "日主强弱": "...",
"日主力量占比": 3.8, "context": "..."
}
}

🔑 3 điều quan trọng cho app Kotlin

1. Lưu nguyên khối chart sau khi nhận — khi user chat với AI, gửi lại y nguyên
   nó làm chart_data trong POST /api/v1/chat/stream. Không cần biến đổi gì.
2. wuxing_power và geju có thể null → khai báo nullable trong data class.
3. geju dùng key tiếng Trung ("格局类型"...) → với kotlinx.serialization dùng
   @SerialName("格局类型"), hoặc đơn giản nhận Map<String, JsonElement>.

Lỗi có thể gặp: 400 (sai format ngày / gender), 422 (thiếu trường), 500 (lỗi
server).

Các endpoint còn lại (/compatibility, /texts, /entertainment/daily-fortune) là
tính năng phụ, không liên quan đến việc lập lá số.

Bạn muốn tôi viết sẵn bộ Kotlin data class + Retrofit interface cho /chart (và
cả SSE client cho /chat/stream) không? Nếu có, cho tôi biết project dùng
kotlinx.serialization hay Gson/Moshi.

