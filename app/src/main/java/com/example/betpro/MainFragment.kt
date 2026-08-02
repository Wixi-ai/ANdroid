package com.example.betpro

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var balanceText: TextView
    private lateinit var wlText: TextView
    private lateinit var totalBetsText: TextView
    private lateinit var timePlayedText: TextView
    private lateinit var goalProgress: ProgressBar
    private lateinit var goalText: TextView

    private var balance = 0.0
    private var wins = 0
    private var losses = 0
    private var totalBets = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefs = requireContext().getSharedPreferences("betpro_prefs", Context.MODE_PRIVATE)

        balanceText = view.findViewById(R.id.balance_text)
        wlText = view.findViewById(R.id.wl_text)
        totalBetsText = view.findViewById(R.id.total_bets_text)
        timePlayedText = view.findViewById(R.id.time_played_text)
        goalProgress = view.findViewById(R.id.goal_progress_main)
        goalText = view.findViewById(R.id.goal_text_main)

        val addButton = view.findViewById<FloatingActionButton>(R.id.add_bet_button)

        addButton.setOnClickListener {
            showAddBetDialog()
        }

        loadDataFromDatabase()
    }

    override fun onResume() {
        super.onResume()
        loadDataFromDatabase()
    }

    private fun loadDataFromDatabase() {
        lifecycleScope.launch {
            // Загружаем баланс из настроек
            val settingsBalance = sharedPrefs.getFloat("balance", 0f).toDouble()
            val goal = sharedPrefs.getFloat("goal", 20000f).toDouble()

            BetDatabase.getInstance(requireContext()).betDao().getAllBets().collect { bets ->
                wins = bets.count { it.isWin }
                losses = bets.count { !it.isWin }
                totalBets = bets.size

                // Баланс из ставок
                val betsBalance = bets.sumOf { bet ->
                    if (bet.isWin) bet.amount * bet.odds - bet.amount
                    else -bet.amount
                }

                // Итоговый баланс = баланс из настроек + баланс из ставок
                balance = settingsBalance + betsBalance

                updateUI(goal)
            }
        }
    }

    private fun showAddBetDialog() {
        val dialog = AddBetDialog { amount, odds, bookmaker, isWin ->
            loadDataFromDatabase()
            Toast.makeText(requireContext(), "Ставка добавлена!", Toast.LENGTH_SHORT).show()
        }
        dialog.show(parentFragmentManager, "AddBetDialog")
    }

    private fun updateUI(goal: Double) {
        balanceText.text = String.format("%.0f ₽", balance)
        wlText.text = "${wins}W / ${losses}L"
        totalBetsText.text = totalBets.toString()
        timePlayedText.text = "0 мин"

        // Обновляем цель
        val goalPercent = ((balance / goal) * 100).toInt().coerceIn(0, 100)
        goalProgress.progress = goalPercent
        goalText.text = String.format("%.0f / %.0f ₽", balance, goal)
    }
}