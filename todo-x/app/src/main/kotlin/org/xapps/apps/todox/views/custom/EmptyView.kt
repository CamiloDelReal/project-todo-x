package org.xapps.apps.todox.views.custom

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import org.xapps.apps.todox.R
import org.xapps.apps.todox.databinding.ContentEmptyViewBinding


class EmptyView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    private var bindings: ContentEmptyViewBinding

    init {
        val a: TypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.EmptyView)
        val icon = a.getResourceId(R.styleable.EmptyView_ev_icon, -1)
        val description = a.getString(R.styleable.EmptyView_ev_description)
        val action = a.getString(R.styleable.EmptyView_ev_action)
        a.recycle()

        val layoutInflater = LayoutInflater.from(context)
        bindings = ContentEmptyViewBinding.inflate(layoutInflater, this, true)

        if (icon != -1) {
            bindings.imgDescription.isVisible = true
            bindings.imgDescription.setImageResource(icon)
        } else {
            bindings.imgDescription.isVisible = false
        }

        if(!description.isNullOrEmpty()) {
            bindings.txvDescription.isVisible = true
            bindings.space.isVisible = true
            bindings.txvDescription.text = description
        } else {
            bindings.txvDescription.isVisible = false
            bindings.space.isVisible = false
        }

        if(!action.isNullOrEmpty()) {
            bindings.btnAction.text = action
            bindings.btnAction.isVisible = true
        } else {
            bindings.btnAction.isVisible = false
        }
    }

    fun setOnActionClickListener(listener: OnClickListener) {
        bindings.btnAction.isVisible = true
        bindings.btnAction.setOnClickListener(listener)
    }

}