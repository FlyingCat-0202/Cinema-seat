package com.example.cinemaseat.ui.state

import androidx.lifecycle.ViewModel
import com.example.cinemaseat.model.CinemaLayoutGenerator
import com.example.cinemaseat.model.Seat
import com.example.cinemaseat.model.SeatStatus
import com.example.cinemaseat.model.SeatType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CinemaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        CinemaUiState(hallLayout = CinemaLayoutGenerator.createSampleHall())
    )
    val uiState: StateFlow<CinemaUiState> = _uiState.asStateFlow()

    private var nextRequestId = 1L

    /**
     * Chọn hoặc bỏ chọn một ghế trên Canvas.
     * Chỉ cho phép chọn ghế có baseStatus == AVAILABLE và đang khớp bộ lọc.
     */
    fun onSeatTapped(seat: Seat) {
        _uiState.update { state ->
            if (!seat.isSelectable || !state.matchesFilter(seat)) {
                // Nếu bấm vào ghế đang bị khóa/đặt, chỉ highlight nhẹ để xem (nếu khớp filter) hoặc bỏ qua
                return@update state
            }
            val updatedSelected = state.selectedSeatIds.toMutableSet()
            if (!updatedSelected.add(seat.id)) {
                updatedSelected.remove(seat.id)
            }
            val newHighlight = if (seat.id in updatedSelected) seat.id else null
            state.copy(
                selectedSeatIds = updatedSelected,
                highlightedSeatId = newHighlight
            )
        }
    }

    /**
     * Xóa một ghế khỏi danh sách đã chọn (thao tác từ Bottom Sheet).
     */
    fun removeSelectedSeat(seatId: String) {
        _uiState.update { state ->
            val updatedSelected = state.selectedSeatIds - seatId
            val newHighlight = if (state.highlightedSeatId == seatId) null else state.highlightedSeatId
            state.copy(
                selectedSeatIds = updatedSelected,
                highlightedSeatId = newHighlight
            )
        }
    }

    /**
     * Xóa toàn bộ ghế đã chọn.
     */
    fun clearAllSelectedSeats() {
        _uiState.update { state ->
            state.copy(
                selectedSeatIds = emptySet(),
                highlightedSeatId = null
            )
        }
    }

    /**
     * Định vị và làm nổi bật một ghế trên Canvas (thao tác từ Bottom Sheet).
     */
    fun focusOnSeat(seatId: String) {
        _uiState.update { state ->
            state.copy(
                highlightedSeatId = seatId,
                focusRequest = CameraFocusRequest(
                    seatId = seatId,
                    requestId = nextRequestId++
                )
            )
        }
    }

    fun toggleTypeFilter(type: SeatType?) {
        _uiState.update { state ->
            val next = if (state.selectedTypeFilter == type) null else type
            state.copy(selectedTypeFilter = next)
        }
    }

    fun toggleStatusFilter(status: SeatStatus?) {
        _uiState.update { state ->
            val next = if (state.selectedStatusFilter == status) null else status
            state.copy(selectedStatusFilter = next)
        }
    }

    fun clearFilters() {
        _uiState.update { state ->
            state.copy(
                selectedTypeFilter = null,
                selectedStatusFilter = null
            )
        }
    }
}
