package com.srb.sqllite

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_add_update.*

class AddUpdateActivity : AppCompatActivity() {

    private var isEditMode = false
    private val CAMERA_REQUEST_CODE=100
    private val STORAGE_REQUSET_CODE=101

    private val IMAGE_PICK_CAMERA_CODE=102
    private val IMAGE_PICK_GALLERY_CODE=103

    private lateinit var cameraPermissions:Array<String>
    private lateinit var storagePermissions:Array<String>

    private lateinit var actionBar: ActionBar

    private var imageUri:Uri? = null

    private lateinit var name:String
    private lateinit var id:String
    private lateinit var phone:String
    private lateinit var email:String
    private lateinit var dob:String
    private lateinit var bio:String
    private lateinit var addedTime:String
    private lateinit var updatedTime:String

    private lateinit var DbHelper: MyDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_update)

        actionBar = supportActionBar!!
        actionBar.title="Add Record"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        val intent  = intent
        isEditMode = intent.getBooleanExtra("isEditMode",false)
        if(isEditMode){
            actionBar.title = " Update Record"

            id = intent.getStringExtra("ID").toString()
            name = intent.getStringExtra("NAME").toString()
            phone = intent.getStringExtra("PHONE").toString()
            email = intent.getStringExtra("EMAIL").toString()
            dob = intent.getStringExtra("DOB").toString()
            bio = intent.getStringExtra("BIO").toString()
            addedTime = intent.getStringExtra("ADDED_TIME").toString()
            updatedTime = intent.getStringExtra("UPDATED_TIME").toString()
            imageUri = Uri.parse(intent.getStringExtra("IMAGE"))


            if(imageUri.toString() == "null"){
                profileIv.setImageResource(R.drawable.ic_person_black)
            }else{
                profileIv.setImageURI(imageUri)
            }
            nameEt.setText(name)
            phoneEt.setText(phone)
            emailEt.setText(email)
            dobEt.setText(dob)
            bioEt.setText(bio)
        }

        DbHelper= MyDbHelper(this)

        cameraPermissions= arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        storagePermissions= arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        profileIv.setOnClickListener{
           // Toast.makeText(this,"Button clicked",Toast.LENGTH_SHORT).show()
            imagePickDialogue()
        }


        saveBtn.setOnClickListener{
            inputData()

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun inputData() {
        //get data
        name="" + nameEt.text.toString().trim()
        phone="" + phoneEt.text.toString().trim()
        email="" + emailEt.text.toString().trim()
        dob="" + dobEt.text.toString().trim()
        bio="" + bioEt.text.toString().trim()

        if(isEditMode){

            val timestamp = "${System.currentTimeMillis()}"
            DbHelper?.updateRecord(
                    "$id",
                        "$name",
                    "$imageUri",
                    "$bio",
                    "$phone",
                    "$email",
                    "$dob",
                        "$addedTime",
                    "$updatedTime",

            )
            Toast.makeText(this,"Updated ...",Toast.LENGTH_SHORT).show()
        }else{
            val timestamp = System.currentTimeMillis()
            val id = DbHelper.insertRecord(
                    ""+name,
                    ""+imageUri,
                    ""+bio,
                    ""+phone,
                    ""+email,
                    ""+dob,
                    ""+"$timestamp",
                    ""+"$timestamp"
            )
            Toast.makeText(this,"Record Added against id $id",Toast.LENGTH_SHORT).show()
        }
        //save data to db

    }

    private fun imagePickDialogue() {
        val options = arrayOf("Camera","Storage")
        val builder = AlertDialog.Builder(this).setTitle("Pick Image From")
        //set items or options
        builder.setItems(options){ dialog,which->

            if (which==0){
                //camera clicked
                if (!checkCameraPermissions()){
                    requestCameraPermissions()
                }
                else{
                    pickFromCamera()
                }
            }else{
                //gallery clicked
                if(!checkStoragePermission()){
                    requestStoragePermission()
                }else{
                    pickFromGallery()
                }
            }
        }
    builder.show()
    }

    private fun pickFromGallery(){
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type="image/*"
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE)
    }

    private fun requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions,STORAGE_REQUSET_CODE)
    }

    private fun checkStoragePermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) ==  PackageManager.PERMISSION_GRANTED
    }

    private fun pickFromCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE,"Image Title")
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image Description")
        //put Image uri
        imageUri= contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
        //intent to open camera
        val cameraIntent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)

        startActivityForResult(
                cameraIntent,
                IMAGE_PICK_CAMERA_CODE
        )
    }

    private fun requestCameraPermissions() {
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE)
    }

    private fun checkCameraPermissions(): Boolean {
        val results = ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED
        val results1 = ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED

        return results&&results1
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){

            CAMERA_REQUEST_CODE->{
                if (grantResults.isNotEmpty()){
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if(cameraAccepted && storageAccepted){
                        pickFromCamera()
                    }
                }else{
                    Toast.makeText(this,"Storage and camera permission required",Toast.LENGTH_SHORT).show()
                }
            }
            STORAGE_REQUSET_CODE->{
                if (grantResults.isNotEmpty()){
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    if(storageAccepted){
                        pickFromGallery()
                    }else{
                        Toast.makeText(this,"Storage permission required",Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //image picked from camera or gallery will be received here
        if(resultCode == Activity.RESULT_OK) {
            //image is picked
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this)

            } else   if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                     CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this)
            }else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                val result = CropImage.getActivityResult(data)
                if(resultCode == Activity.RESULT_OK){
                    val resultUri = result.uri
                    imageUri= resultUri
                    profileIv.setImageURI(resultUri)
                }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    val error = result.error
                    Toast.makeText(this,""+error,Toast.LENGTH_SHORT).show()
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}