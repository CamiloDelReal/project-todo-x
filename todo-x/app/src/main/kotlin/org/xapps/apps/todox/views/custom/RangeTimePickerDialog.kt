package org.xapps.apps.todox.views.custom

import android.app.TimePickerDialog
import android.content.Context
import android.widget.TimePicker
import java.text.DateFormat
import java.util.*


/**
 * Taken from
 * https://medium.com/@toyninn/set-timepickerdialog-after-chose-datepickerdialog-in-one-dialog-and-set-range-for-date-and-time-481006bc5b2
 */
class RangeTimePickerDialog(
    context: Context?,
    callBack: OnTimeSetListener?,
    private var currentHour: Int,
    private var currentMinute: Int,
    is24HourView: Boolean
) :
    TimePickerDialog(context, callBack, currentHour, currentMinute, is24HourView) {
    private var minHour = -1
    private var minMinute = -1
    private var maxHour = 25
    private var maxMinute = 25
    private val calendar = Calendar.getInstance()
    private val dateFormat: DateFormat = DateFormat.getTimeInstance(DateFormat.SHORT)

    fun setMin(hour: Int, minute: Int) {
        minHour = hour
        minMinute = minute
    }

    fun setMax(hour: Int, minute: Int) {
        maxHour = hour
        maxMinute = minute
    }

    override fun onTimeChanged(view: TimePicker, hourOfDay: Int, minute: Int) {
        var validTime = true
        if (hourOfDay < minHour || hourOfDay == minHour && minute < minMinute) {
            validTime = false
        }
        if (hourOfDay > maxHour || hourOfDay == maxHour && minute > maxMinute) {
            validTime = false
        }
        if (validTime) {
            currentHour = hourOfDay
            currentMinute = minute
        }
        updateTime(currentHour, currentMinute)
        updateDialogTitle(view, currentHour, currentMinute)
    }

    private fun updateDialogTitle(
        timePicker: TimePicker,
        hourOfDay: Int,
        minute: Int
    ) {
        calendar[Calendar.HOUR_OF_DAY] = hourOfDay
        calendar[Calendar.MINUTE] = minute
        val title = dateFormat.format(calendar.time)
        setTitle(title)
    }

    init {
        try {
            val superclass: Class<*> = this.javaClass.superclass
            val mTimePickerField = superclass.getDeclaredField("mTimePicker")
            mTimePickerField.isAccessible = true
            val mTimePicker = mTimePickerField[this] as TimePicker
            mTimePicker.setOnTimeChangedListener(this)
        } catch (e: NoSuchFieldException) {
        } catch (e: IllegalArgumentException) {
        } catch (e: IllegalAccessException) {
        }
    }
}
