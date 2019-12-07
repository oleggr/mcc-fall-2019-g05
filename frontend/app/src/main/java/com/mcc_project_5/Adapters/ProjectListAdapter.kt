package com.mcc_project_5.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.mcc_project_5.DataModels.Project
import com.squareup.picasso.Picasso
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcc_project_5.ProjectContentActivity
import com.mcc_project_5.R
import kotlinx.android.synthetic.main.list_of_projects_list_layout.view.*
import kotlin.collections.ArrayList


class ProjectListAdapter: BaseAdapter() {
    private val picasso = Picasso.get()

    fun showPopupMenu(context: Context, v: View, projectId: Int) {
        val popup = PopupMenu(context, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.list_of_projects_popup, popup.menu)

        popup.setOnMenuItemClickListener {
            if (it.itemId != -1) {
                if (it.title == "content") {
                    val intent =
                        Intent(context, ProjectContentActivity::class.java)
                    intent.putExtra("projectId", projectId.toString())
                    context.startActivity(intent)
                }
                System.err.println(it.title)
                true
            }
            false
        }

        popup.show()
    }

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
        val membersList = rowView.findViewById(R.id.membersView) as RecyclerView

        rowView.menuBtn.setOnClickListener {
            showPopupMenu(context, it, items[position].id)
        }

        if (!items[position].isMediaAvailable)
            isMediaAvailable.visibility = View.INVISIBLE
        else
            isMediaAvailable.visibility = View.VISIBLE
        if (items[position].isFavorite)
            isFavorite.setImageResource(android.R.drawable.btn_star_big_on)
        else
            isFavorite.setImageResource(android.R.drawable.btn_star_big_off)

        titleText.text = items[position].title

        if (items[position].imageUrl != "") {
            picasso.load(items[position].imageUrl).into(imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_folder_open_black_24dp)
        }
        //picasso.load(items[position].imageUrl).into(imageView)

        lastModTimeText.text = items[position].lastModified

        val layoutManager = LinearLayoutManager(rowView.context, LinearLayoutManager.HORIZONTAL, false)
        membersList.layoutManager = layoutManager
        val membersListAdapter =
            MemberListAdapter(items[position].membersArray)
        membersList.adapter = membersListAdapter

        rowView.setTag(items[position].id.toString())


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