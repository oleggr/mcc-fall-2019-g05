package com.mcc_project_5

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*
import com.google.firebase.database.FirebaseDatabase
import com.mcc_project_5.Tools.Requester
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()


    }

    fun signUpUser(v: View) {
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

        auth.createUserWithEmailAndPassword(tv_email.text.toString(), tv_password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val requester = Requester(baseContext)
                                val json= JSONObject()
                                json.put("name",tv_username.text.toString())
                                json.put("email",tv_email.text.toString())
                                requester.httpPost("users/create_user", json,  object: Callback {
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
                                })
                                startActivity(Intent(this, ListOfCreatedProjectsActivity::class.java))
                                finish()
                            }
                        }
                    val intent = Intent(this, ListOfCreatedProjectsActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        baseContext, "Sign Up failed. Try again after some time.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
