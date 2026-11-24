package com.example.cinemaseat.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.cinemaseat.model.CinemaLayoutGenerator
import com.example.cinemaseat.model.Seat
import com.example.cinemaseat.model.SeatStatus
import com.example.cinemaseat.model.SeatType
import com.example.cinemaseat.ui.state.CinemaUiState

@Composable
fun CinemaCanvas(
    uiState: CinemaUiState,
    resetTrigger: Int,
    onSeatTap: (Seat) -> Unit,
    modifier: Modifier = Modifier
) {
    val hall = uiState.hallLayout

    val density = LocalDensity.current
    val seatCorner = remember { CornerRadius(8f, 8f) }
    val borderStroke = remember { Stroke(width = 2.5f) }
    val textMeasurer = rememberTextMeasurer()
    val screenPath = remember { Path() }
    val screenStroke = remember { Stroke(width = 10f, cap = StrokeCap.Round) }

    val seatTextStyle = remember(density) {
        val fontSp = with(density) { 19f.toSp() }
        TextStyle(color = Color(0xFF7B3FE4), fontSize = fontSp, fontWeight = FontWeight.SemiBold)
    }
    val screenTextStyle = remember(density) {
        val fontSp = with(density) { 26f.toSp() }
        TextStyle(
            color = Color(0xFF8E8E93),
            fontSize = fontSp,
            fontWeight = FontWeight.Medium,
            letterSpacing = with(density) { 4f.toSp() }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val fitScale = minOf(
                size.width / hall.worldWidth,
                size.height / hall.worldHeight
            ) * 0.96f
            val offsetX = (size.width - hall.worldWidth * fitScale) / 2f
            val offsetY = (size.height - hall.worldHeight * fitScale) / 2f

            withTransform({
                translate(left = offsetX, top = offsetY)
                scale(scaleX = fitScale, scaleY = fitScale, pivot = Offset.Zero)
            }) {
                val screen = hall.screenBounds
                screenPath.reset()
                screenPath.moveTo(screen.left, screen.bottom)
                screenPath.quadraticTo(screen.center.x, screen.top - 10f, screen.right, screen.bottom)
                drawPath(
                    path = screenPath,
                    color = Color(0xFFD82D8B),
                    style = screenStroke
                )
                val screenLabel = textMeasurer.measure("MÀN HÌNH", style = screenTextStyle)
                drawText(
                    textLayoutResult = screenLabel,
                    topLeft = Offset(
                        x = screen.center.x - screenLabel.size.width / 2f,
                        y = screen.bottom - 14f
                    )
                )

                for (seat in hall.seats) {
                    val status = seat.effectiveStatus(uiState.selectedSeatIds)
                    val (fillColor, textColor) = when (status) {
                        SeatStatus.SELECTED -> Color(0xFFD82D8B) to Color.White
                        SeatStatus.BOOKED -> Color(0xFFDBEAFE) to Color(0xFF2563EB)
                        SeatStatus.UNAVAILABLE -> Color(0xFFF3F4F6) to Color(0xFFD1D5DB)
                        SeatStatus.AVAILABLE -> when (seat.type) {
                            SeatType.STANDARD -> Color(0xFFEDE2FF) to Color(0xFF7B3FE4)
                            SeatType.VIP -> Color(0xFFFCD5CE) to Color(0xFFC94A38)
                            SeatType.COUPLE -> Color(0xFFFCE2F0) to Color(0xFFC2185B)
                        }
                    }

                    drawRoundRect(
                        color = fillColor,
                        topLeft = seat.bounds.topLeft,
                        size = seat.bounds.size,
                        cornerRadius = seatCorner
                    )

                    val textResult = textMeasurer.measure(
                        text = seat.code,
                        style = seatTextStyle.copy(color = textColor)
                    )

                    drawText(
                        textLayoutResult = textResult,
                        topLeft = Offset(
                            x = seat.bounds.center.x - textResult.size.width / 2f,
                            y = seat.bounds.center.y - textResult.size.height / 2f
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 520)
@Composable
private fun CinemaCanvasPreview() {
    val hall = remember { CinemaLayoutGenerator.createSampleHall() }
    CinemaCanvas(
        uiState = CinemaUiState(hallLayout = hall, selectedSeatIds = setOf("E5", "E6")),
        resetTrigger = 0,
        onSeatTap = {}
    )
}
