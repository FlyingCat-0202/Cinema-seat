package com.example.cinemaseat.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cinemaseat.model.CinemaLayoutGenerator
import com.example.cinemaseat.model.Seat
import com.example.cinemaseat.model.SeatType
import com.example.cinemaseat.ui.state.CinemaUiState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BottomSheet(
    uiState: CinemaUiState,
    onRemoveSelectedSeat: (String) -> Unit,
    onClearAllSelectedSeats: () -> Unit,
    onFocusOnSeat: (String) -> Unit,
    modifier: Modifier = Modifier,
    initialExpanded: Boolean = false
) {
    val baseColor = Color(0xFFD82D8B)
    var isExpanded by rememberSaveable { mutableStateOf(initialExpanded) }
    val selectedSeats = uiState.selectedSeats
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val maxListHeight = if (isLandscape) 140.dp else 220.dp

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(color = Color(0xFFE5E7EB))

            LegendRow(modifier = Modifier.padding(vertical = 10.dp))

            HorizontalDivider(color = Color(0xFFF1F5F9))

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFAFAFA))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Chi tiết ghế đang chọn",
                            color = Color(0xFF4B5563),
                            fontWeight = FontWeight.SemiBold
                        )
                        if (selectedSeats.isNotEmpty()) {
                            TextButton(onClick = onClearAllSelectedSeats) {
                                Text(
                                    text = "Xóa tất cả",
                                    color = baseColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    if (selectedSeats.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Chưa có ghế nào được chọn.",
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = maxListHeight),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(selectedSeats, key = { it.id }) { seat ->
                                SelectedSeatRow(
                                    seat = seat,
                                    isHighlighted = seat.id == uiState.highlightedSeatId,
                                    onLocate = { onFocusOnSeat(seat.id) },
                                    onRemove = { onRemoveSelectedSeat(seat.id) }
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = Color(0xFFE5E7EB))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (selectedSeats.isEmpty()) {
                                "Ghế đã chọn (0)"
                            } else {
                                "Ghế đã chọn (${selectedSeats.size})"
                            },
                            color = Color(0xFF111827),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (isExpanded) {
                                Icons.Default.KeyboardArrowDown
                            } else {
                                Icons.Default.KeyboardArrowUp
                            },
                            contentDescription = "Mở rộng / Thu gọn",
                            tint = baseColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    val previewText = if (selectedSeats.isEmpty()) {
                        "Chạm vào ghế trống để đặt vé"
                    } else {
                        selectedSeats.joinToString(", ") { it.code }
                    }
                    Text(
                        text = previewText,
                        color = Color(0xFF6B7280),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Tạm tính",
                        color = Color(0xFF6B7280)
                    )
                    Text(
                        text = formatVnd(uiState.totalPrice),
                        color = baseColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendRow(modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item(key = "legend_booked") {
            LegendItem(color = Color(0xFFDBEAFE), label = "Đã đặt")
        }
        item(key = "legend_selected") {
            LegendItem(color = Color(0xFFD82D8B), label = "Ghế bạn chọn")
        }
        item(key = "legend_unavailable") {
            LegendItem(color = Color(0xFFF3F4F6), label = "Không khả dụng")
        }
        items(SeatType.entries, key = { "legend_${it.name}" }) { type ->
            val fillColor = when (type) {
                SeatType.STANDARD -> Color(0xFFEDE2FF)
                SeatType.VIP -> Color(0xFFFCD5CE)
                SeatType.COUPLE -> Color(0xFFFCE2F0)
            }
            LegendItem(color = fillColor, label = type.label)
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, shape = RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF374151)
        )
    }
}

@Composable
private fun SelectedSeatRow(
    seat: Seat,
    isHighlighted: Boolean,
    onLocate: () -> Unit,
    onRemove: () -> Unit
) {
    val (badgeBg, badgeText) = when (seat.type) {
        SeatType.STANDARD -> Color(0xFFEDE2FF) to Color(0xFF7B3FE4)
        SeatType.VIP -> Color(0xFFFCD5CE) to Color(0xFFC94A38)
        SeatType.COUPLE -> Color(0xFFFCE2F0) to Color(0xFFC2185B)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLocate() },
        shape = RoundedCornerShape(10.dp),
        color = if (isHighlighted) Color(0xFFFDF2F8) else Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 52.dp, height = 32.dp)
                    .background(badgeBg, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = seat.code,
                    color = badgeText,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = seat.type.label,
                    color = Color(0xFF111827),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${seat.zone.label} • ${formatVnd(seat.price)}",
                    color = Color(0xFF6B7280),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Xóa ghế",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun formatVnd(amount: Int): String {
    val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
    return "${formatter.format(amount)}đ"
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetPreview() {
    val hall = remember { CinemaLayoutGenerator.createSampleHall() }
    BottomSheet(
        uiState = CinemaUiState(
            hallLayout = hall,
            selectedSeatIds = setOf("A3", "E5", "S1-2"),
            highlightedSeatId = "E5"
        ),
        onRemoveSelectedSeat = {},
        onClearAllSelectedSeats = {},
        onFocusOnSeat = {},
        initialExpanded = true
    )
}
