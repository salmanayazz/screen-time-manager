package com.example.screentimemanager.ui.home

import android.content.Context
import android.content.pm.ApplicationInfo
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.screentimemanager.R

class UserApplicationsAdapter(private val context: Context, private var userAppsList: List<ApplicationInfo>): BaseAdapter() {
    override fun getCount(): Int {
        return userAppsList.size
    }

    override fun getItem(position: Int): Any {
        return userAppsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.layout_applications_list,null)

        var application = userAppsList[position]

        val applicationIcon = view.findViewById(R.id.application_icon) as ImageView
        val applicationTitle = view.findViewById(R.id.application_name) as TextView

        applicationIcon.setImageDrawable(application.loadIcon(context.packageManager))
        applicationTitle.text = application.loadLabel(context.packageManager).toString()

        return view
    }
}