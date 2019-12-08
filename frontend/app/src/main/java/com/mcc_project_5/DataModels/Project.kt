package com.mcc_project_5.DataModels

import android.util.Base64
import org.json.JSONObject

class Project {
    var id:String
    val title: String
    val imageUrl: String
    val lastModified: String
    val isFavorite: Boolean
    val isMediaAvailable: Boolean
    val isOwner: Boolean
    val deadline: String
    val keywordsArray: ArrayList<String> = arrayListOf()
    val membersArray: ArrayList<ProjectMember> = arrayListOf()

    init {

    }

    constructor(json: JSONObject) {
        id = json.getString("id")
        title = json.getString("title")
        imageUrl = json.getString("imageUrl")
        lastModified = json.getString("lastModified")
        isFavorite = json.getBoolean("isFavorite")
        isMediaAvailable = json.getBoolean("isMediaAvailable")
        deadline = json.getString("deadline")
        isOwner = json.getBoolean("isOwner")

        var tmpArray = json.getJSONArray("members")
        membersArray.clear()
        for(i in 0 until tmpArray.length()) {
            val item = tmpArray.getJSONObject(i)
            System.err.println(item)
            membersArray.add(ProjectMember(item))
        }

        tmpArray = json.getJSONArray("keywords")
        keywordsArray.clear()
        for(i in 0 until tmpArray.length()) {
            val item = tmpArray.getString(i)
            System.err.println(item)
            keywordsArray.add(item)
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
        if (isMediaAvailable != other.isMediaAvailable) return false
        if (deadline != other.deadline) return false
        if (keywordsArray != other.keywordsArray) return false
        if (membersArray != other.membersArray) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + lastModified.hashCode()
        result = 31 * result + isFavorite.hashCode()
        result = 31 * result + isMediaAvailable.hashCode()
        result = 31 * result + deadline.hashCode()
        result = 31 * result + keywordsArray.hashCode()
        result = 31 * result + membersArray.hashCode()
        return result
    }

    override fun toString(): String {
        return "Project(id=$id, title='$title', imageUrl='$imageUrl', lastModified='$lastModified', isFavorite=$isFavorite, isMediaAvailable=$isMediaAvailable, deadline='$deadline', keywordsArray=$keywordsArray, membersArray=$membersArray)"
    }


}