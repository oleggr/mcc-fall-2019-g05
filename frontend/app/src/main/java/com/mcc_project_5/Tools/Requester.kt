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


class Requester(context: Context) {
    private val client = OkHttpClient()
    private val baseUrl = Properties(context).getProperty("baseUrl")
    val JSON = "application/json; charset=utf-8".toMediaType()
    private val X_FIREBASE_TOKEN = "Firebase-token"

    fun httpGet(url: String, callBack: Callback) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener(object : OnSuccessListener<InstanceIdResult> {
                override fun onSuccess(instanceIdResult: InstanceIdResult) {
                    val token = instanceIdResult.token //Token
                    Log.d("token", token)
                    val request = Request.Builder()
                        .header(X_FIREBASE_TOKEN, token)
                        .url(baseUrl + url)
                        .get()
                        .build()

                    val call = client.newCall(request)
                    call.enqueue(callBack)
                }
            })
    }

    fun httpDelete(url: String, callBack: Callback) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener(object : OnSuccessListener<InstanceIdResult> {
                override fun onSuccess(instanceIdResult: InstanceIdResult) {
                    val token = instanceIdResult.token //Token

                    val request = Request.Builder()
                        .header("User-Agent", "AndroidApp")
                        .header(X_FIREBASE_TOKEN, token)
                        .url(baseUrl + url)
                        .delete()
                        .build()

                    val call = client.newCall(request)
                    call.enqueue(callBack)
                }
            })

    }

    fun httpPost(url: String, json: JSONObject, callBack: Callback) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener(object : OnSuccessListener<InstanceIdResult> {
                override fun onSuccess(instanceIdResult: InstanceIdResult) {
                    val token = instanceIdResult.token //Token
                    val body = RequestBody.create(JSON, json.toString())

                    val request = Request.Builder()
                        .header(X_FIREBASE_TOKEN, token)
                        .url(baseUrl + url)
                        .post(body)
                        .build()

                    val call = client.newCall(request)
                    call.enqueue(callBack)
                }
            })

    }

    fun httpDownload(url: String, callBack: Callback) {
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener(object : OnSuccessListener<InstanceIdResult> {
                override fun onSuccess(instanceIdResult: InstanceIdResult) {
                    val token = instanceIdResult.token //Token
                    val request = Request.Builder()
                        .header("User-Agent", "AndroidApp")
                        .header(X_FIREBASE_TOKEN, token)
                        .url(baseUrl + url)
                        .get()
                        .build()

                    val call = client.newCall(request)
                    call.enqueue(callBack)

                }
            })

    }
}