package com.feridcetin.tas_kagit_makas

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private var oyuncuSkoru = 0
    private var bilgisayarSkoru = 0
    private var berabereSkoru = 0

    private lateinit var textViewSonuc: TextView
    private lateinit var imageViewBilgisayar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonTas: ImageButton = findViewById(R.id.imageButtonTas)
        val buttonKagit: ImageButton = findViewById(R.id.imageButtonKagit)
        val buttonMakas: ImageButton = findViewById(R.id.imageButtonMakas)
        val buttonSifirla: Button = findViewById(R.id.buttonSifirla)

        textViewSonuc = findViewById(R.id.textViewSonuc)
        imageViewBilgisayar = findViewById(R.id.imageViewBilgisayar)

        // Uygulama başladığında ve skorlar sıfırlandığında çağrılacak fonksiyon
        sifirla()

        buttonTas.setOnClickListener {
            oyna("Taş")
        }

        buttonKagit.setOnClickListener {
            oyna("Kağıt")
        }

        buttonMakas.setOnClickListener {
            oyna("Makas")
        }

        buttonSifirla.setOnClickListener {
            sifirla()
        }
    }

    // Oyunun mantığını içeren ana fonksiyon
    private fun oyna(oyuncuSecimi: String) {
        val secenekler = listOf("Taş", "Kağıt", "Makas")
        val bilgisayarSecimi = secenekler.random()

        val resimId = when (bilgisayarSecimi) {
            "Taş" -> R.drawable.res_tas
            "Kağıt" -> R.drawable.res_kagit
            "Makas" -> R.drawable.res_makas
            else -> R.drawable.res_tas
        }
        imageViewBilgisayar.setImageResource(resimId)

        if (oyuncuSecimi == bilgisayarSecimi) {
            textViewSonuc.text = "Berabere! İkiniz de $oyuncuSecimi seçtiniz."
            berabereSkoru++
        } else {
            val kazanan = when (oyuncuSecimi) {
                "Taş" -> if (bilgisayarSecimi == "Makas") "Oyuncu" else "Bilgisayar"
                "Kağıt" -> if (bilgisayarSecimi == "Taş") "Oyuncu" else "Bilgisayar"
                "Makas" -> if (bilgisayarSecimi == "Kağıt") "Oyuncu" else "Bilgisayar"
                else -> "Hata"
            }

            if (kazanan == "Oyuncu") {
                textViewSonuc.text = "Kazandınız! Siz $oyuncuSecimi seçtiniz."
                oyuncuSkoru++
            } else {
                textViewSonuc.text = "Kaybettiniz! Siz $oyuncuSecimi seçtiniz."
                bilgisayarSkoru++
            }
        }

        guncelleSkor()
    }

    // Skorları ve ekranı başlangıç durumuna getiren yeni fonksiyon
    private fun sifirla() {
        oyuncuSkoru = 0
        bilgisayarSkoru = 0
        berabereSkoru = 0

        guncelleSkor()
        textViewSonuc.text = "Seçiminizi yapın!"
        // Bilgisayar resmini kaldırıyoruz
        imageViewBilgisayar.setImageDrawable(null)
    }

    // Skorları ekranda gösteren fonksiyon
    private fun guncelleSkor() {
        val textViewOyuncuSkor: TextView = findViewById(R.id.textViewOyuncuSkor)
        val textViewBilgisayarSkor: TextView = findViewById(R.id.textViewBilgisayarSkor)

        textViewOyuncuSkor.text = "Oyuncu: $oyuncuSkoru"
        textViewBilgisayarSkor.text = "Bilgisayar: $bilgisayarSkoru"
    }
}