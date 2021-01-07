package com.srb.sqllite

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.ActionBar
import kotlinx.android.synthetic.main.activity_record_detail.*
import kotlinx.android.synthetic.main.activity_record_detail.emailTv
import kotlinx.android.synthetic.main.activity_record_detail.nameTv
import kotlinx.android.synthetic.main.activity_record_detail.phoneTv
import kotlinx.android.synthetic.main.row_record.*
import java.util.*

class RecordDetailActivity : AppCompatActivity() {

    private var actionBar:ActionBar? = null

    private var dbHelper:MyDbHelper? = null

    private var recordId:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_detail)

        actionBar = supportActionBar
        actionBar!!.title = "Record Details"
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        dbHelper = MyDbHelper(this)

        val intent = intent
        recordId = intent.getStringExtra("Record_ID")

        showRecordDetails()
    }

    @SuppressLint("Recycle")
    private fun showRecordDetails() {
        val selectQuery = "SELECT * FROM ${Constants.TABLE_NAME} WHERE ${Constants.C_ID} =\"$recordId\""
        val db = dbHelper!!.writableDatabase
        val cursor = db.rawQuery(selectQuery,null)

        if(cursor.moveToFirst()){
            do{
                val id ="" + cursor.getInt(cursor.getColumnIndex(Constants.C_ID))
                val name ="" + cursor.getString(cursor.getColumnIndex(Constants.C_NAME))
                val image = "" + cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE))
                val bio = "" + cursor.getString(cursor.getColumnIndex(Constants.C_BIO))
                val phone = "" + cursor.getString(cursor.getColumnIndex(Constants.C_PHONE))
                val email = "" + cursor.getString(cursor.getColumnIndex(Constants.C_EMAIL))
                val dob = "" + cursor.getString(cursor.getColumnIndex(Constants.C_DOB))
                val addedTimeStamp = "" + cursor.getString(cursor.getColumnIndex(Constants.C_ADDED_TIMESTAMP))
                val updatedTimeStamp = "" + cursor.getString(cursor.getColumnIndex(Constants.C_UPDATED_TIMESTAMP))

                val calendar1 = Calendar.getInstance(Locale.getDefault())
                calendar1.timeInMillis = addedTimeStamp.toLong()
                val timeAdded = DateFormat.format("dd/MM/yy hh:mm aa",calendar1)

                val calendar2 = Calendar.getInstance(Locale.getDefault())
                calendar2.timeInMillis = updatedTimeStamp.toLong()
                val timeUpdated = DateFormat.format("dd/MM/yy hh:mm aa",calendar2)

                nameTv.text = name
                bioTv.text = bio
                phoneTv.text = phone
                emailTv.text = email
                dobTv.text = dob
                addedDateTv.text = timeAdded
                updatedDateTv.text = timeUpdated

                if(image == "null"){
                    recordProfileIv.setImageResource(R.drawable.ic_person)
                }else{
                    recordProfileIv.setImageURI(Uri.parse(image))
                }

                db.close()

            }while (cursor.moveToNext())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}