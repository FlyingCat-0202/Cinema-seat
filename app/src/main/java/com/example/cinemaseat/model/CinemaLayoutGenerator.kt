package com.example.cinemaseat.model

import androidx.compose.ui.geometry.Rect

/**
 * Sinh dữ liệu sơ đồ phòng chiếu theo bố cục MoMo / CGV:
 * - Hàng A, B, C (Ghế Thường): 3 hàng x 15 ghế (đánh số 15 -> 1 từ trái sang phải) = 45 ghế
 * - Hàng D, E, F, G, H, J, K, L (Ghế VIP, bỏ qua chữ I): 8 hàng x 15 ghế = 120 ghế
 * - Hàng S (Ghế Đôi / Sweetbox): 1 hàng x 9 ghế đôi = 9 ghế đôi
 * Tổng: 174 ghế + 1 Màn hình + 3 Lối đi (trái, phải, và hành lang Ra/Vào phía trên) = 178 đối tượng (đạt chuẩn 150–250 đối tượng).
 */
object CinemaLayoutGenerator {

    private const val SEAT_WIDTH = 56f
    private const val SEAT_HEIGHT = 54f
    private const val SEAT_GAP_X = 10f
    private const val ROW_GAP_Y = 14f

    private const val SIDE_AISLE_WIDTH = 58f
    private const val LEFT_MARGIN = 70f

    // Một số ghế được đặt sẵn hoặc bảo trì để minh họa đủ 4 trạng thái
    private val preBookedCodes = setOf(
        "A8", "A9", "B7", "B8", "C10", "C11",
        "E7", "E8", "E9", "F6", "F7", "F8", "F9", "F10",
        "G7", "G8", "G9", "H8", "H9", "J5", "J6", "K11", "K12",
        "S9-10", "S7-8"
    )

    private val unavailableCodes = setOf(
        "A1", "A15", "D1", "G15", "L1", "S17-18"
    )

