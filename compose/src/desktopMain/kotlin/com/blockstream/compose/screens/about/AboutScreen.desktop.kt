package com.blockstream.compose.screens.about

import androidx.compose.runtime.Composable
import com.blockstream.compose.models.about.AboutViewModelAbstract

@Composable
actual fun AboutScreen(viewModel: AboutViewModelAbstract) = AboutRowsScreen(viewModel)
