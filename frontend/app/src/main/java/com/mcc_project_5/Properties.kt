package com.mcc_project_5

import android.content.Context
import android.content.res.AssetManager
import java.util.Properties

class Properties(context: Context) {
    private val properties: Properties = Properties()

    init {
        val assetManager: AssetManager = context.assets
        val inputStream = assetManager.open("config.properties")
        properties.load(inputStream)
    }

    fun getProperty(key: String): String {
        return properties.getProperty(key)
    }
}