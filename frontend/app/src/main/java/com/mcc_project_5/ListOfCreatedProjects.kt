package com.mcc_project_5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.PopupMenu
import com.mcc_project_5.DataModels.Project
import kotlinx.android.synthetic.main.list_of_projects_list_layout.view.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class ListOfCreatedProjects : AppCompatActivity() {

    private val client = OkHttpClient()
    private val projects = ArrayList<Project>()
    private var lastClicked = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_created_projects)

        /*findViewById<ListView>(R.id.listView).setOnItemClickListener{ _, _, position, _ ->
            val intent = Intent(this@ListOfCreatedProjects, Main2Activity::class.java)
            val item = photoList[position]
            intent.putExtra("author", item.author)
            // low preformance, better way - get from cache
            intent.putExtra("url", item.photoUrl)
            startActivity(intent)
        }*/

        val listAdapter = ProjectListAdapter()
        listAdapter.setItems(projects)
        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = listAdapter
        listView.setOnItemClickListener { _, _, position, _ ->
            lastClicked = position
        }

    }

    fun httpGet(url: String, callBack: Callback): Call {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val call = client.newCall(request)
        call.enqueue(callBack)
        return call
    }

    fun showPopupMenu(v: View) {
        val popup = PopupMenu(this, v)
        System.err.println(projects[lastClicked])
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.list_of_projects_popup, popup.menu)
        popup.setOnMenuItemClickListener {
            if (it.itemId != -1) {
                System.err.println(it.title)
                true
            }
            false
        }
        popup.show()
    }


    fun loadTemplate(view: View) {
        val testJson = "[{\"id\":1,\"title\":\"xxx\",\"imageUrl\":\"aHR0cHM6Ly9wYnMudHdpbWcuY29tL3Byb2ZpbGVfaW1hZ2VzLzQ4ODU0MDk4MjUzOTg0OTcyOC9CODl0MzVzNS5qcGVn\",\"lastModified\":\"01.01.01\",\"isFavorite\":true,\"isMediaAvailable\":false,\"members\":[{\"id\":1,\"imageUrl\":\"aHR0cHM6Ly9tZWRpYWxlYWtzLnJ1L3dwLWNvbnRlbnQvdXBsb2Fkcy8yMDE4LzExL1NhaS0tdC1BcnRlLS1tLTI3My5qcGc=\"}, {\"id\":2,\"imageUrl\":\"aHR0cHM6Ly9tZWRpYWxlYWtzLnJ1L3dwLWNvbnRlbnQvdXBsb2Fkcy8yMDE4LzExL1NhaS0tdC1BcnRlLS1tLTI3My5qcGc=\"}]}, {\"id\":2,\"title\":\"yyy\",\"imageUrl\":\"aHR0cHM6Ly9wYnMudHdpbWcuY29tL3Byb2ZpbGVfaW1hZ2VzLzQ4ODU0MDk4MjUzOTg0OTcyOC9CODl0MzVzNS5qcGVn\",\"lastModified\":\"02.02.02\",\"isFavorite\":false,\"isMediaAvailable\":true,\"members\":[{\"id\":2,\"imageUrl\":\"aHR0cHM6Ly9pLnBpbmltZy5jb20vb3JpZ2luYWxzL2YzL2UxL2I4L2YzZTFiODAxOWYxNjBmODg1MzFkOGFmNzkyNzE2YjRmLnBuZw==\"}]}]"
        val json = JSONArray(testJson)
        projects.clear()
        for(i in 0 until json.length()) {
            val item = json.getJSONObject(i)
            val project = Project(item)
            projects.add(project)
        }
        val adapter = findViewById<ListView>(R.id.listView).adapter as BaseAdapter
        adapter.notifyDataSetChanged()
        Log.d("DDD", projects.toString())
    }

    fun onLoadBtnClick(view: View) {
        val baseUrl = Properties(baseContext).getProperty("baseUrl")
        httpGet(baseUrl + "/getAvailableProjects?userid=%", object:Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("DDD", "FAIL")
                return
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val resultJson = response.body!!.string()
                    val json = JSONArray(resultJson)
                    projects.clear()
                    for(i in 0 until json.length()) {
                        val item = json.getJSONObject(i)
                        projects.add(Project(item))
                        //Dirty hack here, b careful vvv
                        runOnUiThread {
                            val adapter = findViewById<ListView>(R.id.listView).adapter as BaseAdapter
                            adapter.notifyDataSetChanged()
                        }
                    }
                } else {
                    return
                }
            }
        })

    }

}
