package dev.mustaq.clipboard.helper

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.fragment.app.FragmentActivity

/**
 * Created by Mustaq Sameer on 08/04/20
 */

fun View.setVisibilityOnCondition(condition: Boolean) {
    visibility = if (condition) View.VISIBLE
    else View.INVISIBLE
}

fun <T : FragmentActivity> Activity.moveToNewActivity(
    clazz: Class<T>,
    requestCode: Int? = null,
    flags: Int? = null
) {
    val intent = Intent(this, clazz)
    if (flags != null)
        intent.flags = flags
    if (requestCode != null)
        startActivityForResult(intent, requestCode)
    else startActivity(intent)
}