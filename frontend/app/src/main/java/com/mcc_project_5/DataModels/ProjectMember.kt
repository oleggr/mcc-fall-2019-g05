package com.mcc_project_5.DataModels

import android.util.Base64
import org.json.JSONObject

class ProjectMember {
    val id:String
    var name:String = ""
    var user_id: String = ""
    val imageUrl:String
    constructor(json: JSONObject) {
        this.id = json.getString("id")
        if (json.has("name")) {
            this.name  = json.getString("name")
        }
        if (json.has("user_id")) {
            this.user_id  = json.getString("user_id")
        }
        this.imageUrl = json.getString("imageUrl")
    }
}