package com.wrabot.tools.viewbinding

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

/**
 * An header item decoration.
 * @param header the header view for position or null if no header
 */
@Suppress("unused")
class HeaderItemDecoration(private val header: (position: Int) -> View?) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = header(parent.getChildAdapterPosition(view))?.height ?: 0
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)
        parent.children.forEach {
            val header = header(parent.getChildAdapterPosition(it))
            if (header != null) {
                canvas.save()
                canvas.translate(0f, (it.top - header.height).toFloat())
                header.draw(canvas)
                canvas.restore()
            }
        }
    }
}
