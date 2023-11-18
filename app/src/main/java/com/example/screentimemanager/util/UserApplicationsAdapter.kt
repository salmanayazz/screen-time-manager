package com.example.screentimemanager.util

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

/**
 * Custom adapter for displaying a list of user's phone applications in a ListView.
 *
 * @property context The context of the application.
 * @property userAppsList The original list of all user's applications on their phone.
 */
class UserApplicationsAdapter(
    private val context: Context,
    private var userAppsList: List<ApplicationInfo>): BaseAdapter(), Filterable {

    /**
     * The list of the all applications filtered by using search widget
     */
    private var filteredApplications: List<ApplicationInfo> = userAppsList


    /**
     * returns the size of applications items in the filtered application lists.
     *
     * @return The count of applications in the filtered application lists.
     */
    override fun getCount(): Int {
        return filteredApplications.size
    }

    /**
     * returns the application at the specified position in the filtered application lists.
     *
     * @param position The position for the application to return.
     * @return The application at the specified position in the filtered application lists.
     */
    override fun getItem(position: Int): ApplicationInfo {
        return filteredApplications[position]
    }

    /**
     * returns the ID of the application at the specified position in the filtered application lists.
     *
     * @param position The position of the application.
     * @return The ID of the application at the given position.
     */
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


    /**
     * Is used to filter list of all applications based on the query in search widget.
     *
     * @return A filter object
     */
    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                // the search text in the widget
                val searchText = constraint?.toString()?.lowercase()
                val filterResults = FilterResults()

                // if the search text is empty
                // then show all of the applications
                if (searchText.isNullOrBlank()) {
                    filterResults.values = userAppsList
                }
                else {
                    // if the search text is not empty
                    // then filter the applications list by the applications
                    // where their names contain the search query
                    val filteredList = userAppsList.filter { app ->
                        app.loadLabel(context.packageManager)
                            .toString().lowercase()
                            .contains(searchText)
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