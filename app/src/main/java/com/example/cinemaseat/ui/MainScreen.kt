package com.example.cinemaseat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cinemaseat.ui.components.CinemaCanvas
import com.example.cinemaseat.ui.components.FilterDropdownOverlay
import com.example.cinemaseat.ui.components.FilterHeader
import com.example.cinemaseat.ui.state.CinemaViewModel
import com.example.cinemaseat.ui.theme.CinemaSeatTheme

@Composable
fun CinemaMainScreen(
    viewModel: CinemaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var resetTrigger by remember { mutableIntStateOf(0) }
    var isFilterExpanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        FilterHeader(
            hasActiveFilter = uiState.hasActiveFilter,
            isFilterExpanded = isFilterExpanded,
            onToggleFilterPanel = { isFilterExpanded = !isFilterExpanded },
            onResetView = { resetTrigger++ }
        )

        Box(modifier = Modifier.weight(1f)) {
            CinemaCanvas(
                uiState = uiState,
                resetTrigger = resetTrigger,
                onSeatTap = viewModel::onSeatTapped,
                modifier = Modifier.fillMaxSize()
            )

            FilterDropdownOverlay(
                uiState = uiState,
                isFilterExpanded = isFilterExpanded,
                onToggleTypeFilter = viewModel::toggleTypeFilter,
                onToggleStatusFilter = viewModel::toggleStatusFilter,
                onClearFilters = viewModel::clearFilters,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
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