    fun createSampleHall(): CinemaHallLayout {
        val seats = mutableListOf<Seat>()

        // Tổng chiều rộng khối 15 ghế liền nhau giống MoMo
        val blockWidth = 15 * SEAT_WIDTH + 14 * SEAT_GAP_X
        val contentLeft = LEFT_MARGIN + SIDE_AISLE_WIDTH + 20f
        val contentRight = contentLeft + blockWidth
        val worldWidth = contentRight + 20f + SIDE_AISLE_WIDTH + LEFT_MARGIN

        // Màn hình chiếu phía trên
        val screenBounds = Rect(
            left = contentLeft + 80f,
            top = 44f,
            right = contentRight - 80f,
            bottom = 104f
        )

        var currentY = 232f

        // --- KHU 1: GHẾ THƯỜNG (Hàng A, B, C - 3 hàng x 15 = 45 ghế) ---
        val standardRows = listOf("A", "B", "C")
        for (row in standardRows) {
            addRow15(
                seats = seats,
                row = row,
                top = currentY,
                zone = SeatZone.STANDARD,
                type = SeatType.STANDARD,
                startX = contentLeft
            )
            currentY += SEAT_HEIGHT + ROW_GAP_Y
        }

        // Khoảng phân cách giữa khu Thường và khu VIP (đủ chỗ cho nhãn phân khu)
        currentY += 36f

        // --- KHU 2: GHẾ VIP (Hàng D, E, F, G, H, J, K, L - 8 hàng x 15 = 120 ghế) ---
        val vipRows = listOf("D", "E", "F", "G", "H", "J", "K", "L")
        for (row in vipRows) {
            addRow15(
                seats = seats,
                row = row,
                top = currentY,
                zone = SeatZone.VIP,
                type = SeatType.VIP,
                startX = contentLeft
            )
            currentY += SEAT_HEIGHT + ROW_GAP_Y
        }

        // Khoảng phân cách giữa khu VIP và khu Ghế Đôi (đủ chỗ cho nhãn phân khu)
        currentY += 36f

        // --- KHU 3: GHẾ ĐÔI SWEETBOX (Hàng S - 9 ghế đôi trải đều dưới cùng) ---
        val coupleCount = 9
        val coupleGap = 10f
        val coupleWidth = (blockWidth + 80f - (coupleCount - 1) * coupleGap) / coupleCount
        val coupleStartX = contentLeft - 40f

        for (index in 0 until coupleCount) {
            val rightNum = (coupleCount - index) * 2
            val leftNum = rightNum - 1
            val code = "S$leftNum-$rightNum"
            val left = coupleStartX + index * (coupleWidth + coupleGap)
            seats.add(
                Seat(
                    id = code,
                    code = code,
                    rowLabel = "S",
                    column = index + 1,
                    zone = SeatZone.COUPLE,
                    type = SeatType.COUPLE,
                    baseStatus = resolveInitialStatus(code),
                    bounds = Rect(
                        left = left,
                        top = currentY,
                        right = left + coupleWidth,
                        bottom = currentY + SEAT_HEIGHT
                    )
                )
            )
        }
        currentY += SEAT_HEIGHT + ROW_GAP_Y

        val seatsTop = 210f
        val seatsBottom = currentY + 8f
        val worldHeight = seatsBottom + 56f
        val corridorLeft = LEFT_MARGIN - 12f
        val corridorRight = contentRight + 20f + SIDE_AISLE_WIDTH + 12f

        val entrances = listOf(
            // 1. Lối đi dọc bên trái (mũi tên 2 chiều lên/xuống)
            Entrance(
                label = "LỐI ĐI TRÁI",
                bounds = Rect(
                    left = corridorLeft,
                    top = seatsTop,
                    right = LEFT_MARGIN + SIDE_AISLE_WIDTH,
                    bottom = seatsBottom
                )
            ),
            // 2. Lối đi dọc bên phải (mũi tên 2 chiều lên/xuống)
            Entrance(
                label = "LỐI ĐI PHẢI",
                bounds = Rect(
                    left = contentRight + 20f,
                    top = seatsTop,
                    right = corridorRight,
                    bottom = seatsBottom
                )
            ),
            // 3. Hành lang ngang phía trên (gom cả đầu Ra bên trái & đầu Vào bên phải thành 1 Entrance)
            Entrance(
                label = "RA / VÀO",
                bounds = Rect(
                    left = corridorLeft,
                    top = 148f,
                    right = corridorRight,
                    bottom = 198f
                )
            )
        )

        return CinemaHallLayout(
            worldWidth = worldWidth,
            worldHeight = worldHeight,
            screenBounds = screenBounds,
            entrances = entrances,
            seats = seats
        )
    }

    private fun addRow15(
        seats: MutableList<Seat>,
        row: String,
        top: Float,
        zone: SeatZone,
        type: SeatType,
        startX: Float
    ) {
        // Đánh số từ 15 -> 1 (trái sang phải) giống hệt trong ảnh MoMo
        for (index in 0 until 15) {
            val col = 15 - index
            val left = startX + index * (SEAT_WIDTH + SEAT_GAP_X)
            val code = "$row$col"
            seats.add(
                Seat(
                    id = code,
                    code = code,
                    rowLabel = row,
                    column = col,
                    zone = zone,
                    type = type,
                    baseStatus = resolveInitialStatus(code),
                    bounds = Rect(
                        left = left,
                        top = top,
                        right = left + SEAT_WIDTH,
                        bottom = top + SEAT_HEIGHT
                    )
                )
            )
        }
    }

    private fun resolveInitialStatus(code: String): SeatStatus {
        return when {
            code in unavailableCodes -> SeatStatus.UNAVAILABLE
            code in preBookedCodes -> SeatStatus.BOOKED
            else -> SeatStatus.AVAILABLE
        }
    }
}
