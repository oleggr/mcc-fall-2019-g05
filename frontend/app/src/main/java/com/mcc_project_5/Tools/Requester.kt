package com.mcc_project_5.Tools

import android.content.Context
import okhttp3.*

class Requester(context: Context) {
    private val client = OkHttpClient()
    private val baseUrl = Properties(context).getProperty("baseUrl")

    fun httpGet(url: String, callBack: Callback): Call {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val call = client.newCall(request)
        call.enqueue(callBack)
        return call
    }

    fun httpDelete(url: String, callBack: Callback): Call {
        val request = Request.Builder()
            .url(baseUrl + url)
            .delete()
            .build()

        val call = client.newCall(request)
        call.enqueue(callBack)
        return call
    }

    fun httpDownload(url: String, callBack: Callback): Call {
        val request = Request.Builder()
            .url(baseUrl + url)
            .get()
            .build()

        val call = client.newCall(request)
        call.enqueue(callBack)
        return call
    }
}