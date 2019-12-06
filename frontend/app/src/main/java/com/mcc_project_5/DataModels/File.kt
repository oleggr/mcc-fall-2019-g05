package com.mcc_project_5.DataModels

import android.util.Base64
import org.json.JSONObject

class File {
    var url: String = ""
    var createdAt: String = ""
    var title: String = ""
    var id: Int
    constructor(json: JSONObject) {
        this.id = json.getInt("id")
        url = String(Base64.decode(json.getString("url"), Base64.DEFAULT))
        createdAt = json.getString("createdAt")
        title = json.getString("title")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as File

        if (url != other.url) return false
        if (createdAt != other.createdAt) return false
        if (title != other.title) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + id
        return result
    }

    override fun toString(): String {
        return "File(url='$url', createdAt='$createdAt', title='$title', id=$id)"
    }

}