package com.blockstream.data.btcpricehistory

import com.blockstream.data.btcpricehistory.datasource.BitcoinPriceHistoryRemoteDataSource
import org.koin.dsl.module

val btcPriceHistoryModule = module {
    single {
        BitcoinPriceHistoryRemoteDataSource(get(), get())
    }

    single {
        BitcoinPriceHistoryRepository(
            remoteDataSource = get()
        )
    }
}