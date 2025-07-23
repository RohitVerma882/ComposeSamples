package com.composesamples.ui.screen

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.composesamples.R
import com.composesamples.data.model.AppModel
import com.composesamples.data.repository.AppFilterType
import com.composesamples.data.repository.AppRepository
import com.composesamples.ui.viewmodel.InstalledAppsViewModel
import com.composesamples.ui.viewmodel.InstalledAppsViewModelFactory
import com.composesamples.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstalledAppsScreen(
    navController: NavController,
    appRepository: AppRepository,
    viewModel: InstalledAppsViewModel = viewModel(
        factory = InstalledAppsViewModelFactory(
            appRepository
        )
    )
) {
    val apps by viewModel.apps.collectAsStateWithLifecycle()
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
            apps = apps,
            onFilterSelected = { newFilter -> viewModel.setFilterType(newFilter) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InstalledAppsTopAppBar(scrollBehavior: TopAppBarScrollBehavior) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        title = { Text(stringResource(R.string.sample_installed_apps_name)) },
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
private fun InstalledAppsContent(
    modifier: Modifier,
    currentFilter: AppFilterType,
    apps: Resource<List<AppModel>>,
    onFilterSelected: (AppFilterType) -> Unit
) {
    Column(modifier = modifier) {
        AppsFilterToggle(currentFilter) { newFilter ->
            onFilterSelected.invoke(newFilter)
        }
        HorizontalDivider()

        when (apps) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is Resource.Success -> {
                AppList(
                    apps = apps.data!!
                )
            }

            else -> {}
        }
    }
}

@Composable
fun AppsFilterToggle(
    selectedFilter: AppFilterType,
    onFilterSelected: (AppFilterType) -> Unit
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
            AppFilterType.entries.forEachIndexed { index, filterType ->
                SegmentedButton(
                    selected = filterType == selectedFilter,
                    onClick = { onFilterSelected(filterType) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = AppFilterType.entries.size
                    ),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        text = stringResource(
                            when (filterType) {
                                AppFilterType.ALL -> R.string.sample_installed_apps_filter_all
                                AppFilterType.USER -> R.string.sample_installed_apps_filter_user
                                AppFilterType.SYSTEM -> R.string.sample_installed_apps_filter_system
                            }
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun AppList(
    apps: List<AppModel>
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
    app: AppModel,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
               Image(
                    modifier = Modifier.size(40.dp),
                    bitmap = app.icon.asImageBitmap(),
                    contentScale = ContentScale.Fit,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    text = app.name
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                text = app.packageName
            )
        }
    }
}