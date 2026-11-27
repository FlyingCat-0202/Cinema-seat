package com.example.cinemaseat.ui.state

import com.example.cinemaseat.model.CinemaHallLayout
import com.example.cinemaseat.model.Seat
import com.example.cinemaseat.model.SeatStatus
import com.example.cinemaseat.model.SeatType

/**
 * Yêu cầu di chuyển camera tới một ghế cụ thể.
 */
data class CameraFocusRequest(
    val seatId: String,
    val requestId: Long
)

/**
 * Trạng thái tổng thể của màn hình sơ đồ rạp chiếu.
 * Được giữ trong ViewModel để không bị mất khi xoay màn hình.
 */
data class CinemaUiState(
    val hallLayout: CinemaHallLayout,
    val selectedSeatIds: Set<String> = emptySet(),
    val selectedTypeFilter: SeatType? = null,
    val selectedStatusFilter: SeatStatus? = null,
    val highlightedSeatId: String? = null,
    val focusRequest: CameraFocusRequest? = null
) {
    val selectedSeats: List<Seat>
        get() = hallLayout.seats.filter { it.id in selectedSeatIds }

    val totalPrice: Int
        get() = selectedSeats.sumOf { it.price }

    val hasActiveFilter: Boolean
        get() = selectedTypeFilter != null || selectedStatusFilter != null

    fun matchesFilter(seat: Seat): Boolean {
        val typeOk = selectedTypeFilter == null || seat.type == selectedTypeFilter
        val statusOk = selectedStatusFilter == null ||
            seat.effectiveStatus(selectedSeatIds) == selectedStatusFilter
        return typeOk && statusOk
    }

    val matchingSeatCount: Int
        get() = hallLayout.seats.count { matchesFilter(it) }
}
