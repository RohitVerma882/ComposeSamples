package com.composesamples.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.composesamples.AppConstants
import com.composesamples.R
import com.composesamples.data.model.SampleModel
import com.composesamples.data.repository.SampleRepository
import com.composesamples.ui.viewmodel.MainViewModel
import com.composesamples.ui.viewmodel.MainViewModelFactory
import com.composesamples.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    sampleRepository: SampleRepository,
    viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(sampleRepository))
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val uriHandler = LocalUriHandler.current
    val samples by viewModel.samples.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainTopAppBar(
                scrollBehavior = scrollBehavior,
                onShowInfo = { uriHandler.openUri(AppConstants.GITHUB_SOURCE_CODE_URL) }
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        MainContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            samples = samples,
            onSampleClick = { sample ->
                navController.navigate(sample.appRoute.route)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onShowInfo: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        actions = {
            IconButton(onClick = onShowInfo) {
                Icon(Icons.Outlined.Info, contentDescription = null)
            }
        },
        scrollBehavior = scrollBehavior,
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
    )
}

@Composable
private fun MainContent(
    modifier: Modifier,
    samples: Resource<List<SampleModel>>,
    onSampleClick: (SampleModel) -> Unit
) {
    Column(modifier = modifier) {
        when (samples) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is Resource.Success -> {
                SamplesList(
                    samples = samples.data!!,
                    onSampleClick = onSampleClick
                )
            }

            else -> {}
        }
    }
}

@Composable
private fun SamplesList(
    samples: List<SampleModel>,
    onSampleClick: (SampleModel) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(samples) { sample ->
            SampleItem(sample.nameId) {
                onSampleClick.invoke(sample)
            }
        }
    }
}

@Composable
private fun SampleItem(
    nameId: Int,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium,
            text = stringResource(nameId)
        )
    }
}