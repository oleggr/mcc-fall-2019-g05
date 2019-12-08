package com.mcc_project_5.Adapters

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.mcc_project_5.DataModels.Task
import com.mcc_project_5.R
import com.mcc_project_5.Tools.Requester
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class TaskListAdapter() : BaseAdapter() {
    private var items: ArrayList<Task> = ArrayList()

    private lateinit var context: Context
    private var projectId = ""

    constructor(projectId: String) : this() {
        this.projectId = projectId
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        context = parent!!.context
        val requester = Requester(context)
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.project_content_task_list_layout, parent, false)

        val checkBox = rowView.findViewById<CheckBox>(R.id.taskCheckBox)
        val title = rowView.findViewById<TextView>(R.id.taskTitle)

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
               // title.paintFlags = checkBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                title.paintFlags = checkBox.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                taskFinished(requester, items[position].id)
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

    fun taskFinished(requester: Requester, uid: String) {
        val json= JSONObject()
        json.put("id",uid)

        requester.httpPost("task/$projectId/update", json, object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("DDD", "FAIL")
                return
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("DDD","OK")
                } else {
                    Log.d("DDD","NOT OK")
                    return
                }
            }
        })
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