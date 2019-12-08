package com.mcc_project_5

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.mcc_project_5.Tools.Requester
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    var names = arrayListOf<String>()
    private val GALLERY = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        imageView.setOnClickListener { choosePhotoFromGallary()}
    }

    fun signUpUser(v: View) {
        val requester = Requester(baseContext)

        if (tv_username.text.toString().isEmpty()) {
            tv_username.error = "Please enter username"
            tv_username.requestFocus()
            return
        }

        if (tv_email.text.toString().isEmpty()) {
            tv_email.error = "Please enter email"
            tv_email.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(tv_email.text.toString()).matches()) {
            tv_email.error = "Please enter valid email"
            tv_email.requestFocus()
            return
        }

        if (tv_password.text.toString().isEmpty()) {
            tv_password.error = "Please enter password"
            tv_password.requestFocus()
            return
        }
        var username = tv_username.text.toString()

        requester.httpGetNoToken("user/unique/$username",  object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("DDD", "FAIL " + e.message)
                return
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val resultJson = response.body!!.string()
                    Log.d("DDD",resultJson)
                    val json = JSONArray(resultJson)
                    if (json.length() == 0){
                        Log.d("DDD","OK")
                        register(requester)
                    } else {
                        Log.d("DDD","Not valid")
                        for(i in 0 until json.length()) {
                            val item = json.getJSONObject(i)
                            names.add(item.toString())
                        }
                    }
                    return
                } else {
                    Log.d("DDD","NOT OK")
                    return
                }
            }
        })


    }

    fun register(requester: Requester) = auth.createUserWithEmailAndPassword(tv_email.text.toString(), tv_password.text.toString())
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.sendEmailVerification()
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            val json= JSONObject()
                            json.put("name",tv_username.text.toString())
                            json.put("email",tv_email.text.toString())
                            requester.httpPost("user/create", json,  object: Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    Log.d("DDD", "FAIL")
                                    return
                                }

                                override fun onResponse(call: Call, response: Response) {
                                    if (response.isSuccessful) {
                                        Log.d("DDD","OK")
                                        this@SignUpActivity.startActivity(Intent(this@SignUpActivity, ListOfCreatedProjectsActivity::class.java))
                                        this@SignUpActivity.finish()
                                    } else {
                                        Log.d("DDD","NOT OK " + response.message)
                                        runOnUiThread {
                                            Toast.makeText(
                                                baseContext, "Sign Up failed. Try again later.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        return
                                    }
                                }

                            })
                        }
                    }
                //val intent = Intent(this, ListOfCreatedProjectsActivity::class.java)
                //startActivity(intent)
            } else {
                Toast.makeText(
                    baseContext, "Sign Up failed. Try again later.",
                    Toast.LENGTH_SHORT
                ).show()
            }
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
}
