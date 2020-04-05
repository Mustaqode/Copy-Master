package dev.mustaq.clipboard.helper

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE

/**
 * Created by Mustaq Sameer on 04/04/20
 */

@Suppress("DEPRECATION") // Deprecated for third party Services.
fun <T> Context.isServiceRunning(service: Class<T>) =
    (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it.service.className == service.name }