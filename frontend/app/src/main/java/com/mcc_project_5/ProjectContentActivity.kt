package com.mcc_project_5

import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.mcc_project_5.Adapters.FileListAdapter
import com.mcc_project_5.Adapters.PictureListAdapter
import com.mcc_project_5.Adapters.TaskListAdapter
import com.mcc_project_5.DataModels.File
import com.mcc_project_5.DataModels.Picture
import com.mcc_project_5.DataModels.Task
import org.json.JSONArray

class ProjectContentActivity : AppCompatActivity() {
    private val tasks  =  arrayListOf<Task>()
    private val visibleTasks = arrayListOf<Task>()

    private val pictures  =  arrayListOf<Picture>()
    private val visiblePictures = arrayListOf<Picture>()

    private val files  =  arrayListOf<File>()
    private val visibleFiles = arrayListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.project_content_activity)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.project_content_general)
        toolbar.setTitle("Project123")
        setSupportActionBar(toolbar)

        val taskListAdapter = TaskListAdapter()
        taskListAdapter.setItems(visibleTasks)
        val taskListView = findViewById<ListView>(R.id.taskListView)
        taskListView.adapter = taskListAdapter

        val pictureListAdapter = PictureListAdapter()
        pictureListAdapter.setItems(visiblePictures)
        val pictureListView = findViewById<ListView>(R.id.picturesListView)
        pictureListView.adapter = pictureListAdapter

        val fileListAdapter = FileListAdapter()
        fileListAdapter.setItems(visibleFiles)
        val fileListView = findViewById<ListView>(R.id.filesListView)
        fileListView.adapter = fileListAdapter

        findViewById<Button>(R.id.tasks).performClick()
    }

    fun refreshTaskList() {
        val adapter = findViewById<ListView>(R.id.taskListView).adapter as TaskListAdapter
        adapter.notifyDataSetChanged()
    }

    fun refreshPictureList() {
        val adapter = findViewById<ListView>(R.id.picturesListView).adapter as PictureListAdapter
        adapter.notifyDataSetChanged()
    }

    fun refreshFileList() {
        val adapter = findViewById<ListView>(R.id.filesListView).adapter as FileListAdapter
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater = menuInflater
        inflater.inflate(R.menu.project_content_general, menu)
        //inflater.inflate(R.menu.filter, menu)

        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                visibleTasks.clear()
                tasks.forEach {
                    if (it.title.contains(newText!!, true)) {
                        visibleTasks.add(it)
                    }
                }
                refreshTaskList()
                return true
            }

        })
        return true
    }

    fun loadTasksTemplate() {
        val testJson = "[{\"id\":1, \"title\":\"Title1\", \"done\":false}, {\"id\":2, \"title\":\"Title2\", \"done\":true}, {\"id\":3, \"title\":\"Title1\", \"done\":false}]"
        val json = JSONArray(testJson)
        tasks.clear()
        for(i in 0 until json.length()) {
            val item = json.getJSONObject(i)
            val task = Task(item)
            tasks.add(task)
        }
        visibleTasks.clear()
        visibleTasks.addAll(tasks)
        refreshTaskList()
    }

    fun loadPicturesTemplate() {
        val testJson = "[{\"id\":1, \"createdAt\":\"01.01.01\", \"description\":\"random random random\", \"imageUrl\":\"aHR0cHM6Ly9wYnMudHdpbWcuY29tL3Byb2ZpbGVfaW1hZ2VzLzQ4ODU0MDk4MjUzOTg0OTcyOC9CODl0MzVzNS5qcGVn\"}, {\"id\":2, \"createdAt\":\"01.01.02\", \"description\":\"random random random\", \"imageUrl\":\"aHR0cHM6Ly9wYnMudHdpbWcuY29tL3Byb2ZpbGVfaW1hZ2VzLzQ4ODU0MDk4MjUzOTg0OTcyOC9CODl0MzVzNS5qcGVn\"}, {\"id\":3, \"createdAt\":\"01.01.03\", \"description\":\"random random random\", \"imageUrl\":\"aHR0cHM6Ly9wYnMudHdpbWcuY29tL3Byb2ZpbGVfaW1hZ2VzLzQ4ODU0MDk4MjUzOTg0OTcyOC9CODl0MzVzNS5qcGVn\"}]"
        val json = JSONArray(testJson)
        pictures.clear()
        for(i in 0 until json.length()) {
            val item = json.getJSONObject(i)
            val picture = Picture(item)
            pictures.add(picture)
        }
        visiblePictures.clear()
        visiblePictures.addAll(pictures)
        refreshPictureList()
    }

    fun loadFilesTemplate() {
        val testJson = "[{\"id\":1, \"title\":\"file1.pdf\", \"createdAt\":\"01.01.01\", \"url\":\"aHR0cHM6Ly9wYnMudHdpbWcuY29tL3Byb2ZpbGVfaW1hZ2VzLzQ4ODU0MDk4MjUzOTg0OTcyOC9CODl0MzVzNS5qcGVn\"}, {\"id\":2, \"title\":\"file2.doc\", \"createdAt\":\"01.01.02\", \"url\":\"aHR0cHM6Ly9wYnMudHdpbWcuY29tL3Byb2ZpbGVfaW1hZ2VzLzQ4ODU0MDk4MjUzOTg0OTcyOC9CODl0MzVzNS5qcGVn\"}, {\"id\":3, \"title\":\"file1.jpeg\", \"createdAt\":\"01.01.03\", \"url\":\"aHR0cHM6Ly9wYnMudHdpbWcuY29tL3Byb2ZpbGVfaW1hZ2VzLzQ4ODU0MDk4MjUzOTg0OTcyOC9CODl0MzVzNS5qcGVn\"}]"
        val json = JSONArray(testJson)
        files.clear()
        for(i in 0 until json.length()) {
            val item = json.getJSONObject(i)
            val file = File(item)
            files.add(file)
        }
        visibleFiles.clear()
        visibleFiles.addAll(files)
        refreshFileList()
    }

    fun loadTemplate() {
        loadTasksTemplate()
        loadPicturesTemplate()
        loadFilesTemplate()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.title == "refresh") {
            loadTemplate()
        }

        return true
    }

    fun onTasksButtonClick(v: View) {
        findViewById<ListView>(R.id.taskListView).visibility = View.VISIBLE
        findViewById<ListView>(R.id.picturesListView).visibility = View.GONE
        findViewById<ListView>(R.id.filesListView).visibility = View.GONE
        findViewById<Button>(R.id.tasks).setTextColor(getColor(R.color.textColorSelected))
        findViewById<Button>(R.id.files).setTextColor(getColor(R.color.textColorPrimary))
        findViewById<Button>(R.id.pictures).setTextColor(getColor(R.color.textColorPrimary))
    }

    fun onPicturesButtonClick(v: View) {
        findViewById<ListView>(R.id.taskListView).visibility = View.GONE
        findViewById<ListView>(R.id.picturesListView).visibility = View.VISIBLE
        findViewById<ListView>(R.id.filesListView).visibility = View.GONE
        findViewById<Button>(R.id.tasks).setTextColor(getColor(R.color.textColorPrimary))
        findViewById<Button>(R.id.files).setTextColor(getColor(R.color.textColorPrimary))
        findViewById<Button>(R.id.pictures).setTextColor(getColor(R.color.textColorSelected))
    }

    fun onFilesButtonClick(v: View) {
        findViewById<ListView>(R.id.taskListView).visibility = View.GONE
        findViewById<ListView>(R.id.picturesListView).visibility = View.GONE
        findViewById<ListView>(R.id.filesListView).visibility = View.VISIBLE
        findViewById<Button>(R.id.tasks).setTextColor(getColor(R.color.textColorPrimary))
        findViewById<Button>(R.id.files).setTextColor(getColor(R.color.textColorSelected))
        findViewById<Button>(R.id.pictures).setTextColor(getColor(R.color.textColorPrimary))
    }
}