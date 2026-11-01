package com.blockstream.compose.screens.send

import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues
import com.blockstream.compose.components.GreenPagedColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blockstream.compose.models.send.SendChooseAccountViewModelAbstract
import com.blockstream.compose.models.send.SendChooseAccountViewModelPreview
import com.blockstream.compose.GreenPreview
import com.blockstream.compose.components.GreenAccountAsset
import com.blockstream.compose.utils.SetupScreen
import com.blockstream.compose.components.GreenColumn
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SendChooseAccountScreen(
    viewModel: SendChooseAccountViewModelAbstract
) {

    SetupScreen(
        viewModel = viewModel,
        withPadding = false,
    ) {
        val accounts by viewModel.accountsState.collectAsStateWithLifecycle()

        GreenPagedColumn(space = 4, contentPadding = PaddingValues(start = 20.dp, end = 12.dp, top = 16.dp)) {
            accounts.forEach { accountState ->
                GreenAccountAsset(
                    accountAssetBalance = accountState.account,
                    session = viewModel.sessionOrNull,
                    helperText = accountState.message,
                    isError = accountState.isError,
                    onClick = {
                        viewModel.selectAccount(accountState.account)
                    }.takeIf { !accountState.isError }
                )
            }
        }
    }
}

@Composable
@Preview
fun SendChooseAccountScreenPreview() {
    GreenPreview {
        SendChooseAccountScreen(viewModel = SendChooseAccountViewModelPreview.preview())
    }
}

