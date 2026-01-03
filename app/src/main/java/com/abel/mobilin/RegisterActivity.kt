package com.abel.mobilin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val CAMERA_PERMISSION_CODE = 100

    private var isScanningKtp = true

    // Penanda apakah foto sudah diambil
    private var isKtpUploaded = false
    private var isSimUploaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val etNIK = findViewById<EditText>(R.id.etNIK)
        val etSIM = findViewById<EditText>(R.id.etSIM)

        val btnUploadKTP = findViewById<Button>(R.id.btnUploadKTP)
        val btnUploadSIM = findViewById<Button>(R.id.btnUploadSIM)

        // Kita tidak perlu mendefinisikan imgKTP/imgSIM di sini karena diakses di launcher
        // tapi jika ingin akses untuk validasi visual bisa didefinisikan.

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)

        btnUploadKTP.setOnClickListener {
            isScanningKtp = true
            checkAndLaunchCamera()
        }

        btnUploadSIM.setOnClickListener {
            isScanningKtp = false
            checkAndLaunchCamera()
        }

        btnRegister.setOnClickListener {

            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            val nik = etNIK.text.toString().trim()
            val sim = etSIM.text.toString().trim()

            // VALIDASI LENGKAP
            when {
                // 1. Cek Username
                username.isEmpty() -> {
                    etUsername.error = "Username wajib diisi"
                    etUsername.requestFocus()
                }

                // 2. Cek Email
                email.isEmpty() -> {
                    etEmail.error = "Email wajib diisi"
                    etEmail.requestFocus()
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    etEmail.error = "Format email tidak valid"
                    etEmail.requestFocus()
                }

                // 3. Cek Password
                password.isEmpty() -> {
                    etPassword.error = "Password wajib diisi"
                    etPassword.requestFocus()
                }
                !isPasswordValid(password) -> {
                    etPassword.error = "Password min 8 karakter, harus ada huruf besar, kecil, dan angka"
                    etPassword.requestFocus()
                }

                // 4. Cek Konfirmasi Password
                confirmPassword.isEmpty() -> {
                    etConfirmPassword.error = "Konfirmasi password wajib diisi"
                    etConfirmPassword.requestFocus()
                }
                password != confirmPassword -> {
                    etConfirmPassword.error = "Password tidak cocok"
                    etConfirmPassword.requestFocus()
                }

                // 5. Cek NIK
                nik.isEmpty() -> {
                    etNIK.error = "NIK wajib diisi (Scan KTP)"
                    etNIK.requestFocus()
                }
                nik.length != 16 -> {
                    etNIK.error = "NIK harus 16 digit"
                    etNIK.requestFocus()
                }

                // 6. Cek SIM
                sim.isEmpty() -> {
                    etSIM.error = "Nomor SIM wajib diisi (Scan SIM)"
                    etSIM.requestFocus()
                }
                sim.length < 10 -> {
                    etSIM.error = "Nomor SIM tidak valid"
                    etSIM.requestFocus()
                }

                // 7. Cek Apakah Foto Sudah Diupload
                !isKtpUploaded -> {
                    Toast.makeText(this, "Harap ambil foto KTP terlebih dahulu", Toast.LENGTH_SHORT).show()
                }
                !isSimUploaded -> {
                    Toast.makeText(this, "Harap ambil foto SIM terlebih dahulu", Toast.LENGTH_SHORT).show()
                }

                // Jika semua lolos, lakukan register
                else -> registerUser(username, email, password, nik, sim)
            }
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    // Fungsi Validasi Password (Helper)
    private fun isPasswordValid(password: String): Boolean {
        return Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$").matches(password)
    }

    private fun registerUser(
        username: String,
        email: String,
        password: String,
        nik: String,
        sim: String
    ) {
        // Tampilkan loading jika perlu (opsional)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val userId = auth.currentUser?.uid ?: return@addOnSuccessListener

                val userMap = hashMapOf(
                    "username" to username,
                    "email" to email,
                    "nik" to nik,
                    "sim_a" to sim,
                    "uid" to userId,
                    // Opsional: Simpan status bahwa user ini belum diverifikasi admin jika ada logic itu
                    "isVerified" to false
                )

                FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(userId)
                    .set(userMap)
                    .addOnSuccessListener { sendVerificationEmail() }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gagal simpan data ke database", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Register Gagal: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendVerificationEmail() {
        auth.currentUser?.sendEmailVerification()
            ?.addOnSuccessListener {
                Toast.makeText(this, "Registrasi berhasil. Silakan cek email untuk verifikasi.", Toast.LENGTH_LONG).show()
                auth.signOut()
                finish()
            }
            ?.addOnFailureListener {
                Toast.makeText(this, "Gagal mengirim email verifikasi.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkAndLaunchCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            cameraLauncher.launch(null)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                if (isScanningKtp) {
                    findViewById<ImageView>(R.id.imgKTP).apply {
                        setImageBitmap(it)
                        visibility = View.VISIBLE
                    }
                    // Tandai KTP sudah ada
                    isKtpUploaded = true
                } else {
                    findViewById<ImageView>(R.id.imgSIM).apply {
                        setImageBitmap(it)
                        visibility = View.VISIBLE
                    }
                    // Tandai SIM sudah ada
                    isSimUploaded = true
                }
                scanTextFromBitmap(it)
            }
        }

    private fun scanTextFromBitmap(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { result ->

                val cleanText = result.text
                    .replace(" ", "")
                    .replace("\n", "")
                    .replace("O", "0")
                    .replace("I", "1")
                    .replace("S", "5")
                    .replace("B", "8")

                if (isScanningKtp) {
                    val nik = Regex("\\d{16}").find(cleanText)?.value
                    if (nik != null) {
                        findViewById<EditText>(R.id.etNIK).setText(nik)
                        Toast.makeText(this, "NIK berhasil discan", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Gagal membaca NIK otomatis, silakan ketik manual", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val sim = Regex("\\d{10,14}").find(cleanText)?.value
                    if (sim != null) {
                        findViewById<EditText>(R.id.etSIM).setText(sim)
                        Toast.makeText(this, "SIM berhasil discan", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Gagal membaca SIM otomatis, silakan ketik manual", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "OCR gagal memproses gambar", Toast.LENGTH_SHORT).show()
            }
    }
}