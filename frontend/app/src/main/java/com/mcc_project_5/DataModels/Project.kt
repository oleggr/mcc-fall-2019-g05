package com.mcc_project_5.DataModels

import org.json.JSONObject

class Project {
    var id:Int
    val title: String
    val imageUrl: String
    val lastModified: String
    val isFavorite: Boolean
    val membersArray: ArrayList<ProjectMember> = arrayListOf()

    init {

    }

    constructor(json: JSONObject) {
        this.id = json.getInt("id")
        this.title = json.getString("title")
        this.imageUrl = json.getString("imageUrl")
        this.lastModified = json.getString("lastModified")
        this.isFavorite = json.getBoolean("isFavorite")
        val tmpArray = json.getJSONArray("members")
        for(i in 0 until tmpArray.length()) {
            val item = tmpArray.getJSONObject(i)
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