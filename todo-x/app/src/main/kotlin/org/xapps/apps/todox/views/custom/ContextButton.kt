package org.xapps.apps.todox.views.custom

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.xapps.apps.todox.R
import org.xapps.apps.todox.databinding.ContentContextButtonBinding


class ContextButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    private var bindings: ContentContextButtonBinding

    init {
        val a: TypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ContextButton)
        val icon = a.getResourceId(R.styleable.ContextButton_cb_icon, -1)
        val iconTint = a.getResourceId(R.styleable.ContextButton_cb_iconTint, -1)
        val backgroundTint = a.getColor(R.styleable.ContextButton_cb_backgroundTint, -1)
        val strokeColor = a.getColor(R.styleable.ContextButton_cb_strokeColor, -1)
        val text = a.getString(R.styleable.ContextButton_cb_text)
        val textColor = a.getResourceId(R.styleable.ContextButton_cb_textColor, -1)
        a.recycle()

        val layoutInflater = LayoutInflater.from(context)
        bindings = ContentContextButtonBinding.inflate(layoutInflater, this, true)

        if (icon != -1) {
            bindings.imgIcon.setImageResource(icon)

            if(iconTint != -1) {
                bindings.imgIcon.imageTintList = ColorStateList.valueOf(resources.getColor(iconTint, null))
            }
        }

        if(backgroundTint != -1) {
            bindings.rootLayout.setBackgroundColor(backgroundTint)
        }

        if(strokeColor != -1) {
            bindings.rootLayout.strokeColor = strokeColor
        }

        if(textColor != -1) {
            bindings.txvText.setTextColor(resources.getColor(textColor, null))
        }

        bindings.txvText.text = text
    }

    override fun setOnClickListener(newListener: OnClickListener?) {
        bindings.rootLayout.setOnClickListener(newListener)
    }

}