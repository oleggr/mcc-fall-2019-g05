package com.mcc_project_5.Tools

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class FileStorage {

    val properties: Properties
    val context: Context
    val storageRef = FirebaseStorage.getInstance().reference

    constructor(context: Context) {
        this.context = context
        properties = Properties(context)
    }

    fun saveToStorage(file: File): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val randomString = (1..12)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("");

        val path = "/tmp/$randomString" + file.extension

        var uploadTask = storageRef.child(path).putFile(Uri.fromFile(file))
        uploadTask.addOnFailureListener {
            System.err.println(it.message)
        }.addOnSuccessListener {
            System.err.println(it.metadata)
        }
        return path
    }
}