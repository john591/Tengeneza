package com.example.tengeneza.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.tengeneza.R
import com.example.tengeneza.models.PotholeData

class PotholesAdapterClass(private val dataList: ArrayList<PotholeData>): RecyclerView.Adapter<PotholesAdapterClass.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.potholes_item_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        holder.rvCity.text = "Province: ${currentItem.city}"
        holder.rvStreetAddress.text = "Rue: ${currentItem.streetAddress}"
        holder.rvGeoPoint.text = "Localisation: ${currentItem.geoPoint?.latitude} ${currentItem.geoPoint?.longitude}" // we concatenat the geoPoint latitude and longitude
        holder.rvTimestamp.text = "Date: ${currentItem.currentDateTimeString}"
        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(currentItem.potholeImage)
            .apply(RequestOptions().centerCrop())
            .into(holder.rvPoholeImage)
    }

    class ViewHolderClass(itemView: View): RecyclerView.ViewHolder(itemView) {
        val rvTimestamp: TextView = itemView.findViewById(R.id.currentDateTimeString)
        val rvGeoPoint: TextView = itemView.findViewById(R.id.geoPoint)
        val rvStreetAddress: TextView = itemView.findViewById(R.id.streetAddress)
        val rvCity: TextView = itemView.findViewById(R.id.city)
        val rvPoholeImage:ImageView = itemView.findViewById(R.id.potholeImage)
    }

}