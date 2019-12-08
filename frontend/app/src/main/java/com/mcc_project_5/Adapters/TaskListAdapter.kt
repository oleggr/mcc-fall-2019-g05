package com.mcc_project_5.Adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.mcc_project_5.DataModels.Task
import com.mcc_project_5.R

class TaskListAdapter : BaseAdapter() {
    private var items: ArrayList<Task> = ArrayList()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.project_content_task_list_layout, parent, false)

        val checkBox = rowView.findViewById<CheckBox>(R.id.taskCheckBox)
        val title = rowView.findViewById<TextView>(R.id.taskTitle)

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                title.paintFlags = checkBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                title.paintFlags = checkBox.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

        }}

        if (items[position].status == "completed") {
            checkBox.isChecked = true
            title.paintFlags = checkBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            checkBox.isChecked = false
            title.paintFlags = checkBox.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        title.text = items[position].description

        return rowView
    }

    fun setItems(data: ArrayList<Task>) {
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