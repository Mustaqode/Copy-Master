package dev.mustaq.clipboard.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dev.mustaq.clipboard.BuildConfig
import dev.mustaq.clipboard.R
import dev.mustaq.clipboard.helper.moveToNewActivity
import kotlinx.android.synthetic.main.activity_about.*

/**
 * Created by Mustaq Sameer on 15/04/20
 */
class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setupUi()
        setListeners()
    }

    private fun setupUi() {
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.str_license_information))
        uiTvVersion.text = BuildConfig.VERSION_NAME
    }

    private fun setListeners() {
        uiToolbar.setNavigationOnClickListener { onBackPressed() }
        uiTvLibrariesLink.setOnClickListener {
            moveToNewActivity(OssLicensesMenuActivity::class.java)
        }
        uiIvEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:$DEV_MAIL") // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, arrayListOf("devmustaq@gmail.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.str_feedback))
            if (intent.resolveActivity(this.packageManager) != null) {
                startActivity(intent)
            }
        }
    }

    companion object {
        private const val DEV_MAIL = "devmustaq@gmail.com"
    }
}