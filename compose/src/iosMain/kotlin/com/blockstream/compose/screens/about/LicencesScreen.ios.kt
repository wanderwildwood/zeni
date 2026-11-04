package com.blockstream.compose.screens.about

import androidx.compose.runtime.Composable
import com.blockstream.compose.models.about.LegalViewModel

/** Not shipped: the source and the app's own license. The Android page lists every library too. */
@Composable
actual fun LicencesScreen(viewModel: LegalViewModel) = SourceAndLicenceScreen(viewModel)
