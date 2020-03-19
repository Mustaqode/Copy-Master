package dev.mustaq.clipboard

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.mustaq.clipboard.db.DbHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val dbHelper by lazy { DbHelper(this, null) }
    private val copyService by lazy { CopyService() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startClipboardService()
        showClips()
    }

    override fun onDestroy() {
        super.onDestroy()
//        stopClipboardService()
    }

    private fun showClips() {
        uiTvCopiedText.text = dbHelper.showElementsTest()
    }

    private fun startClipboardService() {
        val serviceIntent = Intent(this, CopyService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !copyService.isRunning) {
            startForegroundService(serviceIntent)
        }
    }

    private fun stopClipboardService() {
        val serviceIntent = Intent(this, CopyService::class.java)
        stopService(serviceIntent)
    }
}
