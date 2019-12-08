package com.mcc_project_5.DataModels

import android.util.Base64
import org.json.JSONObject

class User {
    var name: String = ""
    var id: String
    var imageUrl: String  = ""
    var checked: Boolean = false
    val projectsArray: ArrayList<String> = arrayListOf()
    constructor(json: JSONObject) {
        this.id = json.getString("id")
        this.name = json.getString("name")
        this.imageUrl = String(Base64.decode(json.getString("imageUrl"), Base64.DEFAULT))

        var tmpArray = json.getJSONArray("projects")
        projectsArray.clear()
        for(i in 0 until tmpArray.length()) {
            val item = tmpArray.getString(i)
            projectsArray.add(item)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (name != other.name) return false
        if (id != other.id) return false
        if (imageUrl != other.imageUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + imageUrl.hashCode()
        return result
    }

    override fun toString(): String {
        return "User(name='$name', id=$id, imageUrl='$imageUrl')"
    }


}