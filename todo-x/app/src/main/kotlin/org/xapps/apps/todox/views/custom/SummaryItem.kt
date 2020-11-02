package org.xapps.apps.todox.views.custom

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.ImageViewCompat
import kotlinx.android.synthetic.main.content_summary_item.view.*
import org.xapps.apps.todox.R
import org.xapps.apps.todox.databinding.ContentSummaryItemBinding


class SummaryItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    init {
        val a: TypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SummaryItem)
        val icon = a.getResourceId(R.styleable.SummaryItem_si_icon, -1)
        val iconTint = a.getColor(R.styleable.SummaryItem_si_iconTint, -1)
        val iconBackground = a.getDrawable(R.styleable.SummaryItem_si_iconBackground)
        val title = a.getString(R.styleable.SummaryItem_si_title)
        val completed = a.getString(R.styleable.SummaryItem_si_completed)
        val pending = a.getString(R.styleable.SummaryItem_si_pending)
        a.recycle()

        val layoutInflater = LayoutInflater.from(context)
        ContentSummaryItemBinding.inflate(layoutInflater, this, true)

        if (icon != -1) {
            imgIcon.setImageResource(icon)

            if(iconTint != -1) {
                ImageViewCompat.setImageTintList(imgIcon, ColorStateList.valueOf(iconTint))
            }
        }

        if(iconBackground != null) {
            iconBackgroundLayout.background = iconBackground
        }

        txvTitle.text = title
        txvCompleted.text = completed
        txvPending.text = pending
    }

    fun setTitle(title: String) {
        txvTitle.text = title
    }

    fun setCompleted(completed: String) {
        txvCompleted.text = completed
    }

    fun setPending(pending: String) {
        txvPending.text = pending
    }

    override fun setOnClickListener(newListener: OnClickListener?) {
        val fgValue = TypedValue()
        getContext().theme.resolveAttribute(
            android.R.attr.selectableItemBackground,
            fgValue,
            true
        )
        rootLayout.foreground = AppCompatResources.getDrawable(context, fgValue.resourceId)
        rootLayout.setOnClickListener(newListener)
    }

}