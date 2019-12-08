package com.mcc_project_5

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.mcc_project_5.Tools.ImageStorage
import com.mcc_project_5.Tools.Requester
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.Nameable
import kotlinx.android.synthetic.main.activity_profile_settings.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ProfileSettingsActivity: AppCompatActivity(){

    private lateinit var auth: FirebaseAuth
    private var result: Drawer? = null

    lateinit var imageView: ImageView
    private var upload: Button? = null
    private val GALLERY = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)

        val mUser = FirebaseAuth.getInstance().currentUser ?: finish()

        imageView = findViewById(R.id.imageView)
        upload = findViewById<View>(R.id.upload) as Button
        upload!!.setOnClickListener { choosePhotoFromGallary()}

        auth = FirebaseAuth.getInstance()
        btn_change_password.setOnClickListener {
            changePassword()
        }

        result = DrawerBuilder()
            .withActivity(this)
            .withToolbar(toolbar)
            .inflateMenu(R.menu.navigation)
            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                    if (drawerItem is Nameable<*>) {
                        when (drawerItem.identifier.toInt()) {
                            R.id.profile -> {
                                val intent = Intent(this@ProfileSettingsActivity, ProfileSettingsActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                            }
                            R.id.projects -> {
                                val intent = Intent(this@ProfileSettingsActivity, ListOfCreatedProjectsActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                            }
                            R.id.logout -> {
                                auth.signOut()
                                val intent = Intent(this@ProfileSettingsActivity, LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                            }
                        }
                    }

                    return false
                }
            })
            .withSelectedItemByPosition(0)
            .build()

        loadUserProfile()
    }

    private fun loadUserProfile() {
        val requester = Requester(this)
        requester.httpGet("user", object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                System.err.println(e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val content = response.body!!.string()
                System.err.println(content)
                val json = JSONObject(content)
                val url = json.getString("image_url")
                if (url == "") {
                    runOnUiThread {
                        imageView.setImageResource(R.drawable.ic_account_circle_black_24dp)
                    }
                } else
                    ImageStorage(this@ProfileSettingsActivity).loadToImageView(url, this@ProfileSettingsActivity.imageView)
            }

        })
    }

    private fun choosePhotoFromGallary() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            imageView.setImageURI(data!!.data)
        }
    }

    private fun changePassword() {

        if (et_current_password.text.isNotEmpty() &&
            et_new_password.text.isNotEmpty() &&
            et_confirm_password.text.isNotEmpty()
        ) {

            if (et_new_password.text.toString().equals(et_confirm_password.text.toString())) {

                val user = auth.currentUser
                if (user != null && user.email != null) {
                    val credential = EmailAuthProvider
                        .getCredential(user.email!!, et_current_password.text.toString())

// Prompt the user to re-provide their sign-in credentials
                    user?.reauthenticate(credential)
                        ?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this, "Re-Authentication success.", Toast.LENGTH_SHORT).show()
                                user?.updatePassword(et_new_password.text.toString())
                                    ?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this, "Password changed successfully.", Toast.LENGTH_SHORT).show()
                                            auth.signOut()
                                            finish()
                                        }
                                    }

                            } else {
                                Toast.makeText(this, "Re-Authentication failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }

            } else {
                Toast.makeText(this, "Password mismatching.", Toast.LENGTH_SHORT).show()
            }

        } else {
            val bmp = (imageView.drawable as BitmapDrawable).bitmap
            val storedUrl = ImageStorage(this).saveImageToStorage(bmp)
            val requester = Requester(this)
            requester.httpPost("user/set_icon", JSONObject("{\"url\":\"$storedUrl\"}") , object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    System.err.println(e.message)
                    runOnUiThread {
                        Toast.makeText(this@ProfileSettingsActivity, "Failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    System.err.println("OK" + response.message)
                    runOnUiThread {
                        Toast.makeText(this@ProfileSettingsActivity, "Image updated.", Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }

    }


}