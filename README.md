# Ứng dụng Sơ đồ Phòng Chiếu Rạp Phim Tương Tác (Cinema Seat Map)

Đồ án Giữa kỳ - Học phần: Phát triển phần mềm cho thiết bị di động
Đề tài 02: Sơ đồ phòng chiếu rạp phim (Màn hình, lối đi, ghế thường, ghế VIP, ghế đôi và trạng thái đặt vé)

---

## 1. Mô tả đề tài
Ứng dụng Android (Kotlin + Jetpack Compose) mô phỏng sơ đồ phòng chiếu rạp phim theo phong cách MoMo/CGV với 178 đối tượng được vẽ hoàn toàn trên 1 bề mặt Canvas duy nhất:
- 174 ghế phân theo 3 phân khu rõ ràng:
  - Khu Ghế Thường (hàng A-C, 3 hàng x 15 ghế): 45 ghế (75.000đ)
  - Khu Ghế VIP (hàng D-L, bỏ I, 8 hàng x 15 ghế): 120 ghế (110.000đ)
  - Khu Ghế Đôi (hàng S, 9 ghế đôi): 9 ghế (190.000đ)
- 1 Màn hình chiếu cong kèm nhãn MÀN HÌNH.
- 3 Lối đi (Entrance): Hành lang ngang RA / VÀO phía trên kèm mũi tên 1 chiều, và 2 lối đi dọc hai bên kèm mũi tên 2 chiều + nhãn xoay dọc LỐI ĐI.
- 4 Trạng thái trực quan: Trống (AVAILABLE - phân màu theo loại ghế), Đang chọn (SELECTED - hồng cánh sen, chữ trắng), Đã đặt (BOOKED - xanh dương nhạt), Không khả dụng (UNAVAILABLE - xám mờ).
- Công cụ hỗ trợ và Tương tác: Pan (có giới hạn biên), Pinch-to-Zoom quanh tâm 2 ngón, Hit-testing chính xác sau Pan/Zoom, Bộ lọc (Loại ghế và Trạng thái), Bảng chú thích (Legend), Reset View mượt, Expandable Bottom Sheet đồng bộ 2 chiều (chọn/xóa/định vị ghế) và giữ nguyên trạng thái khi xoay màn hình.

## 2. Video minh họa
- Đường dẫn video demo (01-05 phút): <CHÈN_LINK_VIDEO_DEMO_CỦA_BẠN_VÀO_ĐÂY>

## 3. Hướng dẫn chạy ứng dụng
- Môi trường: Android Studio (JDK 11 trở lên), Android SDK compileSdk = 37, minSdk = 24.
- Các bước chạy:
  1. Clone repository về máy và mở thư mục dự án bằng Android Studio.
  2. Chờ Gradle Sync hoàn tất.
  3. Chọn Emulator hoặc thiết bị Android thật (API >= 24) và nhấn Run (Shift + F10).
  4. Mẹo test Pinch-to-Zoom trên Emulator: Giữ phím Ctrl (hoặc Cmd trên macOS) và click kéo chuột trên màn hình sơ đồ.

## 4. Kiến trúc mã nguồn và Cách tổ chức dữ liệu
Ứng dụng áp dụng kiến trúc UDF (Unidirectional Data Flow) tách biệt hoàn toàn 3 lớp Dữ liệu (model/) - Trạng thái (ui/state/) - Giao diện (ui/):

    app/src/main/java/com/example/cinemaseat/
    ├── MainActivity.kt                      // Khởi chạy ứng dụng, bật edge-to-edge
    ├── model/
    │   ├── SeatModels.kt                    // SeatType, SeatStatus, SeatZone, Seat, Entrance, CinemaHallLayout
    │   └── CinemaLayoutGenerator.kt         // Sinh dữ liệu giả lập 178 đối tượng với tọa độ World cố định
    └── ui/
        ├── MainScreen.kt                    // Ghép FilterHeader, CinemaCanvas, FilterDropdownOverlay và BottomSheet
        ├── state/
        │   ├── CinemaUiState.kt             // State tổng bất biến + logic lọc matchesFilter, tính totalPrice
        │   └── CinemaViewModel.kt           // Quản lý chọn/bỏ chọn ghế, lọc, định vị ghế; sống sót khi xoay màn hình
        └── components/
            ├── CinemaCanvas.kt              // 1 Canvas duy nhất vẽ toàn bộ sơ đồ + Pan/Zoom/Hit-testing/Animation
            ├── FilterBar.kt                 // FilterHeader (tiêu đề + nút Filter/Reset View) & FilterDropdownOverlay
            └── BottomSheet.kt               // LegendRow + bảng danh sách ghế đã chọn mở rộng/thu gọn + nút Tiếp tục

