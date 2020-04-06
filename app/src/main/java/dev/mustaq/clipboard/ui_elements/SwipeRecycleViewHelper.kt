package dev.mustaq.clipboard.ui_elements

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dev.mustaq.clipboard.R
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

/**
 * Created by Mustaq Sameer on 06/04/20
 */

class SwipeRecycleViewHelper(
    val context: Context,
    val onSwipeListener : (viewHolder: RecyclerView.ViewHolder, direction: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onSwipeListener(viewHolder, direction)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            .addSwipeLeftBackgroundColor(ContextCompat.getColor(context, R.color.red_600))
            .addSwipeLeftActionIcon(R.drawable.ic_bin)
            .addSwipeRightBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            .addSwipeRightActionIcon(R.drawable.ic_share)
            .create()
            .decorate()
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun getCallback() = ItemTouchHelper(this)

    fun attachToRecyclerView(recyclerView: RecyclerView) =
        getCallback().attachToRecyclerView(recyclerView)
}