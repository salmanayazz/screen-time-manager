package com.example.screentimemanager.ui.home

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.screentimemanager.R
import com.example.screentimemanager.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    var flags = PackageManager.GET_META_DATA or
            PackageManager.GET_SHARED_LIBRARY_FILES

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    fun getApplicationsList (context: Context): List<String>{
        val appsList = context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val mutableList:MutableList<String>  = mutableListOf()
        for (app in appsList){
            if (app.flags and  ApplicationInfo.FLAG_SYSTEM == 0) {
                val appName = app.loadLabel(context.packageManager).toString()
                if(appName.trim().isNotEmpty()){
                    mutableList.add(appName)
                }

                println("the app name is ${appName}")
            }
        }
        return mutableList
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val arrayAdapter: ArrayAdapter<*>
        val users = getApplicationsList(requireContext())
        // access the listView from xml file
        var mListView = root.findViewById<ListView>(R.id.app_lists)
        arrayAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_list_item_1, users)
        mListView.adapter = arrayAdapter
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}