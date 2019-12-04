package com.mcc_project_5.DataModels

import android.util.Base64
import org.json.JSONObject

class Project {
    var id:Int
    val title: String
    val imageUrl: String
    val lastModified: String
    val isFavorite: Boolean
    val isMediaAvailable: Boolean
    val membersArray: ArrayList<ProjectMember> = arrayListOf()

    init {

    }

    constructor(json: JSONObject) {
        id = json.getInt("id")
        title = json.getString("title")
        imageUrl = String(Base64.decode(json.getString("imageUrl"), Base64.DEFAULT))
        lastModified = json.getString("lastModified")
        isFavorite = json.getBoolean("isFavorite")
        isMediaAvailable = json.getBoolean("isMediaAvailable")

        val tmpArray = json.getJSONArray("members")
        membersArray.clear()
        for(i in 0 until tmpArray.length()) {
            val item = tmpArray.getJSONObject(i)
            System.err.println(item)
            membersArray.add(ProjectMember(item))
        }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Project

        if (id != other.id) return false
        if (title != other.title) return false
        if (imageUrl != other.imageUrl) return false
        if (lastModified != other.lastModified) return false
        if (isFavorite != other.isFavorite) return false
        if (membersArray != other.membersArray) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + lastModified.hashCode()
        result = 31 * result + isFavorite.hashCode()
        result = 31 * result + membersArray.hashCode()
        return result
    }

    override fun toString(): String {
        return "Project(id=$id, title='$title', imageUrl='$imageUrl', lastModified='$lastModified', isFavorite=$isFavorite, membersArray=$membersArray)"
    }


}