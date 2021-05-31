package com.wrabot.tools.viewbinding

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

/**
 * An header item decoration.
 * The header must have the same fix size
 * @param header the header view for position or null if no header
 */
@Suppress("unused")
class HeaderItemDecoration(private val header: (holder: RecyclerView.ViewHolder) -> View?) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = header(parent.getChildViewHolder(view))?.computeHeight() ?: 0
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)
        parent.children.forEach {
            val header = header(parent.getChildViewHolder(it))
            if (header != null) {
                val height = header.computeHeight()
                if (height > 0) {
                    canvas.save()
                    canvas.translate(0f, (it.top - height).toFloat())
                    header.draw(canvas)
                    canvas.restore()
                }
            }
        }
    }

    private fun View.computeHeight() = layoutParams.height.coerceAtLeast(0)
}
