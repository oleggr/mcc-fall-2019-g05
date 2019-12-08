package com.mcc_project_5

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toFile
import com.google.firebase.auth.FirebaseAuth
import com.mcc_project_5.Adapters.FileListAdapter
import com.mcc_project_5.Adapters.PictureListAdapter
import com.mcc_project_5.Adapters.TaskListAdapter
import com.mcc_project_5.DataModels.File
import com.mcc_project_5.DataModels.Picture
import com.mcc_project_5.DataModels.Project
import com.mcc_project_5.DataModels.Task
import com.mcc_project_5.Tools.Requester
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.Nameable
import kotlinx.android.synthetic.main.project_content_activity.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.UnknownHostException
import java.util.*
import kotlin.Comparator

class ProjectContentActivity : AppCompatActivity() {

    private var result: Drawer? = null
    private lateinit var auth: FirebaseAuth

    private val tasks  =  arrayListOf<Task>()
    private val visibleTasks = arrayListOf<Task>()

    private val pictures  =  arrayListOf<Picture>()
    private val visiblePictures = arrayListOf<Picture>()

    private val files  =  arrayListOf<File>()
    private val visibleFiles = arrayListOf<File>()

    private var projectId = ""

    private enum class Sort {
        BY_FILE, BY_PICTURE, BY_TASK, NONE
    }

    private var sorting: Sort = Sort.NONE

    private enum class SortOrder {
        ASC, DESC
    }

    private var sortOrder: SortOrder = SortOrder.DESC

    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private val IMAGE_PICK_CODE = 1001
    private val FILE_UPLOAD_CODE = 1001
    var file_uri: Uri? = null
    private val fileUrl = ""


    class ComparatorByTimeFile: Comparator<File>{
        override fun compare(o1: File?, o2: File?): Int {
            if(o1 == null || o2 == null){
                return 0;
            }
            return o1.createdAt.compareTo(o2.createdAt)
        }
    }

    class ComparatorByTimePicture: Comparator<Picture>{
        override fun compare(o1: Picture?, o2: Picture?): Int {
            if(o1 == null || o2 == null){
                return 0;
            }
            return o1.createdAt.compareTo(o2.createdAt)
        }
    }

    class ComparatorByTimeTask: Comparator<Task>{
        override fun compare(o1: Task?, o2: Task?): Int {
            if(o1 == null || o2 == null){
                return 0;
            }
            return o1.createdAt.compareTo(o2.createdAt)
        }
    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.project_content_activity)

