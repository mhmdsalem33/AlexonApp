package com.mhmd.alexon.helper

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class StaggeredGridItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val layoutManager = parent.layoutManager as StaggeredGridLayoutManager
        val spanCount = layoutManager.spanCount
        val position = parent.getChildAdapterPosition(view)

        // Apply margin to all sides except the last row
        if (position < parent.adapter?.itemCount ?: 0 - spanCount) {
            outRect.bottom = space
        }
        if (position % spanCount != spanCount - 1) {
            outRect.right = space
        }
        if (position % spanCount != 0) {
            outRect.left = space
        }
    }
}
