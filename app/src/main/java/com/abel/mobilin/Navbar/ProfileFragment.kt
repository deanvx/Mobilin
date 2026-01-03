package com.abel.mobilin.Navbar

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.abel.mobilin.R
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    // 1. Deklarasi Variabel UI
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var btnLogout: Button
    private lateinit var ivProfileImage: ImageView // Sesuai ID di XML
    private lateinit var btnBack: ImageView

    // 2. Konstanta SharedPreferences (Perbaikan error unresolved reference)
    private val prefsName = "user_pref"
    private val keyPhoto = "profile_photo_uri"

    // 3. Launcher Galeri
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                savePhotoUri(it)
                try {
                    ivProfileImage.setImageURI(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // 4. Inisialisasi View (Menghubungkan variabel dengan ID di XML)
        tvUsername = view.findViewById(R.id.tvUsername)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)
        btnLogout = view.findViewById(R.id.btnLogout)
        ivProfileImage = view.findViewById(R.id.ivProfileImage) // ID harus sama dengan XML
        btnBack = view.findViewById(R.id.btnBack)

        // Load Data
        loadUserProfile()
        loadSavedPhoto()

        // 5. Listener Tombol Logout
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val prefs = requireActivity().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
            prefs.edit().clear().apply()

            Toast.makeText(requireContext(), "Berhasil keluar", Toast.LENGTH_SHORT).show()
            requireActivity().finish() // Menutup Activity saat ini
        }

        // 6. Listener Ganti Foto Profil
        ivProfileImage.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // Listener Tombol Back (Opsional)
        btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }

    private fun loadUserProfile() {
        val prefs = requireActivity().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val username = prefs.getString("username", "Pengguna") ?: "Pengguna"
        val email = FirebaseAuth.getInstance().currentUser?.email ?: prefs.getString("email", "user@example.com")

        tvUsername.text = username
        tvEmail.text = email
        tvUserEmail.text = email
    }

    private fun savePhotoUri(uri: Uri) {
        val prefs = requireActivity()
            .getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        prefs.edit()
            .putString(keyPhoto, uri.toString())
            .apply()
    }

    private fun loadSavedPhoto() {
        val prefs = requireActivity()
            .getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        val uriString = prefs.getString(keyPhoto, null)
        if (uriString != null) {
            try {
                ivProfileImage.setImageURI(Uri.parse(uriString))
            } catch (e: Exception) {
                // Handle jika gambar gagal dimuat (misal file terhapus)
                ivProfileImage.setImageResource(R.drawable.ic_launcher_background) // Gambar default
            }
        }
    }
}