        projectId = this.intent.getStringExtra("projectId")
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.project_content_general)
        toolbar.setTitle(projectId)
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
                                val intent = Intent(this@ProjectContentActivity, ProfileSettingsActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                            }
                            R.id.projects -> {
                                val intent = Intent(this@ProjectContentActivity, ListOfCreatedProjectsActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                            }
                            R.id.logout -> {
                                auth.signOut()
                                val intent = Intent(this@ProjectContentActivity, LoginActivity::class.java)
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

    }

    fun attachment(v: View) {
        val popupMenu: PopupMenu = PopupMenu(this,fab)
        popupMenu.menuInflater.inflate(R.menu.attachment, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.action_user -> {
                    val intent = Intent(this, AddUserActivity::class.java)
                    startActivity(intent)
                }
                R.id.action_task -> {
                    val intent = Intent(this, AddTasksToAProjectActivity::class.java)
                    intent.putExtra("projectId", projectId)
                    startActivity(intent)
                }
                R.id.action_photo ->
                    openCamera()
                R.id.action_gallery ->
                    pickImageFromGallery()
                R.id.action_document ->
                    openFile()
                R.id.action_cancel ->
                    Toast.makeText(this@ProjectContentActivity, "Canceled", Toast.LENGTH_SHORT).show()
            }
            true
        })
        popupMenu.show()
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
                    if (it.description.contains(newText!!, true)) {
                        visibleTasks.add(it)
                    }
                }
                refreshTaskList()
                return true
            }

        })
        return true
    }

    fun loadTasksTemplate(id: String) {
        val requester = Requester(baseContext)
        requester.httpGet("project/$id/tasks", object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("DDD", "FAIL")
                return
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val resultJson = response.body!!.string()
                    Log.d("DDD", resultJson)
                    val json = JSONArray(resultJson)
                    tasks.clear()
                    for(i in 0 until json.length()) {
                        val item = json.getJSONObject(i)
                        val task = Task(item)
                        tasks.add(task)
                    }
                    visibleTasks.clear()
                    visibleTasks.addAll(tasks)
                    runOnUiThread {
                        refreshTaskList()
                    }
                } else {
                    return
                }
            }
        })
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

    fun loadTemplate(id: String) {
        loadTasksTemplate(id)
        loadPicturesTemplate()
        loadFilesTemplate()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.title == "refresh") {
            loadTemplate(projectId)
        } else if (item.title == "sort") {
            if (sortOrder == SortOrder.DESC) {
                item.setIcon(R.drawable.ic_sort_reversed_black_24dp)
                item.icon.layoutDirection = View.LAYOUT_DIRECTION_RTL
                sortOrder = SortOrder.ASC
                performSorting()
            } else {
                item.setIcon(R.drawable.ic_sort_black_24dp)
                item.icon.layoutDirection = View.LAYOUT_DIRECTION_LTR
                sortOrder = SortOrder.DESC
                performSorting()
            }
        }

        return true
    }

    fun performSorting() {
        when(sorting) {
            Sort.BY_TASK -> {
                Collections.sort(visibleTasks, ComparatorByTimeTask())
                if (sortOrder == SortOrder.DESC)
                    visibleTasks.reverse()
                refreshTaskList()
            }
            Sort.BY_FILE -> {
                Collections.sort(visibleFiles, ComparatorByTimeFile())
                if (sortOrder == SortOrder.DESC)
                    visibleFiles.reverse()
                refreshFileList()
            }
            Sort.BY_PICTURE -> {
                Collections.sort(visiblePictures, ComparatorByTimePicture())
                if (sortOrder == SortOrder.DESC)
                    visiblePictures.reverse()
                refreshPictureList()
            }
        }
    }

    fun onTasksButtonClick(v: View) {
        sorting = Sort.BY_TASK
        performSorting()
        findViewById<ListView>(R.id.taskListView).visibility = View.VISIBLE
        findViewById<ListView>(R.id.picturesListView).visibility = View.GONE
        findViewById<ListView>(R.id.filesListView).visibility = View.GONE
        findViewById<Button>(R.id.tasks).setTextColor(getColor(R.color.textColorSelected))
        findViewById<Button>(R.id.files).setTextColor(getColor(R.color.textColorPrimary))
        findViewById<Button>(R.id.pictures).setTextColor(getColor(R.color.textColorPrimary))
    }

    fun onPicturesButtonClick(v: View) {
        sorting = Sort.BY_PICTURE
        performSorting()
        findViewById<ListView>(R.id.taskListView).visibility = View.GONE
        findViewById<ListView>(R.id.picturesListView).visibility = View.VISIBLE
        findViewById<ListView>(R.id.filesListView).visibility = View.GONE
        findViewById<Button>(R.id.tasks).setTextColor(getColor(R.color.textColorPrimary))
        findViewById<Button>(R.id.files).setTextColor(getColor(R.color.textColorPrimary))
        findViewById<Button>(R.id.pictures).setTextColor(getColor(R.color.textColorSelected))
    }

    fun onFilesButtonClick(v: View) {
        sorting = Sort.BY_FILE
        performSorting()
        findViewById<ListView>(R.id.taskListView).visibility = View.GONE
        findViewById<ListView>(R.id.picturesListView).visibility = View.GONE
        findViewById<ListView>(R.id.filesListView).visibility = View.VISIBLE
        findViewById<Button>(R.id.tasks).setTextColor(getColor(R.color.textColorPrimary))
        findViewById<Button>(R.id.files).setTextColor(getColor(R.color.textColorSelected))
        findViewById<Button>(R.id.pictures).setTextColor(getColor(R.color.textColorPrimary))
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        file_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, file_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    private fun openFile() {
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(Intent.createChooser(intent, "Select a file"), FILE_UPLOAD_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //called when user presses ALLOW or DENY from Permission Request Popup
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    openCamera()
                }
                else{
                    //permission from popup was denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun takePhoto()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
                //permission was not enabled
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                //show popup to request permission
                requestPermissions(permission, PERMISSION_CODE)
            }
            else{
                //permission already granted
                openCamera()
            }
        }
        else{
            //system os is < marshmallow
            openCamera()
        }
    }


    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun uploadImage(file: java.io.File, ext: String): JSONObject? {

        try {

            val MEDIA_TYPE = ("jpg/pdf/txt/mp3").toMediaType()

            val req = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("userid", "8457851245")
                .addFormDataPart(
                    "userfile",
                    "profile." + ext,
                    RequestBody.create(MEDIA_TYPE, file)
                ).build()

            val request = Request.Builder()
                .url(fileUrl)
                .post(req)
                .build()

            val client = OkHttpClient()
            val response = client.newCall(request).execute()

            Log.d("response", "uploadImage:" + response.body!!.string())

            return JSONObject(response.body!!.string())

        } catch (e: UnknownHostException) {
            System.err.println( "Error: " + e.getLocalizedMessage())
        } catch (e: UnsupportedEncodingException) {
            System.err.println( "Error: " + e.getLocalizedMessage())
        } catch (e: Exception) {
            System.err.println( "Error: " + e.localizedMessage!!)
        }

        return null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == FILE_UPLOAD_CODE && resultCode == RESULT_OK) || (resultCode == Activity.RESULT_OK)) {
            if(data == null || data.data == null){
                return
            }
            file_uri = data?.data //The uri with the location of the file
            val extension = MimeTypeMap.getFileExtensionFromUrl(file_uri.toString())
            if (extension != null) {
                val ext = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                uploadImage(file_uri!!.toFile(), ext.toString())
            }
        }
    }
}