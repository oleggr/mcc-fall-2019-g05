package com.mcc_project_5.DataModels

import android.util.Base64
import org.json.JSONObject

class Attachment {
    var attachment_type: String = ""
    var attachment_url: String = ""
    var id: String
    var name: String = ""
    var creation_time: String = ""
    var project_id: String = ""
    constructor(json: JSONObject) {
        id = json.getString("id")
        attachment_url = json.getString("attachment_url")
        creation_time = json.getString("creation_time")
        name = json.getString("name")
        attachment_type = json.getString("attachment_type")
        project_id = json.getString("project_id")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Attachment

        if (attachment_type != other.attachment_type) return false
        if (attachment_url != other.attachment_url) return false
        if (id != other.id) return false
        if (name != other.name) return false
        if (creation_time != other.creation_time) return false
        if (project_id != other.project_id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = attachment_type.hashCode()
        result = 31 * result + attachment_url.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + creation_time.hashCode()
        result = 31 * result + project_id.hashCode()
        return result
    }

    override fun toString(): String {
        return "Attachment(attachment_type='$attachment_type', attachment_url='$attachment_url', id='$id', name='$name', creation_time='$creation_time', project_id='$project_id')"
    }


}