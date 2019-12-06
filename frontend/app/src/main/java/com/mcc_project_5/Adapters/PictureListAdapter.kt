package com.mcc_project_5.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mcc_project_5.DataModels.Picture
import com.mcc_project_5.R
import com.squareup.picasso.Picasso

class PictureListAdapter : BaseAdapter() {
    private val picasso = Picasso.get()
    private var items: ArrayList<Picture> = ArrayList()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val context = parent!!.context
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.project_content_picture_list_layout, parent, false)

        val imageView = rowView.findViewById<ImageView>(R.id.pictureImageView)
        val createdAtTextView = rowView.findViewById<TextView>(R.id.createdAtTextView)
        val descriptionTextView = rowView.findViewById<TextView>(R.id.descriptionTextView)

        picasso.load(items[position].imageUrl).into(imageView)

        createdAtTextView.text = items[position].createdAt
        descriptionTextView.text = items[position].description

        return rowView
    }

    fun setItems(data: ArrayList<Picture>) {
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