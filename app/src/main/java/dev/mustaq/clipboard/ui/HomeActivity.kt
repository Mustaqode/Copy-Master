package dev.mustaq.clipboard.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mustaq.clipboard.R
import dev.mustaq.clipboard.adapter.ClipsAdapter
import dev.mustaq.clipboard.db.*
import dev.mustaq.clipboard.helper.isServiceRunning
import dev.mustaq.clipboard.mapper.AnalyticsMapper
import dev.mustaq.clipboard.service.CopyService
import dev.mustaq.clipboard.ui_elements.ClipsDialogFragment
import dev.mustaq.clipboard.helper.SwipeRecycleViewHelper
import kotlinx.android.synthetic.main.activity_main.*

class HomeActivity : AppCompatActivity() {

    private val linearLayoutManager by lazy { LinearLayoutManager(this) }
    private val clipsAdapter by lazy { ClipsAdapter(onItemClickListener, onLongTouchListener) }
    private val swipeRecycleViewHelper by lazy {
        SwipeRecycleViewHelper(
            this,
            ::onSwipeListener
        )
    }
    private val clips: ArrayList<ClipModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.uiToolbar)
        setSupportActionBar(toolbar)
        setupUi()
        setListeners()
    }

    private fun setupUi() {
        addTriggerObject() //To initiate trigger db
        uiSwitchService.isChecked = isServiceRunning(CopyService::class.java)
        setupRecyclerviewAdapter()
        addFreshDataToAdapter()
        setupAnalyticsPanel()
        realmLiveData(getTriggerObjectFromDb()) {
            addFreshDataToAdapter()
            setupAnalyticsPanel()
        }
    }

    private fun setListeners() {
        uiSwitchService.setOnCheckedChangeListener { _, isChecked -> toggleService(isChecked) }
        uiIvInfo.setOnClickListener {}
        uiTvDeleteAll.setOnClickListener { deleteAllFromDb() }
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

    private fun setupRecyclerviewAdapter() {
        uiRecyclerView.adapter = clipsAdapter
        uiRecyclerView.layoutManager = linearLayoutManager
        uiRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            ).apply {
                getDrawable(R.drawable.divider_grey)?.let { setDrawable(it) }
            })
        swipeRecycleViewHelper.attachToRecyclerView(uiRecyclerView)
    }

    private fun setupAnalyticsPanel() {
        val data = AnalyticsMapper.map(clips)
        uiTvTotalClips.text = data.totalClips.toString()
        uiTvOffensiveClips.text = data.offensiveWords.toString()
        uiTvLinks.text = data.links.toString()
        uiTvUnsafeLinks.text = data.links.toString()
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

    private val onItemClickListener: (String) -> Unit = { clip ->
        showClipDialog(clip)
    }

    private val onLongTouchListener: (String) -> Unit = { clip ->
        val clipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(CLIP_LABEL, clip)
        clipboardManager.setPrimaryClip(clipData)
        makeToast("Text Copied")
    }

    private fun onSwipeListener(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        when (direction) {
            ItemTouchHelper.LEFT -> {
                deleteClipFromDb(clips[position])
                addFreshDataToAdapter()
                setupAnalyticsPanel()
            }
            ItemTouchHelper.RIGHT -> {
                shareClip(clips[position].copiedText)
                clipsAdapter.notifyDataSetChanged()
                setupAnalyticsPanel()
            }
        }
    }

    private fun showClipDialog(clip: String) {
        ClipsDialogFragment.newInstance(clip).apply {
            show(supportFragmentManager, FRAGMENT_ID)
        }
    }

    private fun addFreshDataToAdapter() {
        clips.clear()
        clips.addAll(getAllClipsFromDb())
        clipsAdapter.setItems(clips)
    }

    private fun shareClip(clip: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, clip)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, "Share the copied clip"))
    }

    private fun makeToast(text: String) =
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    companion object {
        private const val FRAGMENT_ID = "1001"
        private const val CLIP_LABEL = "clip.text"
    }

}
