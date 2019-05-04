package de.marcelknupfer.studentenausweisguthaben.ui.activities

/*
 * AutostartRegister.kt
 *
 * Copyright (C) 2014 Jakob Wenzel
 *
 * Authors:
 * Jakob Wenzel <jakobwenzel92@gmail.com> (original Java version)
 *  Marcel Knupfer (Kotlin version)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


import android.content.ComponentName
import android.content.pm.PackageManager
import android.util.Log

/**
 * register or unregister the app to be autostarted on nfc discovery
 */
object AutostartRegister {
    private val TAG = AutostartRegister::class.java.name
    internal fun register(pm: PackageManager, autostart: Boolean?) {
        Log.i(TAG, "Autostart is $autostart")
        val enabled = if (autostart?: false)
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        else
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED

        Log.i(TAG, "Setting to $enabled")
        pm.setComponentEnabledSetting(
            ComponentName(
                "de.marcelknupfer.studentenausweisguthaben",
                "de.marcelknupfer.studentenausweisguthaben.ActivityAlias"
            ),
            enabled,
            PackageManager.DONT_KILL_APP
        )
    }
}
