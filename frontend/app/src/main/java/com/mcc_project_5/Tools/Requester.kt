package com.mcc_project_5.Tools

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import okhttp3.*

class Requester(context: Context) {
    private val client = OkHttpClient()
    private val baseUrl = Properties(context).getProperty("baseUrl")
    private val X_FIREBASE_TOKEN = "FIREBASE_TOKEN"
    private val TOKEN = FirebaseInstanceId.getInstance().id

    fun httpGet(url: String, callBack: Callback): Call {
        val request = Request.Builder()
            .header("User-Agent", "AndroidApp")
            .header(X_FIREBASE_TOKEN, TOKEN)
            .header("Accept", "application/json")
            .url(url)
            .get()
            .build()

        val call = client.newCall(request)
        call.enqueue(callBack)
        return call
    }

    fun httpDelete(url: String, callBack: Callback): Call {
        val request = Request.Builder()
            .header("User-Agent", "AndroidApp")
            .header(X_FIREBASE_TOKEN, TOKEN)
            .url(baseUrl + url)
            .delete()
            .build()

        val call = client.newCall(request)
        call.enqueue(callBack)
        return call
    }

    fun httpDownload(url: String, callBack: Callback): Call {
        val request = Request.Builder()
            .header("User-Agent", "AndroidApp")
            .header(X_FIREBASE_TOKEN, TOKEN)
            .url(baseUrl + url)
            .get()
            .build()

        val call = client.newCall(request)
        call.enqueue(callBack)
        return call
    }
}