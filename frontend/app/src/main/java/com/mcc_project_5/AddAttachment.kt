package com.mcc_project_5

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.attachment.*

import kotlinx.android.synthetic.main.activity_add_attachment.*



class AddAttachment : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_attachment)
        setSupportActionBar(toolbar)

        val attachmentValues = arrayListOf("Camera", "Gallery", "File")

        val factory = layoutInflater

        val textEntryView = factory.inflate(R.layout.attachment, null)

        val attachListView = textEntryView.findViewById<ListView>(R.id.attachmentList)

        val attachmentAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, attachmentValues)

        attachListView.adapter = attachmentAdapter

        attachListView.setOnItemClickListener { parent, view, position, id ->
            if (position == 0) {
                Toast.makeText(this, " Item 1 clicked", Toast.LENGTH_SHORT).show()
            }
        }



        fab.setOnClickListener { view ->
            val dialogAttach = LayoutInflater.from(this).inflate(R.layout.attachment, null)


            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                .setView(dialogAttach)
                .setTitle("Attach files")
            //show dialog
            val  mAlertDialog = mBuilder.show()
        }
    }

}
