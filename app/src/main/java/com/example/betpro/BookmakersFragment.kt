package com.example.betpro

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class BookmakersFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bookmakers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val container = view.findViewById<LinearLayout>(R.id.bookmakers_container)
        container.removeAllViews()

        for (bookmaker in BookmakersData.bookmakers) {
            val cardView = createBookmakerCard(bookmaker)
            container.addView(cardView)
        }
    }

    private fun createBookmakerCard(b: Bookmaker): View {
        val cardView = layoutInflater.inflate(R.layout.item_bookmaker, null) as androidx.cardview.widget.CardView

        // Отступы между карточками
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.bottomMargin = 32
        layoutParams.topMargin = 8
        cardView.layoutParams = layoutParams

        cardView.findViewById<TextView>(R.id.tv_short_name).text = b.shortName
        cardView.findViewById<TextView>(R.id.tv_full_name).text = b.fullName
        cardView.findViewById<TextView>(R.id.tv_description).text = b.description
        cardView.findViewById<TextView>(R.id.tv_advantages).text = b.advantages
        cardView.findViewById<TextView>(R.id.tv_bonus).text = b.bonusText

        val button = cardView.findViewById<MaterialButton>(R.id.btn_go)
        button.setOnClickListener {
            // ОТКРЫВАЕМ В БРАУЗЕРЕ (НЕ В WEBVIEW)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(b.refLink))
            startActivity(intent)
        }

        return cardView
    }
}