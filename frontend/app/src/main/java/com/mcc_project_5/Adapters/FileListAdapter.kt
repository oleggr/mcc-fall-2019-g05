package com.mcc_project_5.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mcc_project_5.DataModels.Attachment
import com.mcc_project_5.R

class FileListAdapter : BaseAdapter() {
    private var items: ArrayList<Attachment> = ArrayList()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val context = parent!!.context
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.project_content_file_list_layout, parent, false)

        val imageView = rowView.findViewById<ImageView>(R.id.fileImageView)
        val textView = rowView.findViewById<TextView>(R.id.textView)

        textView.text = items[position].name

        val res = items[position].name.substring(items[position].name.lastIndexOf('.') + 1)
        if (res.toLowerCase() == "pdf") {
            imageView.setImageResource(R.drawable.pdf_res)
        } else if (res.toLowerCase() == "doc" || res.toLowerCase() == "docx") {
            imageView.setImageResource(R.drawable.doc_res)
        } else if (res.toLowerCase() == "jpg" || res.toLowerCase() == "jpeg") {
            imageView.setImageResource(R.drawable.jpg_res)
        } else {
            imageView.setImageResource(R.drawable.rnd_res)
        }

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