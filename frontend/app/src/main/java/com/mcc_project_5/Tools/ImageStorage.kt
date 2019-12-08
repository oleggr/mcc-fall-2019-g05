package com.mcc_project_5.Tools

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class ImageStorage {
    val properties: Properties
    val context: Context
    val storageRef = FirebaseStorage.getInstance().reference

    constructor(context: Context) {
        this.context = context
        properties = Properties(context)
    }

    fun findSuitableImageUrl(link: String): String {
        return link.substring(0, link.lastIndexOf('.') + 1) + properties.getProperty("resolutionOption")  + link.substring(link.lastIndexOf('.'))
    }

    fun loadToImageView(link: String, imageView:ImageView) {
        Log.i("WW", link)
        val newLink = findSuitableImageUrl(link)
        Log.i("WW", newLink)
        storageRef.child(newLink).stream.addOnCompleteListener {
            val bmp = BitmapFactory.decodeStream(it.result!!.stream)
            imageView.setImageBitmap(bmp)
        }
    }

    fun saveImageToStorage(bmp: Bitmap): String {
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val randomString = (1..12)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("");

        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val path = "/tmp/$randomString.jpeg"

        var uploadTask = storageRef.child(path).putBytes(data)
        uploadTask.addOnFailureListener {
            System.err.println(it.message)
        }.addOnSuccessListener {
            System.err.println(it.metadata)
        }
        return path
    }
}