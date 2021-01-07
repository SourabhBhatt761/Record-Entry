package com.srb.sqllite

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.opencsv.CSVReader
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var dbHelper: MyDbHelper

    private val NEWEST_FIRST = "${Constants.C_ADDED_TIMESTAMP} DESC"
    private val OLDEST_FIRST = "${Constants.C_ADDED_TIMESTAMP} ASC"
    private val TITLE_ASC = "${Constants.C_NAME} ASC"
    private val TITLE_DESC = "${Constants.C_NAME} DESC"

    private var recentSortOrder = NEWEST_FIRST
    private val STORAGE_REQUSET_CODE_EXPORT = 1
    private val STORAGE_REQUSET_CODE_IMPORT = 1

    private lateinit var storagePermission: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        dbHelper = MyDbHelper(this)
        loadRecords(NEWEST_FIRST)

        addRecordBtn.setOnClickListener {
            val intent = Intent(this, AddUpdateActivity::class.java)
            intent.putExtra("isEditMode", false)           //want to add new record set it false
            startActivity(intent)
        }
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)
    }

    private fun requestStoragePermissionImport() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUSET_CODE_IMPORT)
    }

    private fun requestStoragePermissionExport() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUSET_CODE_EXPORT)
    }

    private fun loadRecords(orderBy: String) {
        recentSortOrder = orderBy
        val adapterRecord = AdapterRecord(this, dbHelper.getAllRecords(orderBy))

        recordsRv.adapter = adapterRecord
    }

    private fun searchRecords(query: String) {
        val adapterRecord = AdapterRecord(this, dbHelper.searchRecords(query))

        recordsRv.adapter = adapterRecord
    }

    private fun sortDialog() {
        val options = arrayOf("Name Ascending", "Name Descending", "Newest", "Oldest")

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Sort By")
                .setItems(options) { _, which ->
                    if (which == 0) {
                        loadRecords(TITLE_ASC)
                    } else if (which == 1) {
                        loadRecords(TITLE_DESC)
                    } else if (which == 2) {
                        loadRecords(NEWEST_FIRST)
                    } else if (which == 3) {
                        loadRecords(OLDEST_FIRST)
                    }
                }.show()
    }

    public override fun onResume() {
        super.onResume()
        loadRecords(recentSortOrder)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val item = menu!!.findItem(R.id.action_search)
        val searchView = item.actionView as androidx.appcompat.widget.SearchView

        searchView.setOnQueryTextListener(object :
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchRecords(newText)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchRecords(query)
                }
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_sort) {
            sortDialog()
        } else if (id == R.id.action_deleteAll) {
            dbHelper.deleteAllrecords()
            onResume()
        } else if (id == R.id.action_backup) {
            if (checkStoragePermission()) {
                exportCSV()
            } else {
                requestStoragePermissionExport()
            }
        } else if (id == R.id.action_restore) {
            if (checkStoragePermission()) {
                importCSV()
                onResume()
            } else {
                requestStoragePermissionImport()
            }
        }


        return super.onOptionsItemSelected(item)
    }

    private fun exportCSV() {
        val folder =
                File("${Environment.getExternalStorageDirectory()}/SQLiteBackupKotlin")

        var isFolderCreated = false
        if(!folder.exists()) isFolderCreated = folder.mkdir()

        val csvFileName = "SQLite_Backup.csv"

        val fileNameAndPath = "$folder/$csvFileName"

        var recordList = ArrayList<ModelRecord>()
        recordList.clear()
        recordList = dbHelper.getAllRecords(OLDEST_FIRST)

        try{
            val fw = FileWriter(fileNameAndPath)
            for(i in  recordList.indices){
                fw.append(""+recordList[i].id)
                fw.append(",")
                fw.append(""+recordList[i].name)
                fw.append(",")
                fw.append(""+recordList[i].image)
                fw.append(",")
                fw.append(""+recordList[i].bio)
                fw.append(",")
                fw.append(""+recordList[i].phone)
                fw.append(",")
                fw.append(""+recordList[i].email)
                fw.append(",")
                fw.append(""+recordList[i].dob)
                fw.append(",")
                fw.append(""+recordList[i].addedTime)
                fw.append(",")
                fw.append(""+recordList[i].updatedTime)
                fw.append("\n")
            }
            fw.flush()
            fw.close()

            Toast.makeText(this,"Backup Exported to $fileNameAndPath",Toast.LENGTH_SHORT).show()
        }catch (e : Exception){
            Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
        }
     }

    private fun importCSV() {
        val filePathAndName = "${Environment.getExternalStorageDirectory()}/SQLiteBackupKotlin/SQLite_Backup.csv"

        val csvFile = File(filePathAndName)

        if(csvFile.exists()) {

            try{
                val csvReader=CSVReader(FileReader(csvFile.absolutePath))
                var nextLine : Array<String>
                while ( csvReader.readNext().also { nextLine = it } != null){
                 val idd = nextLine[0]
                 val name = nextLine[1]
                 val image = nextLine[2]
                 val bio = nextLine[3]
                 val phone  = nextLine[4]
                 val email  = nextLine[5]
                 val dob  = nextLine[6]
                 val addedTIme  = nextLine[7]
                 val updatedTIme  = nextLine[8]

                    val timeStamp = System.currentTimeMillis()
                    val id = dbHelper.insertRecord(
                            ""+name,
                            ""+image,
                            ""+bio,
                            ""+phone,
                            ""+email,
                            ""+dob,
                            ""+"$timeStamp",
                            ""+"$timeStamp"
                    )
                    Toast.makeText(this,"Record Added against id $id",Toast.LENGTH_SHORT).show()
                }
            }catch (e: Exception){
                Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this,"Backup Not Found",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_REQUSET_CODE_EXPORT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportCSV()
                } else {
                    Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show()
                }
            }
            STORAGE_REQUSET_CODE_IMPORT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    importCSV()
                } else {
                    Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}