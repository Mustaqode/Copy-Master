package dev.mustaq.clipboard.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import dev.mustaq.clipboard.R
import dev.mustaq.clipboard.db.changeAppFirstOpenState
import dev.mustaq.clipboard.db.isAppAlreadyOpened
import dev.mustaq.clipboard.helper.moveToNewActivity

/**
 * Created by Mustaq Sameer on 15/04/20
 */
class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        moveToHomeScreen()
    }

    private fun moveToHomeScreen() {
        Handler().postDelayed({
            moveToNewActivity(HomeActivity::class.java, flags = Intent.FLAG_ACTIVITY_CLEAR_TOP)
            finish()
        }, 1000)
    }
}