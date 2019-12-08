package com.mcc_project_5.Adapters

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.mcc_project_5.DataModels.Project
import com.squareup.picasso.Picasso
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcc_project_5.ListOfCreatedProjectsActivity
import com.mcc_project_5.ProjectContentActivity
import com.mcc_project_5.R
import com.mcc_project_5.Tools.ImageStorage
import kotlinx.android.synthetic.main.list_of_projects_list_layout.view.*
import okhttp3.*
import java.io.IOException
import kotlin.collections.ArrayList
import com.mcc_project_5.Tools.Requester
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ProjectListAdapter: BaseAdapter() {
    private val picasso = Picasso.get()
    private lateinit var context: Context

    fun showPopupMenu(context: Context, v: View, projectId: String, title: String, isOwner: Boolean, requester: Requester) {
        val popup = PopupMenu(context, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.list_of_projects_popup, popup.menu)
        if (!isOwner) {
            popup.menu.getItem(0).isVisible = false
        }


        popup.setOnMenuItemClickListener (PopupMenu.OnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.deleteItem ->
                    deleteItem(projectId, requester)
                R.id.contentItem ->
                {
                    System.err.println(projectId)
                    val intent =
                        Intent(context, ProjectContentActivity::class.java)
                    intent.putExtra("projectId", projectId)
                    intent.putExtra("title", title)
                    intent.putExtra("isOwner", isOwner)
                    context.startActivity(intent)
                }
                R.id.reportItem ->
                    reportItem(projectId, requester)
            }
            true
        })

        popup.show()
    }


    fun deleteItem(id: String, requester: Requester) {
        requester.httpDelete("project/$id/delete", object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("DDD", "FAIL")
                return
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    //items.removeAt(id)
                    Log.d("DDD", "OK")
                    val intent = Intent(context, ListOfCreatedProjectsActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                } else {
                    Log.d("DDD", "NO")
                    return
                }
            }
        })
    }

    fun reportItem(id: String, requester: Requester) {
        try {
            val reportUrl = "project/$id/generate_report"
            val path = File(Environment.getExternalStorageDirectory() , "/Downloads")
            downloadFile(reportUrl, path, null, null, requester)
        }
        catch (e: IOException) {

        }
    }

    fun downloadFile(url: String, dir: File, name: String?, fileExt: String?, requester: Requester) {

        requester.httpDownload(url, object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("DDD", "FAIL")
                return
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val contentType = response.header("content-type", null)
                    var ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentType)
                    ext = if (ext == null) {
                        fileExt
                    } else {
                        ".$ext"
                    }

                    // use provided name or generate a temp file
                    var file: File? = null
                    file = if (name != null) {
                        val filename = String.format("%s%s", name, ext)
                        File(dir.absolutePath, filename)
                    } else {
                        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-kkmmss"))
                        File.createTempFile(timestamp, ext, dir)
                    }
                    Log.d("sfsfg", "sfgfg")
                    /*val body = response.body
                    val sink = Okio.buffer(Okio.sink(file))

                    body?.source().use { input ->
                        sink.use { output ->
                            output.writeAll(input)
                        }
                    }*/
                } else {
                    return
                }
            }
        })

    }

    private var items: ArrayList<Project> = ArrayList()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        context = parent!!.context
        val requester = Requester(context)
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.list_of_projects_list_layout, parent, false)

        val titleText = rowView.findViewById(R.id.titleText) as TextView
        val imageView = rowView.findViewById(R.id.imageView) as ImageView
        val lastModTimeText = rowView.findViewById(R.id.lastModTimeText) as TextView
        val isFavorite = rowView.findViewById(R.id.isFavorite) as ImageView
        val isMediaAvailable = rowView.findViewById(R.id.isMediaAvailable) as ImageView
        val membersList = rowView.findViewById(R.id.membersView) as RecyclerView

        rowView.menuBtn.setOnClickListener {
            showPopupMenu(context, it, items[position].id, items[position].title, items[position].isOwner, requester)
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
            //picasso.load(items[position].imageUrl).into(imageView)
            ImageStorage(parent.context).loadToImageView(items[position].imageUrl, imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_folder_open_black_24dp)
        }
        //picasso.load(items[position].imageUrl).into(imageView)

        lastModTimeText.text = items[position].lastModified

        val layoutManager = LinearLayoutManager(rowView.context, LinearLayoutManager.HORIZONTAL, false)
        membersList.layoutManager = layoutManager
        val membersListAdapter =
            MemberListAdapter(context, items[position].membersArray)
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
