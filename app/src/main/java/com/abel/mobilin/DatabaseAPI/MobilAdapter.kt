package com.abel.mobilin.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.abel.mobilin.BookingActivity
import com.abel.mobilin.DatabaseAPI.Mobil
import com.abel.mobilin.R
import com.abel.mobilin.databinding.ItemMobilBinding
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class MobilAdapter(private val listMobil: ArrayList<Mobil>) :
    RecyclerView.Adapter<MobilAdapter.MobilViewHolder>() {

    inner class MobilViewHolder(val binding: ItemMobilBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MobilViewHolder {
        val binding = ItemMobilBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MobilViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MobilViewHolder, position: Int) {
        val mobil = listMobil[position]

        with(holder.binding) {
            // Set data TextView
            tvNamaMobil.text = mobil.nama ?: "Nama Tidak Tersedia"
            tvTransmisi.text = mobil.transmisi ?: "-"
            tvSeat.text = mobil.seat ?: "0 Seat"

            // Format harga ke Rupiah
            val localeID = Locale("id", "ID")
            val numberFormat = NumberFormat.getCurrencyInstance(localeID)
            numberFormat.maximumFractionDigits = 0
            val hargaRp = numberFormat.format(mobil.harga ?: 0)
            tvHargaSewa.text = "$hargaRp/Hari"

            // Load foto pakai Glide
            Glide.with(holder.itemView.context)
                .load(mobil.foto)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .centerCrop()
                .into(imgMobil)

            // Listener tombol Pesan
            btnPesan.setOnClickListener {
                // Tampilkan Toast
                Toast.makeText(
                    holder.itemView.context,
                    "Kamu memilih ${mobil.nama}",
                    Toast.LENGTH_SHORT
                ).show()

                // Buka BookingActivity
                val intent = Intent(holder.itemView.context, BookingActivity::class.java)
                intent.putExtra("namaMobil", mobil.nama ?: "")
                intent.putExtra("hargaMobil", mobil.harga ?: 0)
                intent.putExtra("fotoMobil", mobil.foto ?: "")
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = listMobil.size
}