package com.example.medilab.ui.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medilab.ui.component.MediLabButton
import com.example.medilab.ui.component.OnboardingPage
import com.example.medilab.ui.navigation.Route
import com.example.medilab.ui.navigation.SharedNavigationViewModel
import com.example.medilab.ui.theme.Spacing
import kotlinx.coroutines.launch

data class OnboardingPageContent(
    val title: String,
    val description: String,
    val illustration: @Composable () -> Unit
)

@Composable
fun OnboardingScreen(
    pages: List<OnboardingPageContent>,
    sharedNavigationViewModel: SharedNavigationViewModel = viewModel()
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { sharedNavigationViewModel.navigateTo(Route.Login.path) }) { Text("Lewati") }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                val currentPageContent = pages[pageIndex]
                OnboardingPage(
                    title = currentPageContent.title,
                    description = currentPageContent.description,
                    content = currentPageContent.illustration,
                    page = pagerState.currentPage,
                    totalPages = pages.size
                )
            }

            Spacer(Modifier.height(Spacing.lg))

            // Indicator and Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pagerState.pageCount) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (pagerState.currentPage == index) 10.dp else 8.dp)
                                .background(
                                    color = if (pagerState.currentPage == index)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outlineVariant,
                                    shape = CircleShape
                                )
                        )
                    }
                }

                MediLabButton(
                    text = if (pagerState.currentPage == pages.size - 1) "Mulai" else "Lanjut",
                    onClick = {
                        if (pagerState.currentPage == pages.size - 1) {
                            sharedNavigationViewModel.navigateTo(Route.Login.path)
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    }
                )
            }
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@Composable
fun OnboardingScreen(
    title: String,
    description: String,
    page: Int,
    totalPages: Int,
    illustration: @Composable () -> Unit,
    buttonText: String,
    sharedNavigationViewModel: SharedNavigationViewModel
) {
    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OnboardingPage(
                title = title,
                description = description,
                page = page,
                totalPages = totalPages,
                content = illustration,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
