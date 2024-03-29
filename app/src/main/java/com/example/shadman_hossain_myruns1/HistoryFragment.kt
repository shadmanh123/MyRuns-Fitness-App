package com.example.shadman_hossain_myruns1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class HistoryFragment:Fragment() {
    private lateinit var listView: ListView
    private lateinit var view: View
    private lateinit var arrayList: ArrayList<ExerciseEntry>
    private lateinit var arrayAdapter: ActivityEntriesAdapter
    private lateinit var database: ExerciseDatabase
    private lateinit var databaseDao: ExerciseDatabaseDao
    private lateinit var repository: ExerciseRepository
    private lateinit var exerciseViewModelFactory: ExerciseViewModelFactory
    private lateinit var exerciseViewModel: ExerciseViewModel
    private lateinit var intentToDisplayEntryActivity: Intent
    private lateinit var intentToMapDisplayActivity: Intent
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.history_fragment, container,false)
        listView = view.findViewById(R.id.historyEntriesListView)
        arrayList = ArrayList()
        arrayAdapter = ActivityEntriesAdapter(requireActivity(),arrayList)
        listView.adapter = arrayAdapter

        database = ExerciseDatabase.getInstance(requireActivity())
        databaseDao = database.exerciseDatabaseDao
        repository = ExerciseRepository(databaseDao)
        exerciseViewModelFactory = ExerciseViewModelFactory(repository)
        exerciseViewModel = ViewModelProvider(requireActivity(), exerciseViewModelFactory).get(ExerciseViewModel::class.java)
        exerciseViewModel.allExerciseLiveData.observe(requireActivity(), {
            arrayAdapter.replace(it)
            arrayAdapter.notifyDataSetChanged()
        })
        listView.setOnItemClickListener { adapterView, view, position, id ->
            val selectedEntry = arrayAdapter.getItem(position)
            val entryKey = selectedEntry.id
            val inputType = selectedEntry.inputType
            if (inputType == 1) {
                intentToDisplayEntryActivity =
                    Intent(requireContext(), DisplayEntryActivity::class.java)
                intentToDisplayEntryActivity.putExtra("entryKey", entryKey)
                startActivity(intentToDisplayEntryActivity)
            }
            else{
                intentToMapDisplayActivity = Intent(requireContext(), MapDisplayActivity::class.java)
                intentToMapDisplayActivity.putExtra("entryKey", entryKey)
                startActivity(intentToMapDisplayActivity)
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        exerciseViewModel.allExerciseLiveData.observe(requireActivity(), {
            arrayAdapter.replace(it)
            arrayAdapter.notifyDataSetChanged()
        })
    }
}