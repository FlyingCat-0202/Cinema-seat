# Ứng dụng Sơ đồ Phòng Chiếu Rạp Phim Tương Tác (Cinema Seat Map)

Đồ án Giữa kỳ - Học phần: Phát triển phần mềm cho thiết bị di động

Đề tài 02: Sơ đồ phòng chiếu rạp phim (Màn hình, lối đi, ghế thường, ghế VIP, ghế đôi và trạng thái đặt vé)

---

## 1. Mô tả đề tài
Ứng dụng Android (Kotlin + Jetpack Compose) mô phỏng sơ đồ phòng chiếu rạp phim với 178 đối tượng được vẽ hoàn toàn trên 1 bề mặt Canvas duy nhất:
- 174 ghế phân theo 3 phân khu rõ ràng:
  - Khu Ghế Thường (hàng A-C, 3 hàng x 15 ghế): 45 ghế (75.000đ)
  - Khu Ghế VIP (hàng D-L, bỏ I, 8 hàng x 15 ghế): 120 ghế (110.000đ)
  - Khu Ghế Đôi (hàng S, 9 ghế đôi): 9 ghế (190.000đ)
- 1 Màn hình chiếu cong kèm nhãn MÀN HÌNH.
- 3 Lối đi (Entrance): Hành lang ngang RA / VÀO phía trên kèm mũi tên 1 chiều, và 2 lối đi dọc hai bên kèm mũi tên 2 chiều + nhãn xoay dọc LỐI ĐI.
- 4 trạng thái: Trống (AVAILABLE), Đang chọn (SELECTED), Đã đặt (BOOKED), Không khả dụng (UNAVAILABLE).
- Công cụ hỗ trợ và tương tác: Pan (có giới hạn biên), Pinch-to-Zoom quanh tâm 2 ngón, Hit-testing chính xác sau Pan/Zoom, Bộ lọc (Loại ghế và Trạng thái), Bảng chú thích (Legend), Reset View mượt, Expandable Bottom Sheet đồng bộ 2 chiều (chọn/xóa/định vị ghế) và giữ nguyên trạng thái khi xoay màn hình.

