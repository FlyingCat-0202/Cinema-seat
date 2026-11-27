package com.example.cinemaseat.ui.components

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
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

    val density = LocalDensity.current
    val containerSize = LocalWindowInfo.current.containerSize
    val screenWidth = with(density) { containerSize.width.toDp() }
    val screenHeight = with(density) { containerSize.height.toDp() }
    val isLandscape = screenWidth > screenHeight

    val maxListHeight = if (isLandscape) {
        (screenHeight * 0.24f).coerceIn(84.dp, 140.dp)
    } else {
        (screenHeight * 0.26f).coerceIn(130.dp, 260.dp)
    }
    val horizontalPad = if (screenWidth < 360.dp) 12.dp else 16.dp
    val summaryVerticalPad = if (isLandscape) 8.dp else 10.dp

    var showDialog by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(color = Color(0xFFE5E7EB))

            LegendRow(modifier = Modifier.padding(vertical = if (isLandscape) 6.dp else 8.dp))

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
                            .padding(horizontal = horizontalPad, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Chi tiết ghế đang chọn",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFF4B5563),
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
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
                                .height(64.dp)
                                .padding(horizontal = horizontalPad),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Chưa có ghế nào được chọn.",
                                style = MaterialTheme.typography.bodySmall,
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
                    .padding(horizontal = horizontalPad, vertical = summaryVerticalPad),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Ghế đã chọn (${selectedSeats.size})",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xFF111827),
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7280),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Tạm tính",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF6B7280)
                    )
                    Text(
                        text = formatVnd(uiState.totalPrice),
                        style = MaterialTheme.typography.titleMedium,
                        color = baseColor,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            ContinueButton(
                enabled = selectedSeats.isNotEmpty(),
                isCompact = isLandscape,
                horizontalPad = horizontalPad,
                onClick = { showDialog = true }
            )
        }
    }

    if (showDialog) {
        val seatCodes = selectedSeats.joinToString(", ") { it.code }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = "Xác nhận đặt vé")
            },
            text = {
                Text(
                    text = "Ghế đã chọn (${selectedSeats.size}): $seatCodes\nTổng tiền: ${formatVnd(uiState.totalPrice)}"
                )
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Đóng")
                }
            }
        )
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
            color = Color(0xFF374151),
            maxLines = 1
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
            // Dùng widthIn(min) + padding để mã dài như "S17-18" hoặc chữ phóng to không bị cắt
            Box(
                modifier = Modifier
                    .widthIn(min = 56.dp)
                    .heightIn(min = 32.dp)
                    .background(badgeBg, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = seat.code,
                    color = badgeText,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = seat.type.label,
                    color = Color(0xFF111827),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${seat.zone.label} • ${formatVnd(seat.price)}",
                    color = Color(0xFF6B7280),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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

@Composable
private fun ContinueButton(
    enabled: Boolean,
    isCompact: Boolean,
    horizontalPad: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val buttonHeight = if (isCompact) 40.dp else 46.dp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                start = horizontalPad,
                end = horizontalPad,
                top = 2.dp,
                bottom = if (isCompact) 6.dp else 10.dp
            )
    ) {
        Surface(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight),
            color = if (enabled) Color(0xFFD82D8B) else Color(0xFFF3B8D8),
            shape = RoundedCornerShape(10.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tiếp tục",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
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
            selectedSeatIds = setOf("A3", "E5", "S17-18"),
            highlightedSeatId = "E5"
        ),
        onRemoveSelectedSeat = {},
        onClearAllSelectedSeats = {},
        onFocusOnSeat = {},
        initialExpanded = true
    )
}
