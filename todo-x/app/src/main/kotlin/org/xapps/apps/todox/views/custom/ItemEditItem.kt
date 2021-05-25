package org.xapps.apps.todox.views.custom

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.xapps.apps.todox.R
import org.xapps.apps.todox.databinding.ItemItemEditBinding


class ItemEditItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyle, defStyleRes) {

    private var bindings: ItemItemEditBinding

    init {
        val a: TypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ItemEditItem)
        val text = a.getString(R.styleable.ItemEditItem_iei_text)
        a.recycle()

        val layoutInflater = LayoutInflater.from(context)
        bindings = ItemItemEditBinding.inflate(layoutInflater, this, true)

        bindings.tieTask.setText(text)
    }

    fun setText(text: String) {
        bindings.tieTask.setText(text)
    }

}