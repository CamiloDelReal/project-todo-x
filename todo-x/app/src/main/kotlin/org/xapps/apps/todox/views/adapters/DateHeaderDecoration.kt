package org.xapps.apps.todox.views.adapters

import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.xapps.apps.todox.viewmodels.Constants


class DateHeaderDecoration<T : RecyclerView.ViewHolder> constructor(private val decorationSupport: DecorationSupport<T>): RecyclerView.ItemDecoration() {

    private val headerViewsCache: MutableMap<Long, RecyclerView.ViewHolder> = mutableMapOf()

    override fun onDrawOver(canvas: Canvas, recyclerView: RecyclerView, state: RecyclerView.State) {

        var lastHeaderId = Constants.ID_INVALID
        val childsCount = recyclerView.childCount

        for(childPosition in 0 until childsCount) {
            val childView = recyclerView.getChildAt(childPosition)
            val childDataIndex = recyclerView.getChildAdapterPosition(childView)

            if(childDataIndex != RecyclerView.NO_POSITION && decorationSupport.hasHeader(childDataIndex)) {
                val headerId = decorationSupport.headerId(childDataIndex)
                if(headerId != lastHeaderId) {
                    lastHeaderId = headerId
                    val headerView = getHeader(recyclerView, childDataIndex)
                    canvas.save()
                    val xTranslation = childView.left.toFloat()
                    val yTranslation = getHeaderX(recyclerView, childView, headerView.itemView, childDataIndex, childPosition)
                    canvas.translate(xTranslation, yTranslation)
                    headerView.itemView.translationX = xTranslation
                    headerView.itemView.translationY = yTranslation
                    headerView.itemView.draw(canvas)
                    canvas.restore()
                }
            }
        }
    }

    private fun getHeaderX(recyclerView: RecyclerView, childView: View, header: View, childDataIndex: Int, childPosition: Int): Float {
        var top = childView.getY()
        if (childPosition == 0) {
            val count: Int = recyclerView.getChildCount()
            val currentId: Long = decorationSupport.headerId(childDataIndex)
            for (i in 1 until count) {
                val adapterPosHere: Int = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(i))
                if (adapterPosHere != RecyclerView.NO_POSITION) {
                    val nextId: Long = decorationSupport.headerId(adapterPosHere)
                    if (nextId != currentId) {
                        val next: View = recyclerView.getChildAt(i)
                        val offset = next.y.toInt() - getHeader(recyclerView, adapterPosHere).itemView.height
                        return if (offset < 0) {
                            offset.toFloat()
                        } else {
                            break
                        }
                    }
                }
            }
            top = Math.max(0f, top)
        }

        return top
    }

    private fun getHeader(parent: ViewGroup, index: Int): RecyclerView.ViewHolder {
        val headerId = decorationSupport.headerId(index)

        return if(headerViewsCache.containsKey(headerId)) {
            headerViewsCache[headerId]!!
        } else {
            val viewHolder = decorationSupport.onCreateHeaderViewHolder(parent)
            val view = viewHolder.itemView
            decorationSupport.onBindHeaderViewHolder(viewHolder, headerId, index)
            val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.measuredWidth, View.MeasureSpec.UNSPECIFIED)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.measuredHeight, View.MeasureSpec.UNSPECIFIED)

            val childWidth = ViewGroup.getChildMeasureSpec(widthSpec, parent.paddingLeft + parent.paddingRight, view.layoutParams.width)
            val childHeight = ViewGroup.getChildMeasureSpec(heightSpec, parent.paddingTop + parent.paddingBottom, view.layoutParams.height)

            view.measure(childWidth, childHeight)
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)

            headerViewsCache.put(headerId, viewHolder)
            viewHolder
        }
    }

    interface DecorationSupport<T : RecyclerView.ViewHolder> {
        fun hasHeader(index: Int): Boolean
        fun headerId(index: Int): Long
        fun onCreateHeaderViewHolder(parent: ViewGroup): T
        fun onBindHeaderViewHolder(view: T, headerId: Long, index: Int)
    }
}