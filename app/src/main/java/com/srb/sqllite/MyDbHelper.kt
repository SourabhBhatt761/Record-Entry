package com.srb.sqllite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDbHelper(context: Context?) : SQLiteOpenHelper(
        context, Constants.DB_NAME, null, Constants.DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(Constants.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //upgrade database (if there is any structure change ,change db version )
        //drop older table if exists
        db?.execSQL("DROP TABLE IF EXISTS" + Constants.TABLE_NAME)
        onCreate(db)
    }

    fun insertRecord(
            name: String?,
            image: String?,
            bio: String?,
            phone: String?,
            email: String?,
            dob: String?,
            addedTime: String?,
            updatedTime: String?,
    ): Long {
        //get writeable database
        val db = this.writableDatabase
        val values = ContentValues()

        //id will be inserted automatically
        //insert data
        values.put(Constants.C_NAME, name)
        values.put(Constants.C_BIO, bio)
        values.put(Constants.C_EMAIL, email)
        values.put(Constants.C_IMAGE, image)
        values.put(Constants.C_PHONE, phone)
        values.put(Constants.C_DOB, dob)
        values.put(Constants.C_ADDED_TIMESTAMP, addedTime)
        values.put(Constants.C_UPDATED_TIMESTAMP, updatedTime)

        //insert row,it will return record id of saved record
        val id = db.insert(Constants.TABLE_NAME, null, values)
        //close db connection
        db.close()

        //return id of inserted record
        return id
    }

    fun getAllRecords(orderBy: String): ArrayList<ModelRecord> {

        val recordList = ArrayList<ModelRecord>()
        val selectQuery = "SELECT * FROM ${Constants.TABLE_NAME} ORDER BY $orderBy"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val modelRecord = ModelRecord(
                        "" + cursor.getInt(cursor.getColumnIndex(Constants.C_ID)),
                        "" + cursor.getString(cursor.getColumnIndex(Constants.C_NAME)),
                        "" + cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE)),
                        "" + cursor.getString(cursor.getColumnIndex(Constants.C_BIO)),
                        "" + cursor.getString(cursor.getColumnIndex(Constants.C_PHONE)),
                        "" + cursor.getString(cursor.getColumnIndex(Constants.C_EMAIL)),
                        "" + cursor.getString(cursor.getColumnIndex(Constants.C_DOB)),
                        "" + cursor.getString(cursor.getColumnIndex(Constants.C_ADDED_TIMESTAMP)),
                        "" + cursor.getString(cursor.getColumnIndex(Constants.C_UPDATED_TIMESTAMP))
                )
                recordList.add(modelRecord)
            } while (cursor.moveToNext())
        }
        db.close()

        return recordList
    }

    @SuppressLint("Recycle")
    fun searchRecords(query:String):ArrayList<ModelRecord>{
        val recordList = ArrayList<ModelRecord>()
        val selectQuery = "SELECT * FROM ${Constants.TABLE_NAME} WHERE ${Constants.C_NAME} LIKE '%$query%'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val modelRecord = ModelRecord(
                        "" + cursor.getInt(cursor.getColumnIndex(Constants.C_ID)),
                        "" + cursor.getString(cursor.getColumnIndex(Constants.C_NAME)),
                        "" + cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE)),
                        "" + cursor.getString(cursor.getColumnIndex(Constants.C_BIO)),
                        "" + cursor.getString(cursor.getColumnIndex(Constants.C_PHONE)),
                        "" + cursor.getString(cursor.getColumnIndex(Constants.C_EMAIL)),
                        "" + cursor.getString(cursor.getColumnIndex(Constants.C_DOB)),
                        "" + cursor.getString(cursor.getColumnIndex(Constants.C_ADDED_TIMESTAMP)),
                        "" + cursor.getString(cursor.getColumnIndex(Constants.C_UPDATED_TIMESTAMP))
                )
                recordList.add(modelRecord)
            } while (cursor.moveToNext())
        }
        db.close()

        return recordList
    }


    fun updateRecord(   id : String,
                        name: String?,
                        image: String?,
                        bio: String?,
                        phone: String?,
                        email: String?,
                        dob: String?,
                        addedTime: String?,
                        updatedTime: String?
                                            ):Long{

        val db = this.writableDatabase
        val values = ContentValues()

        //id will be inserted automatically
        //insert data
        values.put(Constants.C_NAME, name)
        values.put(Constants.C_BIO, bio)
        values.put(Constants.C_EMAIL, email)
        values.put(Constants.C_IMAGE, image)
        values.put(Constants.C_PHONE, phone)
        values.put(Constants.C_DOB, dob)
        values.put(Constants.C_ADDED_TIMESTAMP, addedTime)
        values.put(Constants.C_UPDATED_TIMESTAMP, updatedTime)


        return db.update(Constants.TABLE_NAME,values,"${Constants.C_ID}=?", arrayOf(id)).toLong()
    }

    fun recordCount():Int{
        val countQuery = " SELECT * FROM ${Constants.TABLE_NAME}"
        val db = this.readableDatabase
        val cursor = db.rawQuery(countQuery,null)
        val count = cursor.count
        cursor.close()
        return count
    }

    fun deleteRecord(id:String){
        val db = writableDatabase
        db.delete(
                Constants.TABLE_NAME,
                "${Constants.C_ID} = ?",
                arrayOf(id)
        )
        db.close()
    }

    fun deleteAllrecords(){
        val db = writableDatabase
        db.execSQL("DELETE FROM ${Constants.TABLE_NAME}")
        db.close()

    }



}
