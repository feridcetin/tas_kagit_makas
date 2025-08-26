package com.feridcetin.tas_kagit_makas


import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private var oyuncuSkoru = 0
    private var bilgisayarSkoru = 0
    private var berabereSkoru = 0

    private val oyunGecmisi = mutableListOf<TurSonucu>()
    private lateinit var gecmisAdapter: GecmisAdapter

    private lateinit var textViewSonuc: TextView
    private lateinit var imageViewBilgisayar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonTas: ImageButton = findViewById(R.id.imageButtonTas)
        val buttonKagit: ImageButton = findViewById(R.id.imageButtonKagit)
        val buttonMakas: ImageButton = findViewById(R.id.imageButtonMakas)
        val buttonSifirla: Button = findViewById(R.id.buttonSifirla)
        val recyclerViewGecmis: RecyclerView = findViewById(R.id.recyclerViewGecmis)

        textViewSonuc = findViewById(R.id.textViewSonuc)
        imageViewBilgisayar = findViewById(R.id.imageViewBilgisayar)

        gecmisAdapter = GecmisAdapter(oyunGecmisi)
        recyclerViewGecmis.layoutManager = LinearLayoutManager(this)
        recyclerViewGecmis.adapter = gecmisAdapter

        sifirla()

        buttonTas.setOnClickListener { oyna("Taş") }
        buttonKagit.setOnClickListener { oyna("Kağıt") }
        buttonMakas.setOnClickListener { oyna("Makas") }
        buttonSifirla.setOnClickListener { sifirla() }
    }

    private fun oyna(oyuncuSecimi: String) {
        butonlariDevreDisiBirak(true)


        // Sonuç metnini görünür yap
        textViewSonuc.visibility = View.VISIBLE

        val secenekler = listOf("Taş", "Kağıt", "Makas")
        val bilgisayarSecimi = secenekler.random()

        val resimId = when (bilgisayarSecimi) {
            "Taş" -> R.drawable.res_tas
            "Kağıt" -> R.drawable.res_kagit
            "Makas" -> R.drawable.res_makas
            else -> R.drawable.res_tas
        }
        imageViewBilgisayar.setImageResource(resimId)

        imageViewBilgisayar.animate()
            .alpha(1f)
            .setDuration(500)
            .start()

        val sonuc = when {
            oyuncuSecimi == bilgisayarSecimi -> "Berabere"
            (oyuncuSecimi == "Taş" && bilgisayarSecimi == "Makas") ||
                    (oyuncuSecimi == "Kağıt" && bilgisayarSecimi == "Taş") ||
                    (oyuncuSecimi == "Makas" && bilgisayarSecimi == "Kağıt") -> "Kazandın"
            else -> "Kaybettin"
        }

        val sesId = when (sonuc) {
            "Kazandın" -> R.raw.kazanma
            "Kaybettin" -> R.raw.kaybetme
            else -> R.raw.berabere
        }

        val mediaPlayer = MediaPlayer.create(this, sesId)
        mediaPlayer.start()

        mediaPlayer.setOnCompletionListener {
            it.release()
            butonlariDevreDisiBirak(false)
        }

        when (sonuc) {
            "Kazandın" -> {
                textViewSonuc.text = "Kazandın! Siz $oyuncuSecimi seçtiniz."
                textViewSonuc.setTextColor(Color.GREEN)
            }
            "Kaybettin" -> {
                textViewSonuc.text = "Kaybettin! Siz $oyuncuSecimi seçtiniz."
                textViewSonuc.setTextColor(Color.RED)
            }
            else -> {
                textViewSonuc.text = "Berabere! İkiniz de $oyuncuSecimi seçtiniz."
                textViewSonuc.setTextColor(Color.GRAY)
            }
        }

        when (sonuc) {
            "Kazandın" -> oyuncuSkoru++
            "Kaybettin" -> bilgisayarSkoru++
            else -> berabereSkoru++
        }

        // Değişiklik burada! Yeni öğeyi listenin en başına ekliyoruz.
        oyunGecmisi.add(0, TurSonucu(oyuncuSecimi, bilgisayarSecimi, sonuc))
        gecmisAdapter.notifyDataSetChanged()
        guncelleSkor()
    }

    private fun sifirla() {
        oyuncuSkoru = 0
        bilgisayarSkoru = 0
        berabereSkoru = 0
        oyunGecmisi.clear()
        gecmisAdapter.notifyDataSetChanged()

        guncelleSkor()
        textViewSonuc.text = "Seçiminizi yapın!"
        imageViewBilgisayar.setImageDrawable(null)

        // Sonuç metnini gizle
        textViewSonuc.visibility = View.GONE
    }

    private fun guncelleSkor() {
        val textViewOyuncuSkor: TextView = findViewById(R.id.textViewOyuncuSkor)
        val textViewBilgisayarSkor: TextView = findViewById(R.id.textViewBilgisayarSkor)

        textViewOyuncuSkor.text = "$oyuncuSkoru"
        textViewBilgisayarSkor.text = "$bilgisayarSkoru"
    }

    private fun butonlariDevreDisiBirak(devreDisi: Boolean) {
        findViewById<ImageButton>(R.id.imageButtonTas).isEnabled = !devreDisi
        findViewById<ImageButton>(R.id.imageButtonKagit).isEnabled = !devreDisi
        findViewById<ImageButton>(R.id.imageButtonMakas).isEnabled = !devreDisi
    }
}

data class TurSonucu(
    val oyuncuSecimi: String,
    val bilgisayarSecimi: String,
    val sonuc: String
)