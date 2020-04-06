package dev.mustaq.clipboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.mustaq.clipboard.R
import dev.mustaq.clipboard.db.ClipModel
import kotlinx.android.synthetic.main.model_clips_list.view.*

/**
 * Created by Mustaq Sameer on 29/03/20
 */

class ClipsAdapter(
    private val onItemClickListener: (String) -> Unit,
    private val onLongTouchListener: (String) -> Unit
) : RecyclerView.Adapter<ClipsAdapter.ClipsViewHolder>() {

    private val clips: ArrayList<ClipModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClipsViewHolder {
        return ClipsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.model_clips_list,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = clips.size

    override fun onBindViewHolder(holder: ClipsViewHolder, position: Int) {
        holder.itemView.uiTvClip.text = clips[position].copiedText
    }

    inner class ClipsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                onItemClickListener.invoke(clips[adapterPosition].copiedText)
            }

            view.setOnLongClickListener {
                onLongTouchListener.invoke(clips[adapterPosition].copiedText)
                return@setOnLongClickListener true
            }
        }
    }

    fun setItems(list: ArrayList<ClipModel>) {
        clips.clear()
        clips.addAll(list)
        notifyDataSetChanged()
    }
}