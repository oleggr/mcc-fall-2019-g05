package com.mcc_project_5

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import com.squareup.okhttp.*

import kotlinx.android.synthetic.main.activity_add_attachment.*
import java.io.File
import org.json.JSONObject
import com.squareup.okhttp.MultipartBuilder
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.RequestBody
import androidx.fragment.app.FragmentActivity
import java.io.UnsupportedEncodingException
import java.net.UnknownHostException


class AddAttachment : AppCompatActivity() {

    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private val IMAGE_PICK_CODE = 1001
    private val FILE_UPLOAD_CODE = 1001
    var file_uri: Uri? = null
    val client : OkHttpClient = OkHttpClient()
    private val fileUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_attachment)
        setSupportActionBar(toolbar)


        fab.setOnClickListener { view ->
            val popupMenu: PopupMenu = PopupMenu(this,fab)
            popupMenu.menuInflater.inflate(R.layout.attachment,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.action_photo ->
                        openCamera()
                    R.id.action_gallery ->
                        pickImageFromGallery()
                    R.id.action_document ->
                        openFile()
                    R.id.action_cancel ->
                        Toast.makeText(this@AddAttachment, "Canceled", Toast.LENGTH_SHORT).show()
                }
                true
            })
            popupMenu.show()
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        file_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, file_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    private fun openFile() {
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(Intent.createChooser(intent, "Select a file"), FILE_UPLOAD_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //called when user presses ALLOW or DENY from Permission Request Popup
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    openCamera()
                }
                else{
                    //permission from popup was denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun takePhoto()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
                //permission was not enabled
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                //show popup to request permission
                requestPermissions(permission, PERMISSION_CODE)
            }
            else{
                //permission already granted
                openCamera()
            }
        }
        else{
            //system os is < marshmallow
            openCamera()
        }
    }


    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun uploadImage(file: File, ext: String): JSONObject? {

        try {

            val MEDIA_TYPE = MediaType.parse("jpg/pdf/txt/mp3")

            val req = MultipartBuilder().type(MultipartBuilder.FORM)
                .addFormDataPart("userid", "8457851245")
                .addFormDataPart(
                    "userfile",
                    "profile." + ext,
                    RequestBody.create(MEDIA_TYPE, file)
                ).build()

            val request = Request.Builder()
                .url(fileUrl)
                .post(req)
                .build()

            val client = OkHttpClient()
            val response = client.newCall(request).execute()

            Log.d("response", "uploadImage:" + response.body().string())

            return JSONObject(response.body().string())

        } catch (e: UnknownHostException) {
            System.err.println( "Error: " + e.getLocalizedMessage())
        } catch (e: UnsupportedEncodingException) {
            System.err.println( "Error: " + e.getLocalizedMessage())
        } catch (e: Exception) {
            System.err.println( "Error: " + e.localizedMessage!!)
        }

        return null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == FILE_UPLOAD_CODE && resultCode == RESULT_OK) || (resultCode == Activity.RESULT_OK)) {
            if(data == null || data.data == null){
                return
            }
            file_uri = data?.data //The uri with the location of the file
            val extension = MimeTypeMap.getFileExtensionFromUrl(file_uri.toString())
            if (extension != null) {
                val ext = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                uploadImage(file_uri!!.toFile(), ext.toString())
            }
        }
    }

}
