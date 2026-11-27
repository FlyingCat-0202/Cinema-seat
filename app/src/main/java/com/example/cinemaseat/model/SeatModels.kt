package com.example.cinemaseat.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

/**
 * Loại ghế trong phòng chiếu.
 */
enum class SeatType(val label: String, val defaultPrice: Int) {
    STANDARD("Ghế Thường", 75_000),
    VIP("Ghế VIP", 110_000),
    COUPLE("Ghế Đôi", 190_000)
}

/**
 * Trạng thái của một ghế.
 */
enum class SeatStatus(val label: String) {
    AVAILABLE("Trống"),
    SELECTED("Đang chọn"),
    BOOKED("Đã đặt"),
    UNAVAILABLE("Không khả dụng")
}

/**
 * Phân khu trong rạp chiếu.
 */
enum class SeatZone(val label: String) {
    STANDARD("KHU GHẾ THƯỜNG"),
    VIP("KHU GHẾ VIP"),
    COUPLE("KHU GHẾ ĐÔI")
}


data class Seat(
    val id: String,
    val code: String,
    val rowLabel: String,
    val column: Int,
    val zone: SeatZone,
    val type: SeatType,
    val baseStatus: SeatStatus,
    val price: Int = type.defaultPrice,
    val bounds: Rect
) {
    fun effectiveStatus(selectedSeatIds: Set<String>): SeatStatus {
        return if (id in selectedSeatIds) SeatStatus.SELECTED else baseStatus
    }

    val isSelectable: Boolean
        get() = baseStatus == SeatStatus.AVAILABLE
}

/**
 * Lối đi trên sơ đồ.
 */
data class Entrance(
    val label: String,
    val bounds: Rect
)

/**
 * Toàn bộ dữ liệu hình học của phòng chiếu trong không gian World.
 */
data class CinemaHallLayout(
    val worldWidth: Float,
    val worldHeight: Float,
    val screenBounds: Rect,
    val entrances: List<Entrance>,
    val seats: List<Seat>
)
