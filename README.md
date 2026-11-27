# Quá trình sử dụng AI 
Model đã sử dụng: Gemini 4 Hydrogen, Gemini 4 Neon, ChatGPT Astra 6.2
## 1. Viết code sinh giúp ghế, màn hình và lối đi (Gemini 4 Hydrogen)
- AI tạo file CinemaLayoutGenerator.kt trong đó sẽ sinh sẵn các ghế (hàng A–C Thường, D–H VIP, I–J Ghế đôi) + màn hình + lối đi kèm vài ghế đã đặt/bảo trì mẫu
## 2. Tạo khung UI (Gemini 4 Hydrogen)
- Sinh viên nhờ AI tạo một bộ khung tham khảo UI để dựa trên đó hiểu workflow, cách code, vẽ giao diện UI mong muốn
## 3. Hướng dẫn cách co giãn động theo kích thước màn hình (Gemini 4 Hydrogen)
## 4. Tạo 1 bản draft canvas trong compose CinemaCanvas (Gemini 4 Hydrogen)
- Sinh viên nhờ AI tạo ra 1 bản draft cho hàm canvas bên trong CinemaCanvas compose
- Vấn đề bản draft là mặt hiệu năng (fps) bị giảm rất rõ rệt, gây giật lag khi thực hiện kéo (pan), một trong những nguyên nhân là do textMeasurer được gọi ra liên tục mỗi khung hình. Do đó sinh viên đã đem hàm đó ra ngoài canvas, gọi hàm tính sẵn để trong canvas chỉ cần gọi lại biến remember đó, xác thực sau khi thực hiện hiệu năng được tăng lên rõ rệt, pan đã mượt hơn
## 5. Nhờ đề xuất khung cho Filter 
- Sinh viên đã nhờ AI đề xuất workflow, khung, bản tham khảo cho filter
- Hạn chế là các lựa chọn được bung ra sẵn làm UI khá nhiều và rối, thiếu thẩm mỹ, lướt LazyRow để chọn lọc khá giật. Sinh viên sau đó đã chỉnh lại bằng việc ẩn các lựa chọn filter đi và hiển thị khi ấn chọn filter, làm mượt thanh filter, ngoài ra tận dụng thêm thanh này để add nút reset view tăng thêm tính thân thiện cho UI
