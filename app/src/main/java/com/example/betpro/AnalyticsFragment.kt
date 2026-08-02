package com.example.betpro

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AnalyticsFragment : Fragment() {

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var totalBetsText: TextView
    private lateinit var winsCountText: TextView
    private lateinit var lossesCountText: TextView
    private lateinit var profitText: TextView
    private lateinit var winratePercentText: TextView
    private lateinit var winrateProgress: ProgressBar
    private lateinit var currentBalanceGoalText: TextView
    private lateinit var goalTargetText: TextView
    private lateinit var goalProgress: ProgressBar
    private lateinit var bestBetText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analytics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefs = requireContext().getSharedPreferences("betpro_prefs", Context.MODE_PRIVATE)

        totalBetsText = view.findViewById(R.id.total_bets)
        winsCountText = view.findViewById(R.id.wins_count)
        lossesCountText = view.findViewById(R.id.losses_count)
        profitText = view.findViewById(R.id.profit)
        winratePercentText = view.findViewById(R.id.winrate_percent)
        winrateProgress = view.findViewById(R.id.winrate_progress)
        currentBalanceGoalText = view.findViewById(R.id.current_balance_goal)
        goalTargetText = view.findViewById(R.id.goal_target)
        goalProgress = view.findViewById(R.id.goal_progress)
        bestBetText = view.findViewById(R.id.best_bet)

        loadDataFromDatabase()
    }

    override fun onResume() {
        super.onResume()
        loadDataFromDatabase()
    }

    private fun loadDataFromDatabase() {
        lifecycleScope.launch {
            // Загружаем цель и баланс из настроек
            val goalTarget = sharedPrefs.getFloat("goal", 20000f).toDouble()
            val settingsBalance = sharedPrefs.getFloat("balance", 0f).toDouble()

            BetDatabase.getInstance(requireContext()).betDao().getAllBets().collect { bets ->
                val totalBets = bets.size
                val wins = bets.count { it.isWin }
                val losses = totalBets - wins

                // Прибыль от ставок
                val betsProfit = bets.sumOf { bet ->
                    if (bet.isWin) bet.amount * bet.odds - bet.amount
                    else -bet.amount
                }

                // Итоговый баланс = баланс из настроек + прибыль от ставок
                val currentBalance = settingsBalance + betsProfit

                val winrate = if (totalBets > 0) (wins.toDouble() / totalBets) * 100 else 0.0

                // Лучшая ставка по чистой прибыли
                val bestBet = bets.maxByOrNull {
                    if (it.isWin) it.amount * it.odds - it.amount
                    else 0.0
                }

                val bestBetAmount = bestBet?.let {
                    (it.amount * it.odds - it.amount).toInt()
                } ?: 0
                val bestBetBookmaker = bestBet?.bookmaker ?: ""

                updateUI(totalBets, wins, losses, betsProfit, winrate, currentBalance, goalTarget, bestBetAmount, bestBetBookmaker)
            }
        }
    }

    private fun updateUI(totalBets: Int, wins: Int, losses: Int, betsProfit: Double, winrate: Double, currentBalance: Double, goalTarget: Double, bestBetAmount: Int, bestBetBookmaker: String) {
        totalBetsText.text = totalBets.toString()
        winsCountText.text = wins.toString()
        lossesCountText.text = losses.toString()
        profitText.text = String.format("%+.0f ₽", betsProfit)
        winratePercentText.text = String.format("%.1f%%", winrate)
        winrateProgress.progress = winrate.toInt()

        // Цель по балансу
        goalTargetText.text = "/ ${goalTarget.toInt()} ₽"
        currentBalanceGoalText.text = String.format("%.0f ₽", currentBalance)
        val goalPercent = ((currentBalance / goalTarget) * 100).toInt().coerceIn(0, 100)
        goalProgress.progress = goalPercent

        val bestBetTextStr = if (bestBetAmount > 0) {
            "+${bestBetAmount} ₽ (${bestBetBookmaker})"
        } else {
            "Пока нет ставок"
        }
        bestBetText.text = bestBetTextStr
    }
}