package com.blockstream.green.data

import android.app.Activity
import android.content.res.Configuration
import com.blockstream.data.CountlyBase
import com.blockstream.data.config.AppInfo
import com.blockstream.data.database.Database
import com.blockstream.data.di.ApplicationScope
import com.blockstream.data.managers.SettingsManager

abstract class CountlyAndroid constructor(
    appInfo: AppInfo,
    applicationScope: ApplicationScope,
    settingsManager: SettingsManager,
    database: Database,
) : CountlyBase(appInfo, applicationScope, settingsManager, database) {
    abstract fun onStart(activity: Activity)

    abstract fun onStop()

    abstract fun onConfigurationChanged(newConfig: Configuration)
}
