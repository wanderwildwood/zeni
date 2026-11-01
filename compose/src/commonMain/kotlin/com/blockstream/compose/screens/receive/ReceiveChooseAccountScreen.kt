package com.blockstream.compose.screens.receive

import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues
import com.blockstream.compose.components.GreenPagedColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.blockstream.compose.models.receive.ReceiveChooseAccountViewModelAbstract
import com.blockstream.compose.models.receive.ReceiveChooseAccountViewModelPreview
import com.blockstream.compose.GreenPreview
import com.blockstream.compose.components.GreenAccountAsset
import com.blockstream.compose.utils.SetupScreen
import com.blockstream.compose.components.GreenColumn
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ReceiveChooseAccountScreen(
    viewModel: ReceiveChooseAccountViewModelAbstract
) {

    SetupScreen(
        viewModel = viewModel,
        withPadding = false,
    ) {
        val accounts = viewModel.accounts

        GreenPagedColumn(space = 4, contentPadding = PaddingValues(start = 20.dp, end = 12.dp, top = 16.dp)) {
            accounts.forEach { account ->
                GreenAccountAsset(
                    accountAssetBalance = account.accountAssetBalance,
                    session = viewModel.sessionOrNull,
                ) {
                    viewModel.selectAccount(account)
                }
            }
        }
    }
}

@Composable
@Preview
fun ReceiveChooseAccountScreenPreview() {
    GreenPreview {
        ReceiveChooseAccountScreen(viewModel = ReceiveChooseAccountViewModelPreview.preview())
    }
}

