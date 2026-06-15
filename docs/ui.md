Hãy đóng vai là một lập trình viên Android Senior chuyên về Jetpack Compose và Material 3. Dựa trên file quy chuẩn CLAUDE.md của dự án `BatTu`, hãy thiết kế và viết code cho Composable Component của bảng Tứ Trụ (Four Pillars Grid) nằm trong `presentation/screens/chart/components/BaziGrid.kt`.

### 1. Quy chuẩn Kỹ thuật phải tuân thủ (Bắt buộc):
- Cấu trúc dữ liệu: Tạo một `@Immutable` data class tên là `BaziGridState` hoặc `BaziColumnState` để chứa dữ liệu hiển thị (Thiên Can, Địa Chi, Thập Thần, Tàng Can).
- Quản lý màu sắc:
    + KHÔNG hardcode màu hex cho Ngũ Hành. Hãy sử dụng các token màu giả định từ `com.anhnn.battu.presentation.theme.WuxingColors` (Ví dụ: WuxingColors.Kim, WuxingColors.Mộc...).
    + Sử dụng `MaterialTheme.colorScheme.surface` hoặc `surfaceVariant` làm nền cho các thẻ.
- Thứ tự Modifier chuẩn của dự án: size -> clip/background -> clickable -> padding.
- Hỗ trợ tối thiểu 2 `@Preview` (Light và Dark Mode) sử dụng hệ thống Theme của ứng dụng.

### 2. Yêu cầu thiết kế UI/UX cho BaziGrid:
- Bố cục truyền thống: Hiển thị 4 cột theo thứ tự từ PHẢI qua TRÁI: [Trụ Năm] -> [Trụ Tháng] -> [Trụ Ngày] -> [Trụ Giờ] bằng cách sử dụng `Row` kết hợp với `Modifier.weight(1f)` cho mỗi cột.
- Cấu trúc của mỗi Trụ (Cột):
    + Tiêu đề Trụ (Năm/Tháng/Ngày/Giờ) nằm ở trên cùng, sử dụng text style của Material 3.
    + Ô Thiên Can (ở trên) và Ô Địa Chi (ở dưới). Mỗi ô được thiết kế dạng Card hoặc Box bo góc nhẹ, chữ to, rõ ràng. Nền của ô Can/Chi phải được fill bằng màu Ngũ Hành tương ứng (lấy từ WuxingColors) với độ mờ alpha (0.15f) để tạo độ tinh tế, text hiển thị Can Chi phối hợp màu đậm của hành đó hoặc onSurface.
    + Dưới cùng của mỗi cột hiển thị Thập Thần chính và danh sách các Tàng Can nhỏ hơn (có thể dùng Column nhỏ xếp dọc hoặc Row xếp ngang bên trong trụ).
- Hiệu ứng Nhật Chủ: Trụ Ngày (Nhật Chủ) là trung tâm của lá số, hãy thiết kế viền (Border) hoặc hiệu ứng đặc biệt sử dụng Signature Gradient của Anhnn (`#A1A2FF` sang `#4B4EEE`) bao quanh để người dùng dễ dàng nhận biết bản mệnh của mình.

### 3. Cấu trúc Code đầu ra:
- Hãy viết code hoàn chỉnh, bóc tách các component nhỏ (ví dụ: `BaziColumn`, `CanChiCell`) một cách sạch sẽ để dễ bảo trì.
- Các tương tác bấm vào Can/Chi để xem giải nghĩa phải được đẩy lên dưới dạng sự kiện lambda (onCellClick: (ElementKey) -> Unit) theo đúng mô hình Unidirectional Data Flow (UDF).