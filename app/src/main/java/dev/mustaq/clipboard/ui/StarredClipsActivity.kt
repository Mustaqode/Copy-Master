package dev.mustaq.clipboard.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.mustaq.clipboard.R
import dev.mustaq.clipboard.adapter.ClipsAdapter
import dev.mustaq.clipboard.db.*
import dev.mustaq.clipboard.helper.SwipeRecycleViewHelper
import dev.mustaq.clipboard.helper.setVisibilityOnCondition
import dev.mustaq.clipboard.dialog.ClipsDialogFragment
import kotlinx.android.synthetic.main.activity_starred_clips.uiLottieEmpty
import kotlinx.android.synthetic.main.activity_starred_clips.uiRecyclerView
import kotlinx.android.synthetic.main.activity_starred_clips.uiToolbar

/**
 * Created by Mustaq Sameer on 08/04/20
 */
class StarredClipsActivity : AppCompatActivity() {

    private val linearLayoutManager by lazy { LinearLayoutManager(this) }
    private val starredClipsAdapter by lazy {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starred_clips)
        setupUI()
        uiToolbar.setNavigationOnClickListener {
            onBackPressed()
            finish()
        }

    }

    private fun setupUI() {
        setupRecyclerView()
        addFreshDataToAdapter()
        startLottieAnimation()
    }

    private fun setupRecyclerView() {
        uiRecyclerView.layoutManager = linearLayoutManager
        uiRecyclerView.adapter = starredClipsAdapter
        uiRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            ).apply {
                getDrawable(R.drawable.divider_grey)?.let { setDrawable(it) }
            })
        swipeRecycleViewHelper.attachToRecyclerView(uiRecyclerView)
    }

    private val onItemClickListener: (String) -> Unit = { clip ->
        showClipDialog(clip)
    }

    private val onLongTouchListener: (ClipModel) -> Unit = { clip ->
        val clipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(CLIP_LABEL, clip.copiedText)
        clipboardManager.setPrimaryClip(clipData)
        makeToast("Text Copied")
    }

    private val onStarClickListener: (ClipModel) -> Unit = { clipModel ->
        clipModel.isStarred = !clipModel.isStarred
        addCopiedTextToDb(clipModel)
        addFreshDataToAdapter()
        startLottieAnimation()
    }

    private fun onSwipeListener(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        when (direction) {
            ItemTouchHelper.LEFT -> {
                deleteClipFromDb(clips[position])
                addFreshDataToAdapter()
                startLottieAnimation()
            }
            ItemTouchHelper.RIGHT -> {
                shareClip(clips[position].copiedText)
                starredClipsAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun showClipDialog(clip: String) {
        ClipsDialogFragment.newInstance(clip).apply {
            show(supportFragmentManager, FRAGMENT_ID)
        }
    }

    private fun startLottieAnimation() {
        uiLottieEmpty.alpha = 0.2f
        uiLottieEmpty.setVisibilityOnCondition(clips.size == 0)
        uiLottieEmpty.playAnimation()
    }

    private fun addFreshDataToAdapter() {
        clips.clear()
        clips.addAll(getAllStarredClips())
        starredClipsAdapter.setItems(clips)
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
        private const val FRAGMENT_ID = "1002"
        private const val CLIP_LABEL = "clip.text"
    }

}