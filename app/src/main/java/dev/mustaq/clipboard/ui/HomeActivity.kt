package dev.mustaq.clipboard.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dev.mustaq.clipboard.R
import dev.mustaq.clipboard.adapter.ClipsAdapter
import dev.mustaq.clipboard.db.ClipModel
import dev.mustaq.clipboard.db.DbManager
import dev.mustaq.clipboard.db.realmLiveData
import dev.mustaq.clipboard.helper.isServiceRunning
import dev.mustaq.clipboard.mapper.AnalyticsMapper
import dev.mustaq.clipboard.service.CopyService
import dev.mustaq.clipboard.ui_elements.ClipsDialogFragment
import kotlinx.android.synthetic.main.activity_main.*

class HomeActivity : AppCompatActivity() {

    private val linearLayoutManager by lazy { LinearLayoutManager(this) }
    private val clipsAdapter by lazy { ClipsAdapter(onItemClickListener, onLongTouchListener) }
    private val dbManager by lazy { DbManager() }
    private val clips: ArrayList<ClipModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(uiToolbar)
        setupUi()
        setListeners()
    }

    private fun setupUi() {
        dbManager.addTriggerObject()
        uiSwitchService.isChecked = isServiceRunning(CopyService::class.java)
        realmLiveData(dbManager.getTriggerObjectFromDb()){
            clips.clear()
            clips.addAll(dbManager.getAllClipsFromDb())
            clipsAdapter.setItems(clips)
            clipsAdapter.notifyDataSetChanged()
        }
        getUpdatedClipsFromDb()
        setupAdapter()
    }

    private fun setListeners() {
        uiSwitchService.setOnCheckedChangeListener { _, isChecked -> toggleService(isChecked) }
        uiIvInfo.setOnClickListener { makeToast("Offensive: ${AnalyticsMapper.map(clips).offensiveWords}" +
                "Links: ${AnalyticsMapper.map(clips).links}" +
                "UnsafeLinks: ${AnalyticsMapper.map(clips).unsafeLinks}" +
                "Total: ${AnalyticsMapper.map(clips).totalClips}")
        }
    }

    private fun toggleService(isChecked: Boolean) {
        if (isChecked) {
            startClipboardService()
            makeToast("Easy Copy is Active")
        } else {
            stopClipboardService()
            makeToast("Easy Copy is Stopped")
        }
    }

    private fun getUpdatedClipsFromDb() {
        clips.clear()
        clips.addAll(dbManager.getAllClipsFromDb())
        makeToast("${clips.size}")
    }

    private fun setupAdapter() {
        uiRecyclerView.adapter = clipsAdapter
        uiRecyclerView.layoutManager = linearLayoutManager
        clipsAdapter.setItems(clips)
        clipsAdapter.notifyDataSetChanged()
    }

    private fun startClipboardService() {
        val serviceIntent = Intent(this, CopyService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isServiceRunning(CopyService::class.java)) {
            startForegroundService(serviceIntent)
        }
    }

    private fun stopClipboardService() {
        val serviceIntent = Intent(this, CopyService::class.java)
        stopService(serviceIntent)
    }

    private val onItemClickListener: (String) -> Unit = {clip ->
        showClipDialog(clip)
    }

    private val onLongTouchListener: (String) -> Unit = {clip ->
        val clipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(CLIP_LABEL, clip)
        clipboardManager.setPrimaryClip(clipData)
        makeToast("Text Copied")
    }

    fun showClipDialog(clip: String) {
        ClipsDialogFragment.newInstance(clip).apply {
            show(supportFragmentManager, FRAGMENT_ID)
        }
    }

    private fun makeToast(text: String) =
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    companion object {
        private const val FRAGMENT_ID = "1001"
        private const val CLIP_LABEL = "clip.text"
    }

}
