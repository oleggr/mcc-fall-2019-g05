package com.mcc_project_5.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mcc_project_5.DataModels.Attachment
import com.mcc_project_5.R
import com.mcc_project_5.Tools.ImageStorage
import com.squareup.picasso.Picasso

class PictureListAdapter : BaseAdapter() {
    private val picasso = Picasso.get()
    private var items: ArrayList<Attachment> = ArrayList()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val context = parent!!.context
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.project_content_picture_list_layout, parent, false)

        val imageView = rowView.findViewById<ImageView>(R.id.pictureImageView)
        val createdAtTextView = rowView.findViewById<TextView>(R.id.createdAtTextView)
        val descriptionTextView = rowView.findViewById<TextView>(R.id.descriptionTextView)

        //picasso.load(items[position].imageUrl).into(imageView)
        ImageStorage(parent.context).loadToImageView(items[position].attachment_url, imageView)

        createdAtTextView.text = items[position].creation_time
        descriptionTextView.text = items[position].name

        return rowView
    }

    fun setItems(data: ArrayList<Attachment>) {
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