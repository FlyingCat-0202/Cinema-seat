package com.example.cinemaseat.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cinemaseat.ui.components.CinemaCanvas
import com.example.cinemaseat.ui.state.CinemaViewModel
import com.example.cinemaseat.ui.theme.CinemaSeatTheme

@Composable
fun CinemaMainScreen(
    viewModel: CinemaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CinemaCanvas(
        uiState = uiState,
        resetTrigger = 0,
        onSeatTap = viewModel::onSeatTapped,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Màn hình chính (Dọc)")
@Composable
private fun CinemaMainScreenPortraitPreview() {
    CinemaSeatTheme {
        CinemaMainScreen()
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 380, name = "Màn hình chính (Ngang)")
@Composable
private fun CinemaMainScreenLandscapePreview() {
    CinemaSeatTheme {
        CinemaMainScreen()
    }
}
