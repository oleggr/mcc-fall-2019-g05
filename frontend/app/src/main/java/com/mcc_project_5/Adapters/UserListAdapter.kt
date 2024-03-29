package com.mcc_project_5.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.mcc_project_5.DataModels.User
import com.mcc_project_5.R
import com.mcc_project_5.Tools.ImageStorage
import com.squareup.picasso.Picasso

class UserListAdapter() : BaseAdapter() {
    val picasso = Picasso.get()
    private var items: ArrayList<User> = ArrayList()
    private var currentProjectId = ""

    constructor(currentProjectId: String) : this() {
        this.currentProjectId = currentProjectId
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val context = parent!!.context
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.user_list_list_layout, parent, false)

        val imageView = rowView.findViewById<ImageView>(R.id.userImageView)
        val textView = rowView.findViewById<TextView>(R.id.userNameTextView)
        val checkBox = rowView.findViewById<CheckBox>(R.id.userChecked)

        textView.text = items[position].name
        if (items[position].imageUrl != "") {
            ImageStorage(parent.context).loadToImageView(items[position].imageUrl, imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_account_circle_black_24dp)
        }
        checkBox.isChecked = items[position].projectsArray.contains(currentProjectId)
        checkBox.setOnCheckedChangeListener { buttonView, isChecked -> items[position].checked = isChecked }

        return rowView
    }

    fun setItems(data: ArrayList<User>) {
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