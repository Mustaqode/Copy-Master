package dev.mustaq.clipboard

import android.app.*
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import dev.mustaq.clipboard.db.Clip
import dev.mustaq.clipboard.db.DbHelper

class CopyService : Service() {

    private val clipboardManager by lazy { getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
    private val dbHelper by lazy { DbHelper(this, null) }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val mainIntent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, mainIntent, 0)
        val notification = createNotification(pendingIntent)
        startForeground(FOREGROUND_NOTIFICATION_ID, notification)
        saveCopiedTextToDb()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(intent: PendingIntent): Notification {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle("Easy Clipboard is Active")
            setContentText("Never miss a copied content anymore!")
            setContentIntent(intent)
        }
        return notification.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val clipboardServiceChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(
                clipboardServiceChannel
            )
        }
    }

    private fun saveCopiedTextToDb() {
        clipboardManager.addPrimaryClipChangedListener {
            val text = clipboardManager.primaryClip?.getItemAt(0)?.text
            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
            dbHelper.insertRow(Clip(content = text.toString()))
        }
    }

    companion object {
        private const val FOREGROUND_NOTIFICATION_ID = 1
        const val CHANNEL_ID = "1001"
        const val CHANNEL_NAME = "Clipboard Service Channel"
    }
}