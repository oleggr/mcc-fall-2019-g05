package com.mcc_project_5

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.Nameable
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.mcc_project_5.Tools.Requester
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var result: Drawer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val channelId = getString(R.string.default_notification_channel_id)
        val channelName = "XXX"
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(
            NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_LOW)
        )

        FirebaseMessaging.getInstance().subscribeToTopic("push")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscription FAILED"
                }
                Log.d("DDD", msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("DDD", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                val msg = token
                Log.d("DDD TOKEN", msg)
                val requester = Requester(this@MainActivity)
                requester.httpPut("/user/update", JSONObject("{\"registrationToken\":\"$token\"}"), object:Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d("DDD", "FAIL")
                        Log.d("DDD", e.toString())
                        return
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            Log.d("DDD","OK")
                        } else {
                            Log.d("DDD","NOT OK" + response.message)
                            return
                        }
                    }
                })
            })




        setSupportActionBar(toolbar)

        result = DrawerBuilder()
            .withActivity(this)
            .withToolbar(toolbar)
            .inflateMenu(R.menu.navigation)
            .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                    if (drawerItem is Nameable<*>) {
                        when (drawerItem.identifier.toInt()) {
                            R.id.profile -> {
                                System.err.println("XXX")
                            }
                            R.id.projects -> {
                                val intent = Intent(this@MainActivity, ListOfCreatedProjectsActivity::class.java)
                                startActivity(intent)
                            }
                            R.id.logout -> {
                                System.err.println("ZZZ")
                            }
                        }
                    }

                    return false
                }
            })
            .withSelectedItemByPosition(0)
            .build()
    }


}
