package com.mcc_project_5.DataModels

import android.util.Base64
import org.json.JSONObject

class Picture {
    var imageUrl: String = ""
    var createdAt: String = ""
    var id: Int
    var description: String = ""
    constructor(json: JSONObject) {
        this.id = json.getInt("id")
        imageUrl = String(Base64.decode(json.getString("imageUrl"), Base64.DEFAULT))
        createdAt = json.getString("createdAt")
        description = json.getString("description")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Picture

        if (imageUrl != other.imageUrl) return false
        if (createdAt != other.createdAt) return false
        if (id != other.id) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = imageUrl.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + id
        result = 31 * result + description.hashCode()
        return result
    }

    override fun toString(): String {
        return "Picture(imageUrl='$imageUrl', createdAt='$createdAt', id=$id, description='$description')"
    }


}