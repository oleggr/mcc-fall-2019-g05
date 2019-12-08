package com.mcc_project_5

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.mcc_project_5.DataModels.ProjectMember
import com.mcc_project_5.DataModels.User
import com.mcc_project_5.Tools.Requester
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_add_tasks_to_a_project.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth


class AddTasksToAProjectActivity : AppCompatActivity() {

    var button_date: Button? = null
    var textview_date: TextView? = null
    var cal = Calendar.getInstance()
    private lateinit var projectId: String
    private lateinit var title: String
    var users = ArrayList<ProjectMember>()
    var chosenUserId = ""
    var isOwner = false
    var isShared = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tasks_to_a_project)

        val mUser = FirebaseAuth.getInstance().currentUser ?: finish()

        // get the references from layout file
        textview_date = this.text_view_date_1
        button_date = this.button_date_1

        textview_date!!.text = "--/--/----"

        projectId = this.intent.getStringExtra("projectId")
        title = this.intent.getStringExtra("title")
        isShared = this.intent.getBooleanExtra("isShared", false)

        isOwner = this.intent.getBooleanExtra("isOwner", false)
        chosen.text = ""


        if (!isOwner) {
            assign_text_view.isVisible = false
            chosen.isVisible = false
        }
        if (isOwner) {
            val requester  = Requester(baseContext)
            requester.httpGet("project/$projectId/members", object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("DDD", "FAIL")
                    return
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val resultJson = response.body!!.string()
                        Log.d("Log", resultJson)
                        val json = JSONArray(resultJson)
                        for(i in 0 until json.length()) {
                            val item = json.getJSONObject(i)
                            val user = ProjectMember(item)
                            users.add(user)
                        }
                    } else {
                        return
                    }
                }
            })
        }


        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        button_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@AddTasksToAProjectActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }

        })

        val mPickTimeBtn = findViewById<Button>(R.id.pickTimeBtn)
        val textView= findViewById<TextView>(R.id.timeTv)


        mPickTimeBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute  ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                //cal.set(Calendar.SECOND, second)
                textView.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

    }



    fun onClick(v: View) {

        Log.d("users", users.toString())
        if (users.isEmpty()) {
            return
        }
        val assign = PopupMenu(this@AddTasksToAProjectActivity, v)
        for ( user in users) {
            assign.menu.add(user.name).setOnMenuItemClickListener {
                chosenUserId = user.user_id
                chosen.text = user.name
                Log.d("change_assignee ", chosenUserId)
                true
            }

        }
        assign.menuInflater.inflate(R.menu.asigned, assign.menu)
        assign.show()
    }

    fun selectImage(v: View) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Select Attachment"), 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data!!.data)

            startRecognizing(bitmap)
        }
    }

    fun startRecognizing(bitmap: Bitmap) {
        if (bitmap != null) {
            project_description.setText("")
            val image = FirebaseVisionImage.fromBitmap(bitmap)
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

            detector.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
                    processResultText(firebaseVisionText)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "Select an Image First", Toast.LENGTH_LONG).show()
        }

    }


    private fun processResultText(resultText: FirebaseVisionText) {
        if (resultText.textBlocks.size == 0) {
            Toast.makeText(this, "No text found", Toast.LENGTH_LONG).show()
            return
        }
        for (block in resultText.textBlocks) {
            val blockText = block.text
            project_description.append(blockText)
        }
    }

    fun createTask(v: View) {
        var requester = Requester(baseContext)
        val json= JSONObject()
        json.put("description",project_description.text.toString())
        val deadline = text_view_date_1.text.toString() + " " + timeTv.text.toString()
        json.put("deadline",deadline)
        if (isOwner && chosenUserId != "") {

            json.put("assignee_id", chosenUserId)
        }


        requester.httpPost("project/$projectId/tasks/add", json, object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("DDD", "FAIL")
                return
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("DDD","OK" + response.message)
                } else {
                    Log.d("DDD","NOT OK" + response.message)
                    return
                }
            }
        })
        //MAY BE TEMPORARY SOLUTION
        //finish()
        val intent = Intent(this@AddTasksToAProjectActivity, ProjectContentActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("projectId", projectId)
        intent.putExtra("title", title)
        intent.putExtra("isShared", isShared)
        startActivity(intent)
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textview_date!!.text = sdf.format(cal.getTime())
    }

}
