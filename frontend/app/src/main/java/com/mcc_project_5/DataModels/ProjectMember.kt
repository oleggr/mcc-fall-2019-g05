package com.mcc_project_5.DataModels

import org.json.JSONObject

class ProjectMember {
    val id:Int
    constructor(json: JSONObject) {
        this.id = json.getInt("id")
    }
}