package com.mcc_project_5

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.mcc_project_5.Tools.FileStorage
import com.mcc_project_5.Tools.ImageStorage
import com.mcc_project_5.Tools.Requester
import kotlinx.android.synthetic.main.activity_create_a_project.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*
import java.text.SimpleDateFormat


class CreateAProjectActivity : AppCompatActivity() {

    lateinit var imageView: ImageView
    private var upload: Button? = null
    private val GALLERY = 1

    var button_date: Button? = null
    var textview_date: TextView? = null
    var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_a_project)

        val mUser = FirebaseAuth.getInstance().currentUser ?: finish()

        imageView = findViewById(R.id.imageView)
        upload = findViewById<View>(R.id.upload) as Button
        upload!!.setOnClickListener { choosePhotoFromGallary()}

        // get the references from layout file
        textview_date = this.text_view_date_1
        button_date = this.button_date_1

        textview_date!!.text = "--/--/----"

        // create an OnDateSetListener
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        val mPickTimeBtn = findViewById<Button>(R.id.pickTimeBtn)
        val textView= findViewById<TextView>(R.id.timeTv)

        mPickTimeBtn.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour,minute  ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                //cal.set(Calendar.SECOND, second)
                textView.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }


        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        button_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@CreateAProjectActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }

        })
    }

    private fun choosePhotoFromGallary() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            imageView.setImageURI(data!!.data)

        }
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textview_date!!.text = sdf.format(cal.getTime())
    }

    fun createProject(v: View) {
        var requester = Requester(baseContext)
        val json= JSONObject()
        json.put("title",project_name.text.toString())
        json.put("description",project_description.text.toString())
        json.put("is_shared",checkBoxProject.isChecked)
        val deadline = text_view_date_1.text.toString() + " " + timeTv.text.toString()
        json.put("deadline",deadline)
        val keywords = add_keywords.text.split(" ").toTypedArray()
        val jsonKeywords = JSONArray(keywords)
        json.put("key_words",jsonKeywords)

        /*var file_uri = imageView.getTag().toString()
        val extension = MimeTypeMap.getFileExtensionFromUrl(file_uri.toString())
        val ext = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        requester.httpPostWithFile("projects/create", json, file_uri!!.toFile(), ext.toString(), object: Callback {
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
        })*/
        //val link = ImageStorage(this).saveImageToStorage((imageView.drawable as BitmapDrawable).bitmap)
        //val originalName = "project_icon"
        //System.err.println(originalName)
        //System.err.println(link)
        requester.httpPost("project/create", json, object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("DDD", "FAIL")
                return
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val projectId = response.body!!.string()
                    Log.d("DDD","CREATED" + projectId)
                        /*requester.httpPost("project/$projectId/set_icon", JSONObject("{\"url\":\"$link\"}"), object: Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                System.err.println("Image FAILED update")
                            }

                            override fun onResponse(call: Call, response: Response) {
                                System.err.println("Image successfully updated")
                            }

                        })*/
                } else {
                    Log.d("DDD","NOT OK")
                    return
                }
            }
        })
        //MAY BE TEMPORARY SOLUTION
        //finish()
        val intent = Intent(this@CreateAProjectActivity, ListOfCreatedProjectsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}
