package com.abel.mobilin.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abel.mobilin.R
import com.abel.mobilin.model.HistoryItem
import com.abel.mobilin.ui.BookingDetailActivity
import com.bumptech.glide.Glide

class HistoryAdapter(private val list: List<HistoryItem>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCarImage: ImageView = itemView.findViewById(R.id.ivCarImage)
        val tvCarName: TextView = itemView.findViewById(R.id.tvCarName)
        val tvCarType: TextView = itemView.findViewById(R.id.tvCarType)
        val tvRentalDate: TextView = itemView.findViewById(R.id.tvRentalDate)
        val tvTotalAmount: TextView = itemView.findViewById(R.id.tvTotalAmount)
        val tvBookingCode: TextView = itemView.findViewById(R.id.tvBookingCode)
        val btnDetail: TextView = itemView.findViewById(R.id.btnDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = list[position]
        holder.tvCarName.text = item.carName
        holder.tvCarType.text = item.carType
        holder.tvRentalDate.text = item.rentalDate
        holder.tvTotalAmount.text = item.totalAmount
        holder.tvBookingCode.text = item.bookingCode

        Glide.with(holder.itemView.context)
            .load(item.carImage)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.ivCarImage)

        // Klik Detail → buka BookingDetailActivity
        holder.btnDetail.setOnClickListener {
            val dates = item.rentalDate.split(" - ")
            val startDate = dates.getOrNull(0) ?: "-"
            val endDate = dates.getOrNull(1) ?: "-"

            val context = holder.itemView.context
            val intent = Intent(context, BookingDetailActivity::class.java).apply {
                putExtra("CAR_NAME", item.carName)
                putExtra("CAR_TYPE", item.carType)
                putExtra("START_DATE", startDate)
                putExtra("END_DATE", endDate)
                putExtra("DURATION", "3 Hari") // bisa diubah hitung otomatis
                putExtra("TOTAL_AMOUNT", item.totalAmount)
                putExtra("BOOKING_CODE", item.bookingCode)
                putExtra("CAR_IMAGE", item.carImage)
            }
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int = list.size
}