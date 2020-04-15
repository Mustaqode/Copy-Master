package dev.mustaq.clipboard.ui

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mustaq.clipboard.R
import dev.mustaq.clipboard.adapter.ClipsAdapter
import dev.mustaq.clipboard.db.*
import dev.mustaq.clipboard.helper.*
import dev.mustaq.clipboard.mapper.AnalyticsMapper
import dev.mustaq.clipboard.service.CopyService
import dev.mustaq.clipboard.dialog.ClipsDialogFragment
import kotlinx.android.synthetic.main.activity_home.*
import java.util.concurrent.TimeUnit

class HomeActivity : AppCompatActivity() {

    private val linearLayoutManager by lazy { LinearLayoutManager(this) }
    private val clipboardManager by lazy { getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
    private val clipsAdapter by lazy {
        ClipsAdapter(
            onItemClickListener,
            onLongTouchListener,
            onStarClickListener
        )
    }
    private val swipeRecycleViewHelper by lazy {
        SwipeRecycleViewHelper(
            this,
            ::onSwipeListener
        )
    }
    private val clips: ArrayList<ClipModel> = ArrayList()
    private var longBackPressed: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
//        val toolbar = findViewById<Toolbar>(R.id.uiToolbar)
        setSupportActionBar(uiToolbar)
        setupUi()
        setListeners()
    }

    private fun setupUi() {
        setForegroundAlpha()
        addTriggerObject() //To initiate trigger db
        uiSwitchService.isChecked = isServiceRunning(CopyService::class.java)
        setupRecyclerviewAdapter()
        addFreshDataToAdapter()
        setupAnalyticsPanel()
        realmLiveData(getTriggerObjectFromDb()) {
            addFreshDataToAdapter()
            setupAnalyticsPanel()
            uiTvDeleteAll.setVisibilityOnCondition(clips.size > 0)
            uiEtSearch.setVisibilityOnCondition(clips.size > 0)
            setEmptyClipIndicator()
        }
        uiTvDeleteAll.setVisibilityOnCondition(clips.size > 0)
        uiEtSearch.setVisibilityOnCondition(clips.size > 0)
        setEmptyClipIndicator()
    }

    private fun setListeners() {
        uiSwitchService.setOnCheckedChangeListener { _, isChecked -> toggleService(isChecked) }
        uiIvInfo.setOnClickListener { moveToNewActivity(AboutActivity::class.java)}
        uiTvDeleteAll.setOnClickListener { showDeleteDialog() }
        uiFab.setOnClickListener {
            moveToNewActivity(
                StarredClipsActivity::class.java,
                REQUEST_CODE_ACTIVITY
            )
        }
        uiEtSearch.addTextChangedListener { editable ->
            if (editable.toString().length > 2) performFilter(editable)
            else addFreshDataToAdapter()
        }
    }

    private fun setForegroundAlpha() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            uiClHomeLayout.foreground.alpha = 0
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
    }

    private fun setEmptyClipIndicator() {
        uiLottieEmpty.alpha = 0.2f
        uiLottieEmpty.setVisibilityOnCondition(clips.size == 0)
        uiTvEmptyMessage.setVisibilityOnCondition(clips.size == 0)
        uiLottieEmpty.playAnimation()
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

    @SuppressLint("DefaultLocale")
    private fun performFilter(editable: Editable?) {
        val filteredList = clips.filter {
            it.copiedText.toLowerCase().trim().contains(editable.toString().toLowerCase().trim())
        }
        clips.clear()
        clips.addAll(filteredList)
        clipsAdapter.setItems(clips)
        clipsAdapter.notifyDataSetChanged()
    }

    private val onItemClickListener: (String) -> Unit = { clip ->
        showClipDialog(clip)
    }

    private val onLongTouchListener: (ClipModel) -> Unit = { clip ->
        val clipData = ClipData.newPlainText(CLIP_LABEL, clip.copiedText)
        clipboardManager.setPrimaryClip(clipData)
        makeToast("Text Copied")
        saveCopiedTextToDb(clip)
    }

    private val onStarClickListener: (ClipModel) -> Unit = { clipModel ->
        clipModel.isStarred = !clipModel.isStarred
        addCopiedTextToDb(clipModel)
    }

    private fun onSwipeListener(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        when (direction) {
            ItemTouchHelper.LEFT -> {
                deleteClipFromDb(clips[position])
                addFreshDataToAdapter()
                setupAnalyticsPanel()
                addTriggerObject() //Trigger livedata
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

    private fun showDeleteDialog() {
        showAlertDialog(positive = deleteAllFromDatabase)
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

    private fun saveCopiedTextToDb(clipModel: ClipModel) {
        clipboardManager.addPrimaryClipChangedListener {
            val text = clipboardManager.primaryClip?.getItemAt(0)?.text
            val clip = ClipModel(copiedText = text.toString(), isStarred = clipModel.isStarred)
            addCopiedTextToDb(clip)
            addTriggerObject()
        }
    }

    private val deleteAllFromDatabase: () -> Unit = {
        deleteAllClipsFromDb()
        addFreshDataToAdapter()
        setupAnalyticsPanel()
        addTriggerObject()
        uiTvDeleteAll.visibility = View.INVISIBLE
    }

    private fun makeToast(text: String) =
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        addFreshDataToAdapter()
    }

    override fun onResume() {
        super.onResume()
        setForegroundAlpha()
    }

    override fun onBackPressed() {
        val difference = System.currentTimeMillis() - longBackPressed
        if (difference > TimeUnit.SECONDS.toMillis(2))
            makeToast("Press again to exit")
        else super.onBackPressed()
        longBackPressed = System.currentTimeMillis()
    }

    companion object {
        private const val FRAGMENT_ID = "1001"
        private const val CLIP_LABEL = "clip.text"
        private const val REQUEST_CODE_ACTIVITY = 1001
    }

}
