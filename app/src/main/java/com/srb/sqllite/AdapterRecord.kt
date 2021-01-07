package com.srb.sqllite

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


//Adapter class for recycler view
class AdapterRecord():RecyclerView.Adapter<AdapterRecord.HolderRecord>() {

    private var context: Context?=null
    private var recordList:ArrayList<ModelRecord>?=null

     lateinit var dbHelper: MyDbHelper
    constructor(context: Context?,recordList : ArrayList<ModelRecord>?) : this(){
        this.context = context
        this.recordList = recordList

        dbHelper = MyDbHelper(context)
    }
    
    inner class HolderRecord(itemView : View):RecyclerView.ViewHolder(itemView){

        var profileIv : ImageView = itemView.findViewById(R.id.profileIv)
        var nameTv : TextView = itemView.findViewById(R.id.nameTv)
        var phoneTv = itemView.findViewById<TextView>(R.id.phoneTv)
        var emailTv = itemView.findViewById<TextView>(R.id.emailTv)
        var dobIv = itemView.findViewById<TextView>(R.id.dobIv)
        var moreBtn = itemView.findViewById<ImageButton>(R.id.moreBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderRecord {
        return HolderRecord(
                LayoutInflater.from(context).inflate(R.layout.row_record,parent,false)
        )
    }

    override fun onBindViewHolder(holder: HolderRecord, position: Int) {
        val model = recordList!!.get(position)

        val id = model.id
        val name = model.name
        val  image = model.image
        val  bio= model.bio
        val  phone= model.phone
        val email= model.email
        val dob= model.dob
        val addedTime= model.addedTime
        val updatedTime= model.updatedTime

        holder.nameTv.text = name
        holder.phoneTv.text = phone
        holder.emailTv.text = email
        holder.dobIv.text = dob

        if(image == "null"){
            holder.profileIv.setImageResource(R.drawable.ic_person)
        }else{
            holder.profileIv.setImageURI(Uri.parse(image))
        }

        holder.itemView.setOnClickListener{
            val intent = Intent(context,RecordDetailActivity::class.java)
            intent.putExtra("Record_ID",id)
            context!!.startActivity(intent)
        }

        holder.moreBtn.setOnClickListener{
            showMoreOptions(position,
                                id,
                            name,
                            phone,
                            email,
                            image,
                            bio,
                            dob,
                            addedTime,
                            updatedTime)
        }
    }

    private fun showMoreOptions(position: Int,
                                id: String,
                                name: String,
                                phone: String,
                                email: String,
                                image: String,
                                bio: String,
                                dob: String,
                                addedTime: String,
                                updatedTime: String) {

        val options = arrayOf("Edit","Delete")

        val dialog = AlertDialog.Builder(context)

        dialog.setItems(options) {
            dialog,which ->
            if(which == 0 ){
                //edit clicked
                val intent = Intent(context,AddUpdateActivity::class.java)
                intent.putExtra("ID",id)
                intent.putExtra("NAME",name)
                intent.putExtra("PHONE",phone)
                intent.putExtra("DOB",dob)
                intent.putExtra("BIO",bio)
                intent.putExtra("IMAGE",image)
                intent.putExtra("ADDED_TIME",addedTime)
                intent.putExtra("UPDATED_TIME",updatedTime)
                intent.putExtra("isEditMode",true)          //want to update existing record,set it true
                context!!.startActivity(intent)
            }else{

                dbHelper.deleteRecord(id)

                (context as MainActivity)!!.onResume()
            }
        }
        dialog.show()
    }

    override fun getItemCount(): Int {
        return recordList!!.size
    }
}
















