package com.mcc_project_5

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar

import com.mcc_project_5.DataModels.Project
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.mcc_project_5.Adapters.ProjectListAdapter
import com.mcc_project_5.Tools.Requester
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.Nameable
import kotlinx.android.synthetic.main.list_of_created_projects_activity.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList




class ListOfCreatedProjectsActivity : AppCompatActivity() {

    private var result: Drawer? = null

    private val projects = ArrayList<Project>()
    private val visibleProjects = ArrayList<Project>()
    private var lastClicked = 0
    private var sortOrder = SortOrder.DESC
    private lateinit var auth: FirebaseAuth

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
            Sort.BY_FAVORITE -> Collections.sort(visibleProjects, ComparatorByFavorite())
            Sort.BY_TIME -> Collections.sort(visibleProjects, ComparatorByTime())
            Sort.BY_DEADLINE -> Collections.sort(visibleProjects, ComparatorByDeadline())
        }
        if (sortOrder == SortOrder.DESC)
            visibleProjects.reverse()
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
        setContentView(R.layout.list_of_created_projects_activity)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.general)
        toolbar.setTitle("Projects")
        setSupportActionBar(toolbar)

        auth = FirebaseAuth.getInstance()

        result = DrawerBuilder()
            .withActivity(this)
            .withToolbar(toolbar)
            .inflateMenu(R.menu.navigation)
            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                    if (drawerItem is Nameable<*>) {
                        when (drawerItem.identifier.toInt()) {
                            R.id.profile -> {
                                val intent = Intent(this@ListOfCreatedProjectsActivity, ProfileSettingsActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                            }
                            R.id.projects -> {
                                val intent = Intent(this@ListOfCreatedProjectsActivity, ListOfCreatedProjectsActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                            }
                            R.id.logout -> {
                                auth.signOut()
                                val intent = Intent(this@ListOfCreatedProjectsActivity, LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                            }
                        }
                    }

                    return false
                }
            })
            .withSelectedItemByPosition(1)
            .build()

        /*findViewById<ListView>(R.id.listView).setOnItemClickListener{ _, _, position, _ ->
            val intent = Intent(this@ListOfCreatedProjectsActivity, Main2Activity::class.java)
            val item = photoList[position]
            intent.putExtra("author", item.author)
            // low preformance, better way - get from cache
            intent.putExtra("url", item.photoUrl)
            startActivity(intent)
        }*/

        val listAdapter = ProjectListAdapter()
        listAdapter.setItems(visibleProjects)
        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = listAdapter
        listView.setOnItemClickListener { _, _, position, _ ->
            lastClicked = position
        }

        onLoadBtnClick()

        this.byTime.performClick()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater = menuInflater
        inflater.inflate(R.menu.general, menu)
        //inflater.inflate(R.menu.filter, menu)

        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                visibleProjects.clear()
                projects.forEach {
                    var isKeyword = false
                    for(str in it.keywordsArray) {
                        if (str.contains(newText!!, true)) {
                            isKeyword = true
                            break
                        }
                    }
                    if (it.title.contains(newText!!, true) || isKeyword) {
                        visibleProjects.add(it)
                    }
                }
                refreshList()
                return true
            }

        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.title == "refresh") {
            onLoadBtnClick()
        } else if (item.title == "sort") {
            if (sortOrder == SortOrder.DESC) {
                item.setIcon(R.drawable.ic_sort_reversed_black_24dp)
                item.icon.layoutDirection = View.LAYOUT_DIRECTION_RTL
                sortOrder = SortOrder.ASC
                visibleProjects.reverse()
                refreshList()
            } else {
                item.setIcon(R.drawable.ic_sort_black_24dp)
                item.icon.layoutDirection = View.LAYOUT_DIRECTION_LTR
                sortOrder = SortOrder.DESC
                visibleProjects.reverse()
                refreshList()
            }
        }

        return true
    }



    fun loadTemplate() {
        val testJson = "[{\"id\":1,\"title\":\"xxx\",\"deadline\":\"02.02.02\",\"imageUrl\":\"\",\"lastModified\":\"01.01.01\",\"isFavorite\":true,\"isMediaAvailable\":false,\"keywords\":[\"asd\", \"qwe\"],\"members\":[{\"id\":1,\"imageUrl\":\"\"}, {\"id\":2,\"imageUrl\":\"aHR0cHM6Ly9tZWRpYWxlYWtzLnJ1L3dwLWNvbnRlbnQvdXBsb2Fkcy8yMDE4LzExL1NhaS0tdC1BcnRlLS1tLTI3My5qcGc=\"}]}, {\"id\":2,\"title\":\"yyy\",\"deadline\":\"01.01.01\",\"imageUrl\":\"aHR0cHM6Ly9wYnMudHdpbWcuY29tL3Byb2ZpbGVfaW1hZ2VzLzQ4ODU0MDk4MjUzOTg0OTcyOC9CODl0MzVzNS5qcGVn\",\"lastModified\":\"02.02.02\",\"isFavorite\":false,\"isMediaAvailable\":true,\"keywords\":[\"asd\", \"bnm\"],\"members\":[{\"id\":2,\"imageUrl\":\"aHR0cHM6Ly9pLnBpbmltZy5jb20vb3JpZ2luYWxzL2YzL2UxL2I4L2YzZTFiODAxOWYxNjBmODg1MzFkOGFmNzkyNzE2YjRmLnBuZw==\"}]}]"
        val json = JSONArray(testJson)
        projects.clear()
        for(i in 0 until json.length()) {
            val item = json.getJSONObject(i)
            val project = Project(item)
            projects.add(project)
        }
        visibleProjects.clear()
        visibleProjects.addAll(projects)
        refreshList()
        this.byTime.performClick()
        Log.d("DDD", projects.toString())
    }

    fun createProject(v: View) {
        val intent = Intent(this, CreateAProjectActivity::class.java)
        startActivity(intent)
    }

    fun onLoadBtnClick() {
        val requester = Requester(baseContext)
        requester.httpGet("projects", object:Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("DDD", "FAIL")
                return
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val resultJson = response.body!!.string()
                    Log.d("DDD", resultJson)
                    val json = JSONArray(resultJson)
                    projects.clear()
                    for(i in 0 until json.length()) {
                        val item = json.getJSONObject(i)
                        projects.add(Project(item))
                        //Dirty hack here, b careful vvv
                        visibleProjects.clear()
                        visibleProjects.addAll(projects)
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
