package com.feridcetin.tas_kagit_makas

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private var oyuncuSkoru = 0
    private var bilgisayarSkoru = 0
    private var berabereSkoru = 0

    private val oyunGecmisi = mutableListOf<TurSonucu>()
    private lateinit var gecmisAdapter: GecmisAdapter

    // Artık tüm View değişkenleri null olabilir
    private var imageViewBilgisayar: ImageView? = null
    private var mainLayout: View? = null
    private var buttonTas: ImageButton? = null
    private var buttonKagit: ImageButton? = null
    private var buttonMakas: ImageButton? = null
    private var buttonSifirla: Button? = null
    private var recyclerViewGecmis: RecyclerView? = null
    private var soundButton: Button? = null
    private var backgroundLockButton: Button? = null
    private var themeButton: Button? = null

    private var mevcutArkaPlanId: Int = 0

    private val backgroundResimler = listOf(
        R.drawable.bg_oyun1,
        R.drawable.bg_oyun2,
        R.drawable.bg_oyun3,
        R.drawable.bg_oyun4
    )

    // Ayarlar için değişkenler
    private lateinit var sharedPreferences: SharedPreferences
    private var isSoundEnabled: Boolean = true
    private var isBackgroundLocked: Boolean = false
    private var isDarkTheme: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences("OyunAyarlari", Context.MODE_PRIVATE)
        isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false)
        if (isDarkTheme) {
            setTheme(R.style.Theme_TasKagitMakas)
        } else {
            setTheme(R.style.Theme_TasKagitMakas)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bileşenlerin güvenli bir şekilde atanması
        imageViewBilgisayar = findViewById(R.id.imageViewBilgisayar)
        mainLayout = findViewById(R.id.mainLayout)
        buttonTas = findViewById(R.id.imageButtonTas)
        buttonKagit = findViewById(R.id.imageButtonKagit)
        buttonMakas = findViewById(R.id.imageButtonMakas)
        buttonSifirla = findViewById(R.id.buttonSifirla)
        recyclerViewGecmis = findViewById(R.id.recyclerViewGecmis)
        soundButton = findViewById(R.id.buttonSes)
        backgroundLockButton = findViewById(R.id.buttonArkaPlanKilit)
        themeButton = findViewById(R.id.buttonTemaDegistir)

        // Ayar durumlarını yükle
        isSoundEnabled = sharedPreferences.getBoolean("sesDurumu", true)
        updateSoundButtonText()

        isBackgroundLocked = sharedPreferences.getBoolean("arkaPlanKilitli", false)
        updateBackgroundLockButtonText()

        isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false)
        updateThemeButtonText()


        gecmisAdapter = GecmisAdapter(oyunGecmisi)
        recyclerViewGecmis?.layoutManager = LinearLayoutManager(this)
        recyclerViewGecmis?.adapter = gecmisAdapter

        if (savedInstanceState != null) {
            oyuncuSkoru = savedInstanceState.getInt("oyuncu_skor")
            bilgisayarSkoru = savedInstanceState.getInt("bilgisayar_skor")
            berabereSkoru = savedInstanceState.getInt("berabere_skoru")
            val gecmisListesi = savedInstanceState.getSerializable("oyun_gecmisi") as ArrayList<TurSonucu>?
            gecmisListesi?.let {
                oyunGecmisi.addAll(it)
                gecmisAdapter.notifyDataSetChanged()
            }
            guncelleSkor()
            mevcutArkaPlanId = savedInstanceState.getInt("arka_plan_id")
        } else {
            sifirla()
        }

        if (isBackgroundLocked) {
            val lockedId = sharedPreferences.getInt("kilitliArkaPlanId", 0)
            if (lockedId != 0) {
                mevcutArkaPlanId = lockedId
            }
        }
        arkaPlaniAyarla()

        // Bileşenler null olmadığı sürece tıklama olaylarını ayarla
        buttonTas?.setOnClickListener { oyna("Taş") }
        buttonKagit?.setOnClickListener { oyna("Kağıt") }
        buttonMakas?.setOnClickListener { oyna("Makas") }

        buttonSifirla?.setOnClickListener { sifirla() }
        soundButton?.setOnClickListener { toggleSound() }
        backgroundLockButton?.setOnClickListener { toggleBackgroundLock() }
        themeButton?.setOnClickListener { toggleTheme() }
    }

    private fun toggleSound() {
        isSoundEnabled = !isSoundEnabled
        sharedPreferences.edit().putBoolean("sesDurumu", isSoundEnabled).apply()
        updateSoundButtonText()
    }

    private fun updateSoundButtonText() {
        soundButton?.text = if (isSoundEnabled) "Sesi Kapat" else "Sesi Aç"
    }

    private fun toggleBackgroundLock() {
        isBackgroundLocked = !isBackgroundLocked
        with(sharedPreferences.edit()) {
            putBoolean("arkaPlanKilitli", isBackgroundLocked)
            if (isBackgroundLocked) {
                putInt("kilitliArkaPlanId", mevcutArkaPlanId)
            } else {
                remove("kilitliArkaPlanId")
            }
            apply()
        }
        updateBackgroundLockButtonText()
        if (!isBackgroundLocked) {
            yeniArkaPlanSec()
        }
    }

    private fun updateBackgroundLockButtonText() {
        backgroundLockButton?.text = if (isBackgroundLocked) "Kilidi Kaldır" else "Arka Planı Kilitle"
    }

    private fun toggleTheme() {
        isDarkTheme = !isDarkTheme
        sharedPreferences.edit().putBoolean("isDarkTheme", isDarkTheme).apply()
        recreate()
    }

    private fun updateThemeButtonText() {
        themeButton?.text = if (isDarkTheme) "Açık Tema" else "Koyu Tema"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("oyuncu_skor", oyuncuSkoru)
        outState.putInt("bilgisayar_skor", bilgisayarSkoru)
        outState.putInt("berabere_skoru", berabereSkoru)
        outState.putSerializable("oyun_gecmisi", ArrayList(oyunGecmisi))
        outState.putInt("arka_plan_id", mevcutArkaPlanId)
    }

    private fun oyna(oyuncuSecimi: String) {
        butonlariDevreDisiBirak(true)

        val secenekler = listOf("Taş", "Kağıt", "Makas")
        val bilgisayarSecimi = secenekler.random()

        val resimId = when (bilgisayarSecimi) {
            "Taş" -> R.drawable.res_tas
            "Kağıt" -> R.drawable.res_kagit
            "Makas" -> R.drawable.res_makas
            else -> R.drawable.res_tas
        }
        imageViewBilgisayar?.setImageResource(resimId)

        imageViewBilgisayar?.animate()
            ?.alpha(1f)
            ?.setDuration(500)
            ?.start()

        val sonuc = when {
            oyuncuSecimi == bilgisayarSecimi -> "Berabere"
            (oyuncuSecimi == "Taş" && bilgisayarSecimi == "Makas") ||
                    (oyuncuSecimi == "Kağıt" && bilgisayarSecimi == "Taş") ||
                    (oyuncuSecimi == "Makas" && bilgisayarSecimi == "Kağıt") -> "Kazandın"
            else -> "Kaybettin"
        }

        if (isSoundEnabled) {
            val sesId = when (sonuc) {
                "Kazandın" -> R.raw.kazanma
                "Kaybettin" -> R.raw.kaybetme
                else -> R.raw.berabere
            }
            val mediaPlayer = MediaPlayer.create(this, sesId)
            mediaPlayer.start()
            // Hata bu satırdaydı: 'mediaaPlayer' -> 'mediaPlayer' olarak düzeltildi.
            mediaPlayer.setOnCompletionListener {
                it.release()
                butonlariDevreDisiBirak(false)
            }
        } else {
            butonlariDevreDisiBirak(false)
        }

        when (sonuc) {
            "Kazandın" -> oyuncuSkoru++
            "Kaybettin" -> bilgisayarSkoru++
            else -> berabereSkoru++
        }

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

        isBackgroundLocked = false
        sharedPreferences.edit()
            .remove("arkaPlanKilitli")
            .remove("kilitliArkaPlanId")
            .apply()
        updateBackgroundLockButtonText()
        yeniArkaPlanSec()
        guncelleSkor()
        imageViewBilgisayar?.setImageDrawable(null)
    }

    private fun guncelleSkor() {
        val textViewOyuncuSkor: TextView? = findViewById(R.id.textViewOyuncuSkor)
        val textViewBilgisayarSkor: TextView? = findViewById(R.id.textViewBilgisayarSkor)
        textViewOyuncuSkor?.text = "$oyuncuSkoru"
        textViewBilgisayarSkor?.text = "$bilgisayarSkoru"
    }

    private fun butonlariDevreDisiBirak(devreDisi: Boolean) {
        buttonTas?.isEnabled = !devreDisi
        buttonKagit?.isEnabled = !devreDisi
        buttonMakas?.isEnabled = !devreDisi
    }

    private fun yeniArkaPlanSec() {
        mevcutArkaPlanId = backgroundResimler.random()
        arkaPlaniAyarla()
    }

    private fun arkaPlaniAyarla() {
        mainLayout?.setBackgroundResource(mevcutArkaPlanId)
    }
}
data class TurSonucu(
    val oyuncuSecimi: String,
    val bilgisayarSecimi: String,
    val sonuc: String
) : java.io.Serializable