- Tách dữ liệu khỏi lớp vẽ: Mỗi ghế là một Seat lưu sẵn tọa độ hình học bounds: Rect trong hệ tọa độ World, type, baseStatus, price. Trạng thái đang chọn được quản lý bằng tập hợp selectedSeatIds: Set<String> trong CinemaUiState, kết hợp hàm effectiveStatus(selectedSeatIds) để khi bỏ chọn luôn quay về đúng baseStatus ban đầu và không bị trùng lặp.

## 5. Công thức chuyển đổi tọa độ và Phương pháp Hit-testing
Toàn bộ sơ đồ được định nghĩa trong hệ tọa độ gốc (World Coordinates: worldWidth x worldHeight) và hiển thị lên màn hình (Screen Coordinates) trong CinemaCanvas.kt:

1. Co vừa khít mặc định (fitScale):
   fitScale = min(canvasWidth / worldWidth, canvasHeight / worldHeight) * 0.96
   offsetDefault = ((canvasWidth - worldWidth * fitScale) / 2, (canvasHeight - worldHeight * fitScale) / 2)

2. World -> Screen (khi vẽ bằng withTransform):
   screenX = offset.x + worldX * scale
   screenY = offset.y + worldY * scale

3. Pinch-to-Zoom quanh tâm 2 ngón (centroid) và Pan:
   newScale = clamp(oldScale * zoom, minScale, maxScale)   // minScale = fitScale * 0.75, maxScale = fitScale * 3.5
   zoomRatio = newScale / oldScale
   rawOffset = centroid - (centroid - offset) * zoomRatio + pan
   offset = clampOffset(rawOffset, newScale, canvasSize)   // Luôn giữ tối thiểu biên an toàn trong khung nhìn

4. Screen -> World và Hit-testing (khi chạm chọn ghế tại tapOffset):
   worldX = (tapOffset.x - offset.x) / scale
   worldY = (tapOffset.y - offset.y) / scale
   hitSeat = hall.seats.firstOrNull { seat -> seat.bounds.contains(Offset(worldX, worldY)) }

Nhờ biến đổi ngược trực tiếp từ cùng offset và scale đang dùng để vẽ, kết quả hit-testing chính xác tới từng pixel sát mép ghế sau mọi thao tác Pan và Zoom.

## 6. Quản lý trạng thái và Tối ưu hiệu năng
- Quản lý trạng thái: CinemaViewModel giữ StateFlow<CinemaUiState> nên khi xoay dọc/ngang màn hình, toàn bộ danh sách ghế đang chọn và bộ lọc được bảo toàn nguyên vẹn; trạng thái đóng/mở bảng lọc và Bottom Sheet dùng rememberSaveable. Canvas, Bộ lọc và Bottom Sheet cùng đọc một nguồn dữ liệu uiState, đảm bảo đồng bộ hai chiều tức thì.
- Tối ưu hiệu năng:
  - Toàn bộ Path (screenPath), Stroke, CornerRadius, TextMeasurer, VectorPainter (monoarrow, biarrow) và các TextLayoutResult (screenLabel, exitText, entryText, aisleText, zoneLabels, seatTextLayouts) được tính toán và lưu trữ một lần ở ngoài Canvas bằng remember(...).
  - Bên trong Canvas { ... } không cấp phát thêm đối tượng đồ họa nặng hay đo lại chữ mỗi khung hình, giúp thao tác Pan, Zoom và animation (tween(350)) phản hồi mượt mà.

---

