package com.example.screentimemanager.ui.home

import android.content.Context
import android.content.pm.ApplicationInfo
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import com.example.screentimemanager.R

class UserApplicationsAdapter(private val context: Context, private var userAppsList: List<ApplicationInfo>): BaseAdapter(), Filterable {

    private var filteredApplications: List<ApplicationInfo> = userAppsList


    override fun getCount(): Int {
        return filteredApplications.size
    }

    override fun getItem(position: Int): ApplicationInfo {
        return filteredApplications[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.layout_applications_list,null)

        var application = filteredApplications[position]

        val applicationIcon = view.findViewById(R.id.application_icon) as ImageView
        val applicationTitle = view.findViewById(R.id.application_name) as TextView

        applicationIcon.setImageDrawable(application.loadIcon(context.packageManager))
        applicationTitle.text = application.loadLabel(context.packageManager).toString()

        return view
    }
    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val queryString = constraint?.toString()?.lowercase()
                val filterResults = FilterResults()

                if (queryString.isNullOrBlank()) {
                    filterResults.values = userAppsList
                } else {
                    val filteredList = userAppsList.filter { app ->
                        app.loadLabel(context.packageManager)
                            .toString().lowercase()
                            .contains(queryString)
                    }
                    filterResults.values = filteredList
                }
                return filterResults
            }
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                results?.let {
                    filteredApplications = it.values as List<ApplicationInfo>
                    notifyDataSetChanged()
                }
            }
        }
    }
}