package com.example.screentimemanager.ui.home

import android.content.Context
import android.content.Intent
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
import com.example.screentimemanager.ui.appSetting.AppSetting


class HomeFragment : Fragment() {

    var flags = PackageManager.GET_META_DATA or
            PackageManager.GET_SHARED_LIBRARY_FILES

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var applicationsAdapter: UserApplicationsAdapter

    fun getApplicationsList (context: Context): List<ApplicationInfo>{
        //TODO : this function is time consuming , it needs to be done inside a coroutines NOT UI threads
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

        /* getting all users applications installed on their phone
        and show all the users apps using listview and custom adapter
         */
        val users = getApplicationsList(requireContext())
        var appListView = root.findViewById<ListView>(R.id.app_lists)
        applicationsAdapter = UserApplicationsAdapter(requireContext(),users)
        appListView.adapter = applicationsAdapter

        /*
        setting the clicker for each item in application listview, which
        opens the appSetting activity for the clicked application.
         */
        appListView.setOnItemClickListener(){_, _, position, _ ->
            val intent: Intent = Intent(activity,AppSetting::class.java)
            val selectedApplication: ApplicationInfo = applicationsAdapter.getItem(position)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}