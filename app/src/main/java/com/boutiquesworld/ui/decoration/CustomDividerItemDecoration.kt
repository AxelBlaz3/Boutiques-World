package com.boutiquesworld.ui.decoration

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.boutiquesworld.R

class CustomDividerItemDecoration :
    RecyclerView.ItemDecoration() {
    private val mPaint: Paint = Paint()
    private var mDivider: Drawable? = null

    init {
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
        mPaint.color = android.R.attr.colorControlNormal
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        for (i in 0 until parent.childCount) {
            val right = parent.width - parent.paddingRight

            val view = parent.getChildAt(i)

            mDivider = ContextCompat.getDrawable(view.context, R.drawable.divider)

            // Draw divider only when the view types are different
            val params = view
                .layoutParams as RecyclerView.LayoutParams
            val top = view.bottom + params.bottomMargin
            val bottom = top + mDivider!!.intrinsicHeight
            mDivider!!.setBounds(
                view.context.resources.getDimension(R.dimen.divider_left_margin).toInt(),
                top,
                right,
                bottom
            )
            mDivider!!.draw(c)
        }
    }
}