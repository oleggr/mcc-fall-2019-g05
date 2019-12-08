package com.mcc_project_5

import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.mcc_project_5.Tools.Requester
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        val mUser = FirebaseAuth.getInstance().currentUser
        if (null != mUser){

            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = "XXX"
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW)
            )

            FirebaseMessaging.getInstance().subscribeToTopic("push")
                .addOnCompleteListener { task ->
                    var msg = "Subscribed"
                    if (!task.isSuccessful) {
                        msg = "Subscription FAILED"
                    }
                    Log.d("DDD", msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }

            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("DDD", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result?.token

                    // Log and toast
                    val msg = token
                    Log.d("DDD TOKEN", msg)
                    val requester = Requester(this@LoginActivity)
                    requester.httpPut("/user/update", JSONObject("{\"registrationToken\":\"$token\"}"), object:
                        Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Log.d("DDD", "FAIL")
                            Log.d("DDD", e.toString())
                            return
                        }

                        override fun onResponse(call: Call, response: Response) {
                            if (response.isSuccessful) {
                                Log.d("DDD","OK")
                            } else {
                                Log.d("DDD","NOT OK" + response.message)
                                return
                            }
                        }
                    })
                })

            this@LoginActivity.startActivity(Intent(this@LoginActivity, ListOfCreatedProjectsActivity::class.java))
            this@LoginActivity.finish()
        }

        btn_sign_up.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_log_in.setOnClickListener {
            doLogin()
        }

    }


    private fun doLogin() {
        if (tv_username.text.toString().isEmpty()) {
            tv_username.error = "Please enter email"
            tv_username.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(tv_username.text.toString()).matches()) {
            tv_username.error = "Please enter valid email"
            tv_username.requestFocus()
            return
        }

        if (tv_password.text.toString().isEmpty()) {
            tv_password.error = "Please enter password"
            tv_password.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(tv_username.text.toString(), tv_password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    updateUI(null)
                }
            }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {

        if (currentUser != null) {
            val intent = Intent(this, ListOfCreatedProjectsActivity::class.java)
            startActivity(intent)
        }
        else {
            Toast.makeText(
                baseContext, "Login failed.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}
