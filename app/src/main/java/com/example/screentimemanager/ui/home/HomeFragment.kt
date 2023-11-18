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

    private lateinit var applicationsAdapter: UserApplicationsAdapter

    fun getApplicationsList (context: Context): List<ApplicationInfo>{
        val appsList = context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val mutableList:MutableList<ApplicationInfo>  = mutableListOf()
        for (app in appsList){
            if (app.flags and  ApplicationInfo.FLAG_SYSTEM == 0) {
                val appName = app.loadLabel(context.packageManager).toString()
                if(appName.trim().isNotEmpty()){
                    mutableList.add(app)
                }
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
        val users = getApplicationsList(requireContext())
        // access the listView from xml file
        var mListView = root.findViewById<ListView>(R.id.app_lists)
        applicationsAdapter = UserApplicationsAdapter(requireContext(),users)
        mListView.adapter = applicationsAdapter
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}