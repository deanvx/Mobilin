package com.abel.mobilin.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.abel.mobilin.databinding.ActivityBookingDetailBinding
import com.bumptech.glide.Glide

class BookingDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari Intent
        val carName = intent.getStringExtra("CAR_NAME") ?: "-"
        val carType = intent.getStringExtra("CAR_TYPE") ?: "-"
        val startDate = intent.getStringExtra("START_DATE") ?: "-"
        val endDate = intent.getStringExtra("END_DATE") ?: "-"
        val duration = intent.getStringExtra("DURATION") ?: "-"
        val totalAmount = intent.getStringExtra("TOTAL_AMOUNT") ?: "-"
        val bookingCode = intent.getStringExtra("BOOKING_CODE") ?: "-"
        val carImage = intent.getStringExtra("CAR_IMAGE") ?: ""

        // Set ke UI
        binding.tvCarNameDetail.text = carName
        binding.tvCarTypeDetail.text = carType
        binding.tvStartDate.text = startDate
        binding.tvEndDate.text = endDate
        binding.tvDuration.text = duration
        binding.tvTotalPayment.text = totalAmount
        binding.tvBookingCodeDetail.text = bookingCode

        Glide.with(this)
            .load(carImage)
            .placeholder(android.R.drawable.ic_menu_report_image)
            .into(binding.ivCarDetailImage)

        // Tombol Back Arrow di atas
        binding.btnBackDetail.setOnClickListener { finish() }

        // Tombol Back bawah
        binding.btnBackDetailBottom.setOnClickListener { finish() }

        // Tombol Batalkan Booking
        binding.btnCancelBooking.setOnClickListener {
            // TODO: logika cancel booking
        }
    }
}