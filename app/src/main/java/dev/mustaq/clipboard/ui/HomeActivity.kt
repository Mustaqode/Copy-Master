package dev.mustaq.clipboard.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dev.mustaq.clipboard.R
import dev.mustaq.clipboard.application.ClipsAdapter
import dev.mustaq.clipboard.db.ClipModel
import dev.mustaq.clipboard.db.DbManager
import dev.mustaq.clipboard.service.CopyService
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*

class HomeActivity : AppCompatActivity() {

    private val copyService by lazy { CopyService() }
    private val linearLayoutManager by lazy { LinearLayoutManager(this) }
    private val clipsAdapter by lazy { ClipsAdapter() }
    private val dbManager by lazy { DbManager() }
    private val clips: ArrayList<ClipModel> = ArrayList()
    private var managedClips: RealmResults<ClipModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(uiToolbar)
        setupUi()
        setListeners()
    }


    private fun setupUi() {
        uiSwitchService.isChecked = copyService.isServiceRunning()
        getUpdatedClipsFromDb()
        setupAdapter()
    }

    private fun setListeners() {
        uiSwitchService.setOnCheckedChangeListener { _, isChecked -> toggleService(isChecked) }
    }

    private fun toggleService(isChecked: Boolean) {
        if (isChecked) {
            startClipboardService()
            Toast.makeText(this, "Easy Copy is Active", Toast.LENGTH_SHORT).show()
        } else {
            stopClipboardService()
            Toast.makeText(this, "Easy Copy is Stopped", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUpdatedClipsFromDb() {
        clips.clear()
        clips.addAll(dbManager.getAllClipsFromDb())
    }

    private fun setupAdapter() {
        uiRecyclerView.adapter = clipsAdapter
        uiRecyclerView.layoutManager = linearLayoutManager
        clipsAdapter.setItems(clips)
        clipsAdapter.notifyDataSetChanged()
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
