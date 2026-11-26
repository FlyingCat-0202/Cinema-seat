package com.example.cinemaseat.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cinemaseat.model.CinemaLayoutGenerator
import com.example.cinemaseat.model.SeatStatus
import com.example.cinemaseat.model.SeatType
import com.example.cinemaseat.ui.state.CinemaUiState

@Composable
fun FilterHeader(
    hasActiveFilter: Boolean,
    isFilterExpanded: Boolean,
    onToggleFilterPanel: () -> Unit,
    onResetView: () -> Unit,
    modifier: Modifier = Modifier
) {
    val baseColor = Color(0xFFD82D8B)
    val headerBg = Color(0xFFFDE8F3)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(headerBg)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.75f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Color(0xFF1F2937),
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "Rạp chiếu phim HCMUS",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF1F2937),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        //  Icon Filter | Icon Reset View
        Surface(
            shape = RoundedCornerShape(50),
            color = Color.White.copy(alpha = 0.85f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onToggleFilterPanel() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = "Bộ lọc",
                        tint = if (isFilterExpanded || hasActiveFilter) {
                            baseColor
                        } else {
                            Color(0xFF1F2937)
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(18.dp)
                        .background(Color(0xFFD1D5DB))
                )

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onResetView() }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Autorenew,
                        contentDescription = "Reset View",
                        tint = Color(0xFF1F2937),
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer(scaleX = -1f)
                    )
                }
            }
        }
    }
}

@Composable
fun FilterDropdownOverlay(
    uiState: CinemaUiState,
    isFilterExpanded: Boolean,
    onToggleTypeFilter: (SeatType) -> Unit,
    onToggleStatusFilter: (SeatStatus) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val baseColor = Color(0xFFD82D8B)

    Column(modifier = modifier.fillMaxWidth()) {
        AnimatedVisibility(
            visible = isFilterExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.96f)
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (uiState.hasActiveFilter) {
                        item(key = "clear_filter") {
                            Surface(
                                onClick = onClearFilters,
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFFCE7F3),
                                border = BorderStroke(1.dp, baseColor.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Xóa lọc",
                                        tint = baseColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Xóa lọc",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = baseColor,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    items(SeatType.entries, key = { "type_${it.name}" }) { type ->
                        val selected = uiState.selectedTypeFilter == type
                        FilterChip(
                            selected = selected,
                            onClick = { onToggleTypeFilter(type) },
                            label = { Text(type.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color(0xFFF3F4F6),
                                labelColor = Color(0xFF374151),
                                selectedContainerColor = baseColor,
                                selectedLabelColor = Color.White
                            ),
                            border = null
                        )
                    }

                    items(SeatStatus.entries, key = { "status_${it.name}" }) { status ->
                        val selected = uiState.selectedStatusFilter == status
                        FilterChip(
                            selected = selected,
                            onClick = { onToggleStatusFilter(status) },
                            label = { Text(status.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color(0xFFF3F4F6),
                                labelColor = Color(0xFF374151),
                                selectedContainerColor = Color(0xFF7B3FE4),
                                selectedLabelColor = Color.White
                            ),
                            border = null
                        )
                    }
                }
            }
        }

        // Thông báo khi lọc không có kết quả phù hợp
        if (uiState.hasActiveFilter && uiState.matchingSeatCount == 0) {
            Surface(
                color = Color(0xFFFEF2F2).copy(alpha = 0.96f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Không có ghế phù hợp với điều kiện lọc.",
                        color = Color(0xFFB91C1C),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onClearFilters) {
                        Text("Xóa lọc", color = baseColor, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FilterBarPreview() {
    val hall = remember { CinemaLayoutGenerator.createSampleHall() }
    Column {
        FilterHeader(
            hasActiveFilter = true,
            isFilterExpanded = true,
            onToggleFilterPanel = {},
            onResetView = {}
        )
        FilterDropdownOverlay(
            uiState = CinemaUiState(
                hallLayout = hall,
                selectedTypeFilter = SeatType.VIP
            ),
            isFilterExpanded = true,
            onToggleTypeFilter = {},
            onToggleStatusFilter = {},
            onClearFilters = {}
        )
    }
}