## 7. Nhật ký sử dụng AI Agent

Các AI Agent / Model đã sử dụng: Gemini 4 Hydrogen.

### 1. Sinh dữ liệu ghế, màn hình và lối đi (SeatModels.kt, CinemaLayoutGenerator.kt)
- Nhiệm vụ giao cho AI: Thiết kế bản tham khảo mô hình dữ liệu và viết CinemaLayoutGenerator.kt sinh sẵn sơ đồ rạp gồm ghế Thường, VIP, Đôi, màn hình và lối đi kèm trạng thái mẫu.
- Phần mã AI đề xuất:
  - Ban đầu AI tạo 108 ghế (hàng A-C Thường, D-H VIP, I-J Đôi, 12 ghế/hàng có lối đi giữa) cùng các model phụ ZoneLabel, RowLabel, AisleArea và SeatZone (FRONT_STANDARD, MIDDLE_VIP, BACK_COUPLE).
- Cách sinh viên kiểm tra và chỉnh sửa:
  - Thêm ghế để đạt trên 150 đối tượng, đơn giản hóa SeatModels.kt bằng cách bỏ ZoneLabel/RowLabel, đổi tên enum SeatZone thành STANDARD, VIP, COUPLE và đổi AisleArea thành Entrance (entrances) cho dễ hiểu.

### 2. Tạo khung kiến trúc UI và Luồng dữ liệu (MainActivity.kt, MainScreen.kt, CinemaViewModel.kt, CinemaUiState.kt)
- Nhiệm vụ giao cho AI: Đề xuất kiến trúc Jetpack Compose kết hợp ViewModel + StateFlow.
- Phần mã AI đề xuất:
  - Khung màn hình sử dụng BottomSheetScaffold (nền tối), cùng CinemaUiState và CinemaViewModel xử lý chọn ghế, lọc, định vị.
- Hạn chế / Lỗi sai của AI:
  - Khung ban đầu ghép sẵn nhiều thành phần hơi phức tạp, tạo lại nhiều biến thừa thãi và giao diện nền tối không phù hợp, gây khó chịu mắt.
- Cách sinh viên kiểm tra và chỉnh sửa:
  - Rút gọn MainScreen.kt về bản tối giản để tự xây dựng từng phần, chuyển sang bố cục Column + Box trên nền trắng.

### 3. Hướng dẫn vẽ sơ đồ trên Canvas và co giãn động (CinemaCanvas.kt)
- Nhiệm vụ giao cho AI: Hướng dẫn cách vẽ Canvas, cách co giãn động theo kích thước màn hình, tính tỷ lệ cỡ chữ theo kích thước ô ghế và xuất mũi tên lối đi dạng vector.
- Phần mã AI đề xuất:
  - Code vẽ thử hàng ghế ban đầu với kích thước cố định (seatSize = 80f) và font 14.sp.
  - Gợi ý file SVG cho 2 mũi tên lối đi.
- Hạn chế / Lỗi sai của AI:
  - Lỗi tràn màn hình và lệch cỡ chữ: Ghế cố định bị tràn mép phải, cỡ chữ sp thay đổi bất thường theo density của từng máy.
  - Lỗi Render Preview và vị trí mũi tên: Hai file mono_direction_arrow.xml và bidirection_arrow.xml ban đầu dán thẻ svg gây Render Error, mũi tên bên phải bị lòi ra ngoài mép do chưa trừ chiều rộng 80f, hai mũi tên dọc bị ép sát ghế phải do căn theo bounds.left.
- Cách sinh viên kiểm tra và chỉnh sửa:
  - Đổi sang hệ tọa độ World với fitScale và ép cỡ chữ cho phù hợp chiều cao ghế.
  - Kiểm tra trực tiếp trên tab Split, chuyển cả 2 drawable sang chuẩn vector của Android, sửa tọa độ căn giữa theo bounds.center, chỉnh màu ghế, và bổ sung thêm 3 nhãn phân khu (KHU GHẾ THƯỜNG, KHU GHẾ VIP, KHU GHẾ ĐÔI).

### 4. Xây dựng tương tác Pan, Zoom, Hit-testing và tối ưu FPS trong CinemaCanvas.kt
- Nhiệm vụ giao cho AI: Hỗ trợ ghép detectTransformGestures, clampOffset, detectTapGestures và animate cho Reset View / Focus ghế vào CinemaCanvas.kt.
- Phần mã AI đề xuất:
  - Bản draft ban đầu của CinemaCanvas gọi textMeasurer.measure(...) và tạo Path() trực tiếp bên trong block Canvas mỗi lần vẽ lại.
- Hạn chế / Lỗi sai của AI:
  - Hạn chế về hiệu năng (FPS): Khi thử thao tác Pan và Zoom liên tục trên bản draft, khung hình bị giảm FPS và có hiện tượng khựng nhẹ do textMeasurer.measure(...) bị gọi lặp lại cho 174 ghế ở mỗi khung hình.
- Cách sinh viên kiểm tra và chỉnh sửa:
  - Tách screenPath, screenLabel, exitText, entryText, aisleText và toàn bộ seatTextLayouts ra ngoài Canvas để đo và cache sẵn một lần duy nhất, bên trong Canvas chỉ tra cứu để vẽ. Kiểm tra lại cho thấy thao tác Pan/Zoom mượt hơn rõ rệt.
  - Ngoài ra, sinh viên cũng tối ưu lại các hàm bằng cách dùng trực tiếp fitScale thay cho các biến tính thừa từ AI.

### 5. Thiết kế Header và bộ lọc (FilterBar.kt, MainScreen.kt)
- Nhiệm vụ giao cho AI: Đề xuất khung FilterBar và logic bật/tắt lọc.
- Phần mã AI đề xuất:
  - Bản đầu tiên hiển thị sẵn thanh chip lọc cố định trên đầu màn hình kèm hàng tiêu đề phụ Đang lọc: X/174 ghế, và khi làm dạng xổ xuống ban đầu bị đẩy CinemaCanvas tụt xuống dưới kèm shadowElevation tạo bóng đen quanh viền.
- Hạn chế / Lỗi sai của AI:
  - Hiển thị sẵn toàn bộ chip chiếm nhiều diện tích và làm giao diện rối, thiếu thẩm mỹ.
- Cách sinh viên kiểm tra và chỉnh sửa:
  - Thiết kế lại FilterHeader, gom nút Bộ lọc và Reset View vào cụm bên phải, ẩn danh sách chip mặc định và chỉ cho FilterDropdownOverlay trượt đè lên mép trên Canvas khi bấm icon lọc, bỏ dòng subtitle thừa và bỏ shadowElevation.

### 6. Tối ưu kích thước Bottom Sheet (BottomSheet.kt)
- Nhiệm vụ giao cho AI: Hỗ trợ co giãn bảng Bottom Sheet tự động để hiển thị vừa vặn trên nhiều dòng máy (màn hình nhỏ và khi xoay ngang).
- Phần mã AI đề xuất:
  - AI hướng dẫn lấy chiều cao màn hình qua LocalConfiguration (screenHeightDp) để tính tỷ lệ chiều cao cho danh sách ghế.
- Hạn chế / Lỗi sai của AI:
  - Android Studio báo cảnh báo vàng ngay tại dòng code của AI vì cách lấy screenHeightDp đã cũ, không chuẩn xác khi hiển thị tràn viền.
  - Nút Tiếp tục bị đặt nhầm vị trí nên trồi lên trên bảng danh sách ghế thay vì nằm ở đáy màn hình.
  - Ô mã ghế đặt kích thước cố định khiến các ghế đôi có tên dài bị cắt bớt chữ.
- Cách sinh viên kiểm tra và chỉnh sửa:
  - Đọc cảnh báo lint trong Android Studio rồi đổi sang dùng LocalWindowInfo kết hợp LocalDensity để đo kích thước chuẩn hơn và xóa sạch cảnh báo.
  - Đưa nút Tiếp tục vào đúng vị trí cuối cùng để luôn cố định ở chân bảng.
  - Chỉnh lại ô mã ghế tự co giãn vừa vặn theo độ dài tên ghế.
