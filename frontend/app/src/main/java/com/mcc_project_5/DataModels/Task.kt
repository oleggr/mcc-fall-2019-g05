package com.mcc_project_5.DataModels

import org.json.JSONObject

class Task {
    var description: String = "Description"
    var status: String = "pending"
    var deadline: String = ""
    var id: String
    var createdAt: String = ""
    constructor(json: JSONObject) {
        this.id = json.getString("id")
        this.description = json.getString("description")
        this.deadline = json.getString("deadline")
        this.createdAt = json.getString("createdAt")
        this.status = json.getString("status")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        if (description != other.description) return false
        if (status != other.status) return false
        if (deadline != other.deadline) return false
        if (id != other.id) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = description.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + deadline.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }

    override fun toString(): String {
        return "Task(title='$description', done=$status, id=$id, deadline=$deadline, createdAt='$createdAt')"
    }


}