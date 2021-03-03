package org.xapps.apps.todox.views.bindings

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.graphics.toColorInt
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.xapps.apps.todox.R
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.views.utils.ColorUtils


class CategoryChipGroup : ChipGroup {

    var listener: InverseBindingListener? = null
    var pendingSelection: List<Category>? = null

    constructor(context: Context) : this(context, null)

    constructor(arg0: Context, arg1: AttributeSet?) : super(arg0, arg1)

    constructor(arg0: Context, arg1: AttributeSet, arg2: Int) : super(arg0, arg1, arg2)

}

object CategoryChipGroupBindings {

    @JvmStatic
    @BindingAdapter("valuesAttrChanged")
    fun setListener(view: CategoryChipGroup, listener: InverseBindingListener?) {
        view.listener = listener
    }

    @JvmStatic
    @get:InverseBindingAdapter(attribute = "values")
    @set:BindingAdapter("values")
    var CategoryChipGroup.selectecdValues: List<Category>?
        get() {
            val items = ArrayList<Category>()
            for (i in 0 until childCount) {
                val chip = getChildAt(i) as Chip
                if (chip.isChecked)
                    items.add(chip.tag as Category)
            }
            return items
        }
        set(values) {
            val newValues = values ?: ArrayList()

            if (childCount > 0) {
                for (i in 0 until childCount)
                    (getChildAt(i) as Chip).isChecked = false

                newValues.forEach {
                    val chip = findViewWithTag<Chip>(it)
                    chip.isChecked = true
                }
            } else {
                pendingSelection = newValues
            }
        }

    @JvmStatic
    @BindingAdapter(value = ["entries", "checkable"], requireAll = false)
    fun setEntries(view: CategoryChipGroup, entries: List<Category>?, checkable: Boolean = false) {
        view.removeAllViews()
        entries?.let {
            val layout = if (checkable)
                R.layout.item_chip_filter
            else
                R.layout.item_chip
            entries.forEach {
                val chip = LayoutInflater.from(view.context).inflate(layout, view, false) as Chip
                chip.text = it.name
                chip.tag = it
                chip.chipBackgroundColor = ColorStateList.valueOf(it.color.toColorInt())
                val textColorResource = if (ColorUtils.isDarkColor(it.color)) {
                    R.color.textOnDark
                } else {
                    R.color.textOnLight
                }
                val textColor = view.context.getColor(textColorResource)
                chip.setTextColor(textColor)
                val rippleColor = if (ColorUtils.isDarkColor(it.color)) {
                        R.color.rippleOnDark
                    } else {
                        R.color.rippleOnLight
                    }
                chip.setRippleColorResource(rippleColor)
                chip.setChipIconTintResource(textColorResource)
                chip.checkedIconTint = ColorStateList.valueOf(textColor)
                if (checkable) {
                    chip.setOnCheckedChangeListener { _, _ ->
                        view.listener?.apply {
                            onChange()
                        }
                    }
                    if (view.pendingSelection != null && view.pendingSelection!!.contains(it))
                        chip.isChecked = true
                }
                view.addView(chip)
            }
        }
    }

}