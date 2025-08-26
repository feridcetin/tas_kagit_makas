package com.feridcetin.tas_kagit_makas

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GecmisAdapter(private val gecmisListesi: List<TurSonucu>) :
    RecyclerView.Adapter<GecmisAdapter.GecmisViewHolder>() {

    class GecmisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewOyuncuSecim: ImageView = itemView.findViewById(R.id.imageViewOyuncuSecim)
        val imageViewBilgisayarSecim: ImageView = itemView.findViewById(R.id.imageViewBilgisayarSecim)
        val textViewSonuc: TextView = itemView.findViewById(R.id.textViewSonucGecmis)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GecmisViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_gecmis, parent, false)
        return GecmisViewHolder(view)
    }

    override fun onBindViewHolder(holder: GecmisViewHolder, position: Int) {
        val tur = gecmisListesi[position]

        val oyuncuResimId = when (tur.oyuncuSecimi) {
            "Taş" -> R.drawable.res_tas
            "Kağıt" -> R.drawable.res_kagit
            "Makas" -> R.drawable.res_makas
            else -> 0
        }
        holder.imageViewOyuncuSecim.setImageResource(oyuncuResimId)

        val bilgisayarResimId = when (tur.bilgisayarSecimi) {
            "Taş" -> R.drawable.res_tas
            "Kağıt" -> R.drawable.res_kagit
            "Makas" -> R.drawable.res_makas
            else -> 0
        }
        holder.imageViewBilgisayarSecim.setImageResource(bilgisayarResimId)

        // Sonuç metnini ayarla
        holder.textViewSonuc.text = tur.sonuc

        // Sonuç rengini ayarla
        val sonucRengi = when(tur.sonuc) {
            "Kazandın" -> Color.GREEN
            "Kaybettin" -> Color.RED
            else -> Color.GRAY
        }
        holder.textViewSonuc.setTextColor(sonucRengi)
    }

    override fun getItemCount(): Int {
        return gecmisListesi.size
    }
}