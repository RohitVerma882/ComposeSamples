package com.composesamples.ui.screen

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.composesamples.R
import com.composesamples.data.model.AppInfo
import com.composesamples.data.repository.AppRepository
import com.composesamples.ui.viewmodel.FilterType
import com.composesamples.ui.viewmodel.InstalledAppsUiState
import com.composesamples.ui.viewmodel.InstalledAppsViewModel
import com.composesamples.ui.viewmodel.InstalledAppsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstalledAppsScreen(
    navController: NavController,
    appRepository: AppRepository,
    viewModel: InstalledAppsViewModel = viewModel(factory = InstalledAppsViewModelFactory(appRepository))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentFilter by viewModel.filterType.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            InstalledAppsTopAppBar(
                scrollBehavior = scrollBehavior,

                )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        InstalledAppsContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            currentFilter = currentFilter,
            uiState = uiState,
            onFilterSelected = { newFilter -> viewModel.setFilter(newFilter) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InstalledAppsTopAppBar(scrollBehavior: TopAppBarScrollBehavior) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        title = { Text(stringResource(R.string.sample_installed_apps_name)) }
    )
}

@Composable
private fun InstalledAppsContent(
    modifier: Modifier,
    currentFilter: FilterType,
    uiState: InstalledAppsUiState,
    onFilterSelected: (FilterType) -> Unit
) {
    Column(modifier = modifier) {
        FilterToggle(currentFilter) { newFilter ->
            onFilterSelected.invoke(newFilter)
        }
        HorizontalDivider()

        when (uiState) {
            is InstalledAppsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is InstalledAppsUiState.Success -> {
                AppList(
                    apps = uiState.apps
                )
            }
        }
    }
}


@Composable
fun FilterToggle(
    selectedFilter: FilterType,
    onFilterSelected: (FilterType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            FilterType.entries.forEachIndexed { index, filterType ->
                SegmentedButton(
                    selected = filterType == selectedFilter,
                    onClick = { onFilterSelected(filterType) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = FilterType.entries.size
                    ),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        text = when (filterType) {
                            FilterType.ALL_APPS -> "All"
                            FilterType.USER_APPS -> "User"
                            FilterType.SYSTEM_APPS -> "System"
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppList(
    apps: List<AppInfo>
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(
            items = apps,
            key = { it.packageName }
        ) { app ->
            AppItem(app) {

            }
        }
    }
}

@Composable
private fun AppItem(
    app: AppInfo,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val appIconPainter = if (app.icon is BitmapDrawable) {
                    BitmapPainter(app.icon.bitmap.asImageBitmap())
                } else {
                    rememberVectorPainter(Icons.Rounded.Android)
                }

                Image(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    painter = appIconPainter,
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = app.name
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                text = app.packageName
            )
        }
    }
}