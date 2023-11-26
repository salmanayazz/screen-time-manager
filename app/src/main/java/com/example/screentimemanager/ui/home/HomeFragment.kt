package com.example.screentimemanager.ui.home

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.screentimemanager.R
import com.example.screentimemanager.databinding.FragmentHomeBinding
import com.example.screentimemanager.ui.appsetting.AppSettingActivity
import com.example.screentimemanager.util.UserApplicationsAdapter
import com.example.screentimemanager.util.Util


class HomeFragment : Fragment() {

    var flags = PackageManager.GET_META_DATA or
            PackageManager.GET_SHARED_LIBRARY_FILES

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var applicationsAdapter: UserApplicationsAdapter


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
        val apps = Util.getApplicationsList(requireContext())
        var appListView = root.findViewById<ListView>(R.id.app_lists)
        applicationsAdapter = UserApplicationsAdapter(requireContext(),apps)
        appListView.adapter = applicationsAdapter

        /*
        setting the clicker for each item in application listview, which
        opens the appSetting activity for the clicked application.
         */
        appListView.setOnItemClickListener(){_, _, position, _ ->

            val selectedApplication: ApplicationInfo = applicationsAdapter.getItem(position)
            val intent: Intent = Intent(activity,AppSettingActivity::class.java)
                .putExtra(AppSettingActivity.APPLICATION_INFO, selectedApplication)
            startActivity(intent)
        }

        /*
        setting the functionality of the search widget to filter applications
         */
        var searchView: SearchView = root.findViewById(R.id.search_text)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                // Handle text changes here if needed
                applicationsAdapter.filter.filter(newText)
                return false
            }
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                return false
            }
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}