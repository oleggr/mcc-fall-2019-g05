package com.mcc_project_5.DataModels

import android.util.Base64
import org.json.JSONObject

class Task {
    var title: String = "title"
    var done: Boolean = false
    var id: Int
    constructor(json: JSONObject) {
        this.id = json.getInt("id")
        this.title = json.getString("title")
        this.done = json.getBoolean("done")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        if (title != other.title) return false
        if (done != other.done) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + done.hashCode()
        result = 31 * result + id
        return result
    }

    override fun toString(): String {
        return "Task(title='$title', done=$done, id=$id)"
    }


}