## 2. Video minh họa
- [Video demo google drive](https://drive.google.com/file/d/1L4SuIHdEbmVDi1Zje1n5ynTVBdZbmzBm/view?usp=sharing)
- [Link dự phòng qua youtube](https://youtu.be/ZPx6TYrDfic)

## 3. Hướng dẫn chạy ứng dụng

1. Clone repository về máy rồi mở thư mục dự án bằng Android Studio.
2. Trong SDK Manager, cài **Android SDK Platform 37**. Dự án dùng **JDK 25** để chạy Gradle. Nếu Android Studio yêu cầu tải thêm JDK hoặc SDK, hãy hoàn tất cài đặt rồi chờ Gradle Sync chạy xong.
3. Chọn máy ảo hoặc điện thoại chạy Android 7.0 (API 24) trở lên, sau đó nhấn **Run** để mở màn hình chọn ghế.

## 4. Cấu trúc mã nguồn

```text
app/src/main/java/com/example/cinemaseat/
├── MainActivity.kt                # Khởi tạo ứng dụng và mở màn hình chọn ghế.
├── model/
│   ├── CinemaLayoutGenerator.kt   # Tạo sơ đồ mẫu gồm 174 ghế, màn chiếu và các lối đi.
│   └── SeatModels.kt              # Định nghĩa dữ liệu ghế, lối đi và phòng chiếu.
└── ui/
    ├── MainScreen.kt              # Bố trí bộ lọc, sơ đồ ghế và bảng ghế đã chọn.
    ├── components/
    │   ├── BottomSheet.kt         # Hiển thị chú thích, danh sách ghế đã chọn và tổng tiền.
    │   ├── CinemaCanvas.kt        # Vẽ sơ đồ, xử lý kéo, thu phóng và xác định ghế được chạm.
    │   └── FilterBar.kt           # Hiển thị bộ lọc loại ghế, trạng thái ghế và nút đặt lại góc nhìn.
    ├── state/
    │   ├── CinemaUiState.kt       # Định nghĩa trạng thái màn hình, gồm ghế đã chọn, bộ lọc và yêu cầu định vị.
    │   └── CinemaViewModel.kt     # Cập nhật trạng thái khi chọn, bỏ chọn, lọc hoặc định vị ghế.
    └── theme/
        ├── Color.kt               # Khai báo màu dùng cho giao diện sáng và tối.
        ├── Theme.kt               # Áp dụng bảng màu và kiểu chữ cho giao diện.
        └── Type.kt                # Khai báo kiểu chữ mặc định của ứng dụng.
```

## 5. Chuyển đổi tọa độ và xác định ghế được chạm

Mỗi ghế có vị trí cố định trên sơ đồ (`world`). Vị trí của ghế trên Canvas (`screen`) phụ thuộc vào tỷ lệ thu phóng `scale` và độ dịch chuyển `offset` tính bằng pixel.

### Khi vẽ và khi chạm

| Thao tác | Tọa độ X | Tọa độ Y |
| --- | --- | --- |
| Vẽ ghế từ sơ đồ lên Canvas | `screenX = worldX × scale + offsetX` | `screenY = worldY × scale + offsetY` |
| Đổi điểm chạm về sơ đồ | `worldX = (screenX − offsetX) / scale` | `worldY = (screenY − offsetY) / scale` |

Khi người dùng chạm, ứng dụng dùng `scale` và `offset` hiện tại để đổi điểm chạm về sơ đồ. Hàm `containsRounded` kiểm tra điểm đó có nằm trong hình chữ nhật bo góc của ghế hay không.

### Khi mở sơ đồ hoặc nhấn Reset View

Ứng dụng chọn tỷ lệ vừa cả chiều rộng lẫn chiều cao, thu thêm 4% để chừa lề rồi căn giữa sơ đồ.

```text
fitScale = 0.96 × min(canvasWidth / worldWidth, canvasHeight / worldHeight)

offsetX = (canvasWidth  − worldWidth  × fitScale) / 2
offsetY = (canvasHeight − worldHeight × fitScale) / 2
```

### Khi kéo hoặc thu phóng bằng hai ngón

`zoom` là hệ số thu phóng của bước cử chỉ hiện tại. `centroid` là tâm hai ngón tay và `pan` là độ dịch chuyển của cử chỉ trên Canvas.

```text
minScale = max(0.75 × fitScale, 0.2)
maxScale = max(3.5  × fitScale, 2.0)
newScale = clamp(oldScale × zoom, minScale, maxScale)
```

Ứng dụng điều chỉnh độ dịch chuyển quanh tâm hai ngón, cộng phần kéo, rồi giới hạn vị trí để sơ đồ vẫn nằm trong tầm nhìn. Công thức dưới đây áp dụng cho cả hai trục.

```text
zoomRatio = newScale / oldScale
rawOffset = centroid − (centroid − offset) × zoomRatio + pan
newOffset = clampOffset(rawOffset, newScale, canvasSize)
```

## 6. Quản lý trạng thái và tối ưu hiệu năng

**Đồng bộ lựa chọn:** Khi người dùng chạm vào một ghế trống, `CinemaViewModel` cập nhật `CinemaUiState` qua `StateFlow`. Canvas đọc trạng thái mới để đổi màu ghế, còn Bottom Sheet cập nhật danh sách và tổng tiền. Nếu người dùng xóa ghế ngay trong Bottom Sheet, thay đổi đó cũng hiển thị trên sơ đồ.

**Khi xoay màn hình:** Danh sách ghế đã chọn và bộ lọc nằm trong `ViewModel`, nên người dùng có thể xoay máy rồi tiếp tục chọn ghế. Bảng lọc và Bottom Sheet dùng `rememberSaveable` để giữ trạng thái đóng hoặc mở.

**Khi kéo và thu phóng:** Vị trí ghế trên màn hình thay đổi liên tục, nhưng nội dung các nhãn ghế vẫn như cũ. Ứng dụng dùng `remember` để tái sử dụng kết quả đo chữ và đường cong màn chiếu khi dữ liệu liên quan chưa đổi. Các cặp màu theo loại ghế và trạng thái cũng được khai báo sẵn. Nhờ vậy, Canvas vẽ lại sơ đồ mà không phải đo lại 174 nhãn ghế hay tạo lại từng cặp màu ở mỗi khung hình.

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
