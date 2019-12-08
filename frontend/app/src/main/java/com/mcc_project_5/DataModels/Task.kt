package com.mcc_project_5.DataModels

import org.json.JSONObject

class Task {
    var title: String = "title"
    var done: Boolean = false
    var id: String
    var createdAt: String = ""
    constructor(json: JSONObject) {
        this.id = json.getString("id")
        this.title = json.getString("title")
        this.createdAt = json.getString("createdAt")
        this.done = json.getBoolean("done")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        if (title != other.title) return false
        if (done != other.done) return false
        if (id != other.id) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + done.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }

    override fun toString(): String {
        return "Task(title='$title', done=$done, id=$id, createdAt='$createdAt')"
    }


}