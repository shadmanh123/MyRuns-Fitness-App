package com.example.shadman_hossain_myruns1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment

class HistoryFragment:Fragment() {
    private lateinit var listView: ListView
    private lateinit var view: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.history_fragment, container,false)
        listView = requireView().findViewById(R.id.historyEntriesListView)
        return view
    }
}