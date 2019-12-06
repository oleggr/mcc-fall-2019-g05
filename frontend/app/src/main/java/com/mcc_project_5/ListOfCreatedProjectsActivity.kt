package com.mcc_project_5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.PopupMenu
import androidx.appcompat.widget.Toolbar

import com.mcc_project_5.DataModels.Project
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import com.mcc_project_5.Adapters.ProjectListAdapter
import com.mcc_project_5.Tools.Properties
import kotlinx.android.synthetic.main.activity_list_of_created_projects.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class ListOfCreatedProjectsActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private val projects = ArrayList<Project>()
    private var lastClicked = 0
    private var sortOrder = SortOrder.DESC

    private enum class Sort {
        BY_FAVORITE, BY_TIME, BY_DEADLINE, NONE
    }

    private enum class SortOrder {
        ASC, DESC
    }
    private var sorting: Sort = Sort.NONE

    class ComparatorByFavorite: Comparator<Project>{
        override fun compare(o1: Project?, o2: Project?): Int {
            if(o1 == null || o2 == null){
                return 0;
            }
            return o1.isFavorite.compareTo(o2.isFavorite)
        }
    }

    class ComparatorByTime: Comparator<Project>{
        override fun compare(o1: Project?, o2: Project?): Int {
            if(o1 == null || o2 == null){
                return 0;
            }
            return o1.lastModified.compareTo(o2.lastModified)
        }
    }

    class ComparatorByDeadline: Comparator<Project>{
        override fun compare(o1: Project?, o2: Project?): Int {
            if(o1 == null || o2 == null){
                return 0;
            }
            return o1.deadline.compareTo(o2.deadline)
        }
    }

    fun refreshList() {
        val adapter = findViewById<ListView>(R.id.listView).adapter as BaseAdapter
        adapter.notifyDataSetChanged()
    }

    fun performSorting() {
        when(sorting) {
            Sort.BY_FAVORITE -> Collections.sort(projects, ComparatorByFavorite())
            Sort.BY_TIME -> Collections.sort(projects, ComparatorByTime())
            Sort.BY_DEADLINE -> Collections.sort(projects, ComparatorByDeadline())
        }
        if (sortOrder == SortOrder.DESC)
            projects.reverse()
        refreshList()
    }

    fun onSortByDeadline(v: View) {
        if (sorting == Sort.BY_DEADLINE){
            return
        }
        sorting = Sort.BY_DEADLINE
        findViewById<ImageButton>(R.id.byDeadline).setImageResource(R.drawable.ic_timer_white_24dp)
        findViewById<ImageButton>(R.id.byFavorite).setImageResource(R.drawable.ic_star_black_24dp)
        findViewById<ImageButton>(R.id.byTime).setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp)
        performSorting()
    }

    fun onSortByTime(v: View) {
        if (sorting == Sort.BY_TIME){
            return
        }
        sorting = Sort.BY_TIME
        findViewById<ImageButton>(R.id.byDeadline).setImageResource(R.drawable.ic_timer_black_24dp)
        findViewById<ImageButton>(R.id.byFavorite).setImageResource(R.drawable.ic_star_black_24dp)
        findViewById<ImageButton>(R.id.byTime).setImageResource(R.drawable.ic_radio_button_unchecked_white_24dp)
        performSorting()
    }

    fun onSortByFavorite(v: View) {
        if (sorting == Sort.BY_FAVORITE){
            return
        }
        sorting = Sort.BY_FAVORITE
        findViewById<ImageButton>(R.id.byDeadline).setImageResource(R.drawable.ic_timer_black_24dp)
        findViewById<ImageButton>(R.id.byFavorite).setImageResource(R.drawable.ic_star_white_24dp)
        findViewById<ImageButton>(R.id.byTime).setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp)
        performSorting()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_created_projects)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.general)
        toolbar.setTitle("Projects")
        setSupportActionBar(toolbar)

        /*findViewById<ListView>(R.id.listView).setOnItemClickListener{ _, _, position, _ ->
            val intent = Intent(this@ListOfCreatedProjectsActivity, Main2Activity::class.java)
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

    //android:onClick="loadTemplate"

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater = menuInflater
        inflater.inflate(R.menu.general, menu)
        //inflater.inflate(R.menu.filter, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.title == "refresh") {
            loadTemplate()
        } else if (item.title == "sort") {
            if (sortOrder == SortOrder.DESC) {
                item.setIcon(R.drawable.ic_sort_reversed_black_24dp)
                item.icon.layoutDirection = View.LAYOUT_DIRECTION_RTL
                sortOrder = SortOrder.ASC
                projects.reverse()
                refreshList()
            } else {
                item.setIcon(R.drawable.ic_sort_black_24dp)
                item.icon.layoutDirection = View.LAYOUT_DIRECTION_LTR
                sortOrder = SortOrder.DESC
                projects.reverse()
                refreshList()
            }
        }

        return true
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


    fun loadTemplate() {
        val testJson = "[{\"id\":1,\"title\":\"xxx\",\"deadline\":\"02.02.02\",\"imageUrl\":\"\",\"lastModified\":\"01.01.01\",\"isFavorite\":true,\"isMediaAvailable\":false,\"members\":[{\"id\":1,\"imageUrl\":\"\"}, {\"id\":2,\"imageUrl\":\"aHR0cHM6Ly9tZWRpYWxlYWtzLnJ1L3dwLWNvbnRlbnQvdXBsb2Fkcy8yMDE4LzExL1NhaS0tdC1BcnRlLS1tLTI3My5qcGc=\"}]}, {\"id\":2,\"title\":\"yyy\",\"deadline\":\"01.01.01\",\"imageUrl\":\"aHR0cHM6Ly9wYnMudHdpbWcuY29tL3Byb2ZpbGVfaW1hZ2VzLzQ4ODU0MDk4MjUzOTg0OTcyOC9CODl0MzVzNS5qcGVn\",\"lastModified\":\"02.02.02\",\"isFavorite\":false,\"isMediaAvailable\":true,\"members\":[{\"id\":2,\"imageUrl\":\"aHR0cHM6Ly9pLnBpbmltZy5jb20vb3JpZ2luYWxzL2YzL2UxL2I4L2YzZTFiODAxOWYxNjBmODg1MzFkOGFmNzkyNzE2YjRmLnBuZw==\"}]}]"
        val json = JSONArray(testJson)
        projects.clear()
        for(i in 0 until json.length()) {
            val item = json.getJSONObject(i)
            val project = Project(item)
            projects.add(project)
        }
        refreshList()
        this.byTime.performClick()
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
                            refreshList()
                        }
                    }
                } else {
                    return
                }
            }
        })

    }

}
