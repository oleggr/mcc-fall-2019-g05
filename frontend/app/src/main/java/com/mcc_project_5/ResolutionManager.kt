package com.mcc_project_5

import android.content.Context

class ResolutionManager(context:Context) {
    val properties: Properties = Properties(context)
    var verticalResolution:Int = 0
    var horizontalResolution:Int = 0
    fun setDefaultImageResultion(resX: Int, resY: Int):ResolutionManager {
        this.verticalResolution = resX
        this.horizontalResolution = resY
        return this
    }
    fun save() {
        properties.setProperty("verticalResolution", verticalResolution.toString())
        properties.setProperty("horizontalResolution", horizontalResolution.toString())
        //Append server request to save resolution here.
    }
    fun load() {
        verticalResolution = properties.getProperty("verticalResolution").toInt()
        horizontalResolution = properties.getProperty("horizontalResolution").toInt()
    }
}