package com.example.cinemaseat.ui.components

import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import com.example.cinemaseat.R
import com.example.cinemaseat.model.CinemaLayoutGenerator
import com.example.cinemaseat.model.Seat
import com.example.cinemaseat.model.SeatStatus
import com.example.cinemaseat.model.SeatType
import com.example.cinemaseat.ui.state.CinemaUiState
import kotlinx.coroutines.launch

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
    val screenStroke = remember { Stroke(width = 10f, cap = StrokeCap.Round) }

    val screenPath = remember(hall) {
        Path().apply {
            val screen = hall.screenBounds
            moveTo(screen.left, screen.bottom)
            quadraticTo(screen.center.x, screen.top - 10f, screen.right, screen.bottom)
        }
    }

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
    val entranceTextStyle = remember(density) {
        val fontSp = with(density) { 18f.toSp() }
        TextStyle(
            color = Color(0xFF64748B),
            fontSize = fontSp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = with(density) { 1.5f.toSp() }
        )
    }

    val screenLabel = remember(textMeasurer, screenTextStyle) {
        textMeasurer.measure("MÀN HÌNH", style = screenTextStyle)
    }
    val exitText = remember(textMeasurer, entranceTextStyle) {
        textMeasurer.measure("LỐI RA", style = entranceTextStyle)
    }
    val entryText = remember(textMeasurer, entranceTextStyle) {
        textMeasurer.measure("LỐI VÀO", style = entranceTextStyle)
    }
    val aisleText = remember(textMeasurer, entranceTextStyle) {
        textMeasurer.measure("LỐI ĐI", style = entranceTextStyle)
    }
    val seatTextLayouts = remember(hall, textMeasurer, seatTextStyle) {
        hall.seats.associate { seat ->
            seat.id to textMeasurer.measure(text = seat.code, style = seatTextStyle)
        }
    }

    val monoarrow = rememberVectorPainter(ImageVector.vectorResource(R.drawable.mono_direction_arrow))
    val biarrow = rememberVectorPainter(ImageVector.vectorResource(R.drawable.bidirection_arrow))

    val currentUiState by rememberUpdatedState(uiState)
    val currentOnSeatTap by rememberUpdatedState(onSeatTap)

    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var fitScale by remember { mutableFloatStateOf(1f) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var initialized by remember { mutableStateOf(false) }

    fun clampOffset(candidate: Offset, targetScale: Float, size: IntSize): Offset {
        if (size.width <= 0 || size.height <= 0) return candidate
        val scaledW = hall.worldWidth * targetScale
        val scaledH = hall.worldHeight * targetScale
        val marginX = size.width * 0.25f
        val marginY = size.height * 0.25f

        val minX = size.width - scaledW - marginX
        val maxX = marginX
        val minY = size.height - scaledH - marginY
        val maxY = marginY

        return Offset(
            x = candidate.x.coerceIn(minOf(minX, maxX), maxOf(minX, maxX)),
            y = candidate.y.coerceIn(minOf(minY, maxY), maxOf(minY, maxY))
        )
    }

    LaunchedEffect(resetTrigger) {
        if (resetTrigger > 0 && canvasSize.width > 0 && canvasSize.height > 0) {
            val targetScale = minOf(
                canvasSize.width / hall.worldWidth,
                canvasSize.height / hall.worldHeight
            ) * 0.96f
            val targetOffset = Offset(
                x = (canvasSize.width - hall.worldWidth * targetScale) / 2f,
                y = (canvasSize.height - hall.worldHeight * targetScale) / 2f
            )
            val startScale = scale
            val startOffset = offset
            launch {
                animate(
                    initialValue = startScale,
                    targetValue = targetScale,
                    animationSpec = tween(350)
                ) { value, _ -> scale = value }
            }
            launch {
                animate(
                    typeConverter = Offset.VectorConverter,
                    initialValue = startOffset,
                    targetValue = targetOffset,
                    animationSpec = tween(350)
                ) { value, _ -> offset = value }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .clipToBounds()
            .onSizeChanged { newSize ->
                canvasSize = newSize
                if (newSize.width > 0 && newSize.height > 0) {
                    fitScale = minOf(
                        newSize.width / hall.worldWidth,
                        newSize.height / hall.worldHeight
                    ) * 0.96f
                    if (!initialized) {
                        scale = fitScale
                        offset = Offset(
                            x = (newSize.width - hall.worldWidth * fitScale) / 2f,
                            y = (newSize.height - hall.worldHeight * fitScale) / 2f
                        )
                        initialized = true
                    } else {
                        offset = clampOffset(offset, scale, newSize)
                    }
                }
            }
            // Pan (kéo 1 ngón) + Pinch-to-Zoom (2 ngón quanh tâm centroid)
            .pointerInput(hall) {
                detectTransformGestures { centroid, pan, zoom, _ ->
                    if (canvasSize.width <= 0 || canvasSize.height <= 0) return@detectTransformGestures
                    val minScale = (fitScale * 0.75f).coerceAtLeast(0.2f)
                    val maxScale = (fitScale * 3.5f).coerceAtLeast(2.0f)

                    val oldScale = scale
                    val newScale = (oldScale * zoom).coerceIn(minScale, maxScale)
                    val zoomRatio = newScale / oldScale

                    val rawOffset = centroid - (centroid - offset) * zoomRatio + pan
                    scale = newScale
                    offset = clampOffset(rawOffset, newScale, canvasSize)
                }
            }
            // Tap Hit-testing: chạm chọn ghế chuẩn xác sau khi Pan hoặc Zoom
            .pointerInput(hall) {
                detectTapGestures { tapOffset ->
                    val worldPoint = Offset(
                        x = (tapOffset.x - offset.x) / scale,
                        y = (tapOffset.y - offset.y) / scale
                    )
                    val hitSeat = currentUiState.hallLayout.seats.firstOrNull { seat ->
                        seat.bounds.contains(worldPoint)
                    }
                    if (hitSeat != null) {
                        currentOnSeatTap(hitSeat)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            withTransform({
                translate(left = offset.x, top = offset.y)
                scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero)
            }) {
                val screen = hall.screenBounds
                drawPath(
                    path = screenPath,
                    color = Color(0xFFD82D8B),
                    style = screenStroke
                )
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
                    val alpha = if (uiState.matchesFilter(seat)) 1f else 0.2f

                    drawRoundRect(
                        color = fillColor.copy(alpha = alpha),
                        topLeft = seat.bounds.topLeft,
                        size = seat.bounds.size,
                        cornerRadius = seatCorner
                    )

                    val textResult = seatTextLayouts[seat.id] ?: continue
                    drawText(
                        textLayoutResult = textResult,
                        color = textColor.copy(alpha = alpha),
                        topLeft = Offset(
                            x = seat.bounds.center.x - textResult.size.width / 2f,
                            y = seat.bounds.center.y - textResult.size.height / 2f
                        )
                    )
                }

                for (entrance in hall.entrances) {
                    if (entrance.label == "RA / VÀO") {
                        val arrowW = 80f
                        val arrowH = 40f

                        // 1. Mũi tên đơn trái + chữ "LỐI RA" phía trên
                        val leftArrowX = entrance.bounds.left + arrowW
                        translate(left = leftArrowX, top = entrance.bounds.top) {
                            with(monoarrow) {
                                draw(size = Size(arrowW, arrowH))
                            }
                        }

                        drawText(
                            textLayoutResult = exitText,
                            topLeft = Offset(
                                x = leftArrowX + arrowW / 2f - exitText.size.width / 2f,
                                y = entrance.bounds.top - exitText.size.height + 2f
                            )
                        )

                        // 2. Mũi tên đơn phải + chữ "LỐI VÀO" phía trên
                        val rightArrowX = entrance.bounds.right - arrowW * 2
                        translate(left = rightArrowX, top = entrance.bounds.top) {
                            with(monoarrow) {
                                draw(size = Size(arrowW, arrowH))
                            }
                        }

                        drawText(
                            textLayoutResult = entryText,
                            topLeft = Offset(
                                x = rightArrowX + arrowW / 2f - entryText.size.width / 2f,
                                y = entrance.bounds.top - entryText.size.height + 2f
                            )
                        )
                    } else {
                        val arrowWidth = 35f
                        val arrowHeight = 600f

                        translate(
                            left = entrance.bounds.center.x - arrowWidth / 2f,
                            top = entrance.bounds.center.y - arrowHeight / 2f
                        ) {
                            with(biarrow) {
                                draw(size = Size(arrowWidth, arrowHeight))
                            }
                        }

                        val isLeftAisle = entrance.label == "LỐI ĐI TRÁI"
                        val rotationAngle = if (isLeftAisle) -90f else 90f
                        val sideOffset = if (isLeftAisle) -24f else 24f
                        val labelCenter = Offset(
                            x = entrance.bounds.center.x + sideOffset,
                            y = entrance.bounds.center.y
                        )

                        rotate(degrees = rotationAngle, pivot = labelCenter) {
                            drawText(
                                textLayoutResult = aisleText,
                                topLeft = Offset(
                                    x = labelCenter.x - aisleText.size.width / 2f,
                                    y = labelCenter.y - aisleText.size.height / 2f
                                )
                            )
                        }
                    }
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
