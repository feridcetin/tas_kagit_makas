package com.feridcetin.tas_kagit_makas

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val SPLASH_SCREEN_SURESI = 3000L // 3 saniye

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Belirli bir süre sonra MainActivity'ye geçiş yap
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // SplashActivity'yi kapat ki geri tuşuyla dönülmesin
        }, SPLASH_SCREEN_SURESI)
    }
}