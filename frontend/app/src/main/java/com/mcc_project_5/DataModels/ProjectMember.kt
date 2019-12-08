package com.mcc_project_5.DataModels

import android.util.Base64
import org.json.JSONObject

class ProjectMember {
    val id:String
    val name:String
    val imageUrl:String
    constructor(json: JSONObject) {
        this.id = json.getString("id")
        this.name = json.getString("name")
        this.imageUrl = String(Base64.decode(json.getString("imageUrl"), Base64.DEFAULT))
    }
}