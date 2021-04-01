package org.xapps.apps.todox.views.custom

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.widget.ImageViewCompat
import org.xapps.apps.todox.R
import org.xapps.apps.todox.databinding.ContentSummaryItemBinding


class SummaryItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    private lateinit var bindings: ContentSummaryItemBinding

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
        bindings = ContentSummaryItemBinding.inflate(layoutInflater, this, true)

        if (icon != -1) {
            bindings.imgIcon.setImageResource(icon)

            if(iconTint != -1) {
                ImageViewCompat.setImageTintList(bindings.imgIcon, ColorStateList.valueOf(iconTint))
            }
        }

        if(iconBackground != null) {
            bindings.iconBackgroundLayout.background = iconBackground
        }

        bindings.txvTitle.text = title
        bindings.txvCompleted.text = completed
        bindings.txvPending.text = pending
    }

    fun setTitle(title: String) {
        bindings.txvTitle.text = title
    }

    fun setCompleted(completed: String) {
        bindings.txvCompleted.text = completed
    }

    fun setPending(pending: String) {
        bindings.txvPending.text = pending
    }

    override fun setOnClickListener(newListener: OnClickListener?) {
        bindings.rootLayout.setOnClickListener(newListener)
    }

}