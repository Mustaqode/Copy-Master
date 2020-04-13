package dev.mustaq.clipboard.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import dev.mustaq.clipboard.R
import kotlinx.android.synthetic.main.activity_home.*

/**
 * Created by Mustaq Sameer on 13/04/20
 */

@SuppressLint("InflateParams", "NewApi")
fun Activity.showAlertDialog(
    message: String? = null,
    positive: () -> Unit,
    negative: (() -> Unit)? = null
) {
    uiClHomeLayout.foreground.alpha = 220
    val dialogBuilder = AlertDialog.Builder(this)
    val view = layoutInflater.inflate(R.layout.dialog_custom_alert, null)
    val positiveButton = view.findViewById(R.id.uiBtnPositive) as AppCompatButton
    val negativeButton = view.findViewById(R.id.uiBtnNegative) as AppCompatButton
    val alertMessage = view.findViewById(R.id.uiTvAlertMessage) as TextView
    dialogBuilder.setView(view)
    val dialog = dialogBuilder.create()
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.show()
    if (!message.isNullOrEmpty()) {
        alertMessage.text = message.toString()
    }
    positiveButton.setOnClickListener {
        positive.invoke()
        dialog.cancel()
        uiClHomeLayout.foreground.alpha = 0
    }
    negativeButton.setOnClickListener {
        if (negative != null) {
            negative.invoke()
            uiClHomeLayout.foreground.alpha = 0
        } else {
            dialog.cancel()
            uiClHomeLayout.foreground.alpha = 0
        }
    }
}