package com.mcc_project_5

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ListView
import com.mcc_project_5.DataModels.Project
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class ListOfCreatedProjects : AppCompatActivity() {

    private val client = OkHttpClient()
    private val projects = ArrayList<Project>()

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

        val listAdapter = SimpleListAdapter()
        listAdapter.setItems(projects)
        findViewById<ListView>(R.id.listView).adapter = listAdapter
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

    fun loadTemplate(view: View) {
        val testJson = "[{\"id\":1,\"title\":\"xxx\",\"imageUrl\":\"\",\"lastModified\":\"01.01.01\",\"isFavorite\":false,\"members\":[{\"id\":1}]}, {\"id\":2,\"title\":\"yyy\",\"imageUrl\":\"\",\"lastModified\":\"02.02.02\",\"isFavorite\":false,\"members\":[{\"id\":2}]}]"
        val json = JSONArray(testJson)
        projects.clear()
        for(i in 0 until json.length()) {
            val item = json.getJSONObject(i)
            projects.add(Project(item))
        }
        val adapter = findViewById<ListView>(R.id.listView).adapter as BaseAdapter
        adapter.notifyDataSetChanged()
        Log.d("DDD", projects.toString())
    }

    fun onLoadBtnClick(view: View) {
        val baseUrl = Properties(baseContext).getProperty("baseUrl")
        httpGet(baseUrl, object:Callback {
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
