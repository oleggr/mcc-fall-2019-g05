package com.mcc_project_5.Tools

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import okhttp3.MediaType.Companion.toMediaType
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import okhttp3.*
import okhttp3.RequestBody
import org.json.JSONObject
import com.google.firebase.auth.GetTokenResult
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser




class Requester(context: Context) {
    private val client = OkHttpClient()
    private val baseUrl = Properties(context).getProperty("baseUrl")
    val JSON = "application/json; charset=utf-8".toMediaType()
    private val X_FIREBASE_TOKEN = "Firebase-token"

    fun httpGet(url: String, callBack: Callback) {
        val mUser = FirebaseAuth.getInstance().currentUser
        mUser!!.getIdToken(true)
            .addOnCompleteListener(object : OnCompleteListener<GetTokenResult> {
                override fun onComplete(task: Task<GetTokenResult>) {
                    if (task.isSuccessful()) {
                        val idToken = task.getResult()?.getToken()
                        Log.d("token", idToken)
                        val request = Request.Builder()
                            .header(X_FIREBASE_TOKEN, idToken!!)
                            .url(baseUrl + url)
                            .get()
                            .build()

                        val call = client.newCall(request)
                        call.enqueue(callBack)
                    } else {
                        Log.d("token", "NO")
                    }
                }
            })
    }

    fun httpDelete(url: String, callBack: Callback) {
        val mUser = FirebaseAuth.getInstance().currentUser
        mUser!!.getIdToken(true)
            .addOnCompleteListener(object : OnCompleteListener<GetTokenResult> {
                override fun onComplete(task: Task<GetTokenResult>) {
                    if (task.isSuccessful()) {
                        val idToken = task.getResult()?.getToken()
                        Log.d("token", idToken)
                        val request = Request.Builder()
                            .header("User-Agent", "AndroidApp")
                            .header(X_FIREBASE_TOKEN, idToken!!)
                            .url(baseUrl + url)
                            .delete()
                            .build()

                        val call = client.newCall(request)
                        call.enqueue(callBack)
                    } else {
                        Log.d("token", "NO")
                    }
                }
            })

    }

    fun httpPost(url: String, json: JSONObject, callBack: Callback) {
        val mUser = FirebaseAuth.getInstance().currentUser
        mUser!!.getIdToken(true)
            .addOnCompleteListener(object : OnCompleteListener<GetTokenResult> {
                override fun onComplete(task: Task<GetTokenResult>) {
                    if (task.isSuccessful()) {
                        val idToken = task.getResult()?.getToken()
                        Log.d("token", idToken)
                        val body = RequestBody.create(JSON, json.toString())

                        val request = Request.Builder()
                            .header(X_FIREBASE_TOKEN, idToken!!)
                            .url(baseUrl + url)
                            .post(body)
                            .build()

                        val call = client.newCall(request)
                        call.enqueue(callBack)
                    } else {
                        Log.d("token", "NO")
                    }
                }
            })

    }

    fun httpDownload(url: String, callBack: Callback) {
        val mUser = FirebaseAuth.getInstance().currentUser
        mUser!!.getIdToken(true)
            .addOnCompleteListener(object : OnCompleteListener<GetTokenResult> {
                override fun onComplete(task: Task<GetTokenResult>) {
                    if (task.isSuccessful()) {
                        val idToken = task.getResult()?.getToken()
                        Log.d("token", idToken)
                        val request = Request.Builder()
                            .header("User-Agent", "AndroidApp")
                            .header(X_FIREBASE_TOKEN, idToken!!)
                            .url(baseUrl + url)
                            .get()
                            .build()

                        val call = client.newCall(request)
                        call.enqueue(callBack)
                    } else {
                        Log.d("token", "NO")
                    }
                }
            })

    }
}