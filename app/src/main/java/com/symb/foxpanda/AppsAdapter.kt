package com.symb.foxpanda

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class AppsAdapter(context:Context, list:List<String>): RecyclerView.Adapter<AppsAdapter.ViewHolder>() {

    internal var context1: Context
    internal var stringList:List<String>


    init{
        context1 = context
        stringList = list
    }
    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        var cardView: CardView
        var imageView: ImageView
        var textView_App_Name: TextView
        var textView_App_Package_Name:TextView
        init{
            cardView = view.findViewById(R.id.card_view) as CardView
            imageView = view.findViewById(R.id.imageview) as ImageView
            textView_App_Name = view.findViewById(R.id.Apk_Name) as TextView
            textView_App_Package_Name = view.findViewById(R.id.Apk_Package_Name) as TextView
        }
    }
   override fun onCreateViewHolder(parent: ViewGroup, viewType:Int):AppsAdapter.ViewHolder {
        val view2 = LayoutInflater.from(context1).inflate(R.layout.carview_layout, parent, false)
        val viewHolder = ViewHolder(view2)
        return viewHolder
    }
  override  fun onBindViewHolder(viewHolder:ViewHolder, position:Int) {
        val apkInfoExtractor = ApkInfoExtractor(context1)
        val ApplicationPackageName = stringList.get(position) as String
        val ApplicationLabelName = apkInfoExtractor.GetAppName(ApplicationPackageName)
        val drawable = apkInfoExtractor.getAppIconByPackageName(ApplicationPackageName)
        viewHolder.textView_App_Name.setText(ApplicationLabelName)
        viewHolder.textView_App_Package_Name.setText(ApplicationPackageName)
        viewHolder.imageView.setImageDrawable(drawable)
        //Adding click listener on CardView to open clicked application directly from here .

        viewHolder.cardView.setOnClickListener(object:View.OnClickListener{
            override fun onClick(view:View) {
                val intent = context1.getPackageManager().getLaunchIntentForPackage(ApplicationPackageName)
                if (intent != null)
                {
                    context1.startActivity(intent)
                }
                else
                {
                    Toast.makeText(context1, ApplicationPackageName + " Error, Please Try Again.", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
    override fun getItemCount(): Int { //To change body of created functions use File | Settings | File Templates.
        return stringList.size
    }
}