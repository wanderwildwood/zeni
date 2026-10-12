@file:OptIn(ExperimentalUuidApi::class)

package com.blockstream.data.extensions

import com.blockstream.data.managers.DeviceManager.Companion.JADE
import com.juul.kable.PeripheralBuilder
import com.juul.kable.PlatformAdvertisement
import com.juul.kable.ScannerBuilder
import kotlin.uuid.ExperimentalUuidApi

expect fun ScannerBuilder.conflate()

expect fun PeripheralBuilder.setupJade(mtu: Int)

expect fun PlatformAdvertisement.isBonded(): Boolean

// zeni: the Jade's service is the generic Nordic UART UUID, which other radios advertise too
// (a MeshCore node was listed as a Jade), so the name has to say Jade as well.
val PlatformAdvertisement.isJade
    get() = uuids.firstOrNull()?.toString() == JADE && name?.startsWith("Jade", ignoreCase = true) == true