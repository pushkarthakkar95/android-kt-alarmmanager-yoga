package com.mlkitexample.yogachallenge.view

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.widget.TimePicker
import androidx.annotation.NonNull
import androidx.fragment.app.DialogFragment
import com.mlkitexample.yogachallenge.R
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class TimePickerFragment : DialogFragment(),TimePickerDialog.OnTimeSetListener {
    lateinit var sharedPrefs:SharedPreferences
    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        sharedPrefs = requireActivity().getSharedPreferences("yogachallenge.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        val hour = sharedPrefs.getInt(getString(R.string.hours_str),6)
        val min = sharedPrefs.getInt(getString(R.string.min_str),0)
        return TimePickerDialog(requireContext(),this,hour,min,true)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        with(sharedPrefs.edit()){
            putInt(getString(R.string.hours_str), hourOfDay)
            putInt(getString(R.string.min_str),minute)
            apply()
        }
        val activity = requireActivity() as MainActivity
        val timeToNotify = Calendar.getInstance()
        timeToNotify.set(Calendar.HOUR_OF_DAY,hourOfDay)
        timeToNotify.set(Calendar.MINUTE,minute)
        val currenttime = Calendar.getInstance()
        if(timeToNotify.timeInMillis-currenttime.timeInMillis > 0){
            //do nothing
        }else{
            timeToNotify.add(Calendar.HOUR_OF_DAY,24)
        }
        activity.processTimePickerResult(timeToNotify.timeInMillis)
    }

}
