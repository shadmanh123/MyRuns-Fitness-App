package com.example.shadman_hossain_myruns1

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ActivityEntriesAdapter(private val context: Context, private var entryList: List<ExerciseEntry>):
BaseAdapter() {
    private lateinit var view: View
    private lateinit var activityType: TextView
    private lateinit var activityTime: TextView
    private lateinit var distance: TextView
    private lateinit var duration: TextView
//    private lateinit var unitPreference: Int
    override fun getCount(): Int {
        return entryList.size
    }

    override fun getItem(p0: Int): Any {
        return entryList.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        view = View.inflate(context, R.layout.activity_entry_adapter, null)
        activityType = view.findViewById(R.id.activityType)
        activityTime = view.findViewById(R.id.activityTime)
        distance = view.findViewById(R.id.distance)
        duration = view.findViewById(R.id.duration)

        activityType.text = entryList.get(position).id.toString()
        activityTime.text = entryList.get(position).dateTime.toString()
        distance.text = entryList.get(position).distance.toString()
        duration.text = entryList.get(position).duration.toString()

        return view
    }

    fun replace(newEntryList: List<ExerciseEntry>){
        entryList = newEntryList
    }
}