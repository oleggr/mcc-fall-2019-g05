package com.mcc_project_5

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mcc_project_5.DataModels.Project
import com.squareup.picasso.Picasso

class SimpleListAdapter: BaseAdapter() {
    private var items: ArrayList<Project> = ArrayList()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val context = parent!!.context
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.list_of_projects_list_layout, parent, false)

        val titleText = rowView.findViewById(R.id.titleText) as TextView
        val imageView = rowView.findViewById(R.id.imageView) as ImageView
        val lastModTimeText = rowView.findViewById(R.id.lastModTimeText) as TextView
        val isFavorite = rowView.findViewById(R.id.isFavorite) as ImageView
        val isMediaAvailable = rowView.findViewById(R.id.isMediaAvailable) as ImageView

        if (!items[position].isMediaAvailable)
            isMediaAvailable.visibility = View.INVISIBLE
        else
            isMediaAvailable.visibility = View.VISIBLE
        if (items[position].isFavorite)
            isFavorite.setImageResource(android.R.drawable.btn_star_big_on)
        else
            isFavorite.setImageResource(android.R.drawable.btn_star_big_off)

        titleText.text = items[position].title
        //Picasso.get().load(items[position].imageUrl).into(imageView)
        lastModTimeText.text = items[position].lastModified

        return rowView
    }

    fun setItems(data: ArrayList<Project>) {
        items = data
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

}
