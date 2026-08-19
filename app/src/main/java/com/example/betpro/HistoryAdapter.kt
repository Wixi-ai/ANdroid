package com.example.betpro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter(private var bets: List<Bet>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val bet = bets[position]
        holder.bind(bet)
    }

    override fun getItemCount(): Int = bets.size

    fun updateList(newList: List<Bet>) {
        bets = newList
        notifyDataSetChanged()
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val betAmount: TextView = itemView.findViewById(R.id.bet_amount)
        private val betDetails: TextView = itemView.findViewById(R.id.bet_details)
        private val betResult: TextView = itemView.findViewById(R.id.bet_result)

        fun bind(bet: Bet) {
            betAmount.text = String.format(Locale.getDefault(), "%.0f ₽", bet.amount)

            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            val dateStr = dateFormat.format(bet.date)
            betDetails.text = "${bet.bookmaker} • Кф ${String.format("%.2f", bet.odds)} • $dateStr"

            val profit = if (bet.isWin) bet.amount * bet.odds - bet.amount else -bet.amount
            val sign = if (profit >= 0) "+" else ""
            betResult.text = "$sign${String.format("%.0f", profit)} ₽"

            if (bet.isWin) {
                betResult.setTextColor(0xFF4CAF50.toInt())
            } else {
                betResult.setTextColor(0xFFE53935.toInt())
            }
        }
    }
}