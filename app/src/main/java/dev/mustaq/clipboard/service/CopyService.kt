package dev.mustaq.clipboard.service

import android.app.*
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import dev.mustaq.clipboard.ui.HomeActivity
import dev.mustaq.clipboard.R
import dev.mustaq.clipboard.db.*

class CopyService : Service() {

    private val clipboardManager by lazy { getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(FOREGROUND_NOTIFICATION_ID, createClipboardNotification())
        saveCopiedTextToDb()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createClipboardNotification(): Notification {
        val homeIntent = Intent(this, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, homeIntent, 0)
        val remoteView = RemoteViews(
            this.packageName,
            R.layout.model_service_notification
        )
        createNotificationChannel()
        return createNotification(pendingIntent, remoteView)
    }

    private fun createNotification(intent: PendingIntent, remoteView: RemoteViews): Notification {
        val notification = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setCustomContentView(remoteView)
            .setSmallIcon(R.drawable.ic_clip)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            notification.setCategory(Notification.CATEGORY_SERVICE)
        else notification.setSound(null)
        return notification.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val clipboardServiceChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(
                clipboardServiceChannel
            )
        }
    }

    private fun saveCopiedTextToDb() {
        clipboardManager.addPrimaryClipChangedListener {
            val text = clipboardManager.primaryClip?.getItemAt(0)?.text
            val clip = ClipModel(copiedText = text.toString())
            addCopiedTextToDb(clip)
            addTriggerObject()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private const val FOREGROUND_NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "CH_01"
        const val CHANNEL_NAME = "Clipboard Service Channel"
    }
}