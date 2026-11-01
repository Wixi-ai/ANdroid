package com.example.betpro

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var balanceText: TextView
    private lateinit var goalText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefs = requireContext().getSharedPreferences("betpro_prefs", Context.MODE_PRIVATE)

        // Ищем элементы по НОВЫМ ID, которые мы прописали в новом XML
        balanceText = view.findViewById(R.id.settingsBalance)
        goalText = view.findViewById(R.id.settingsGoal)

        val depositBtn = view.findViewById<Button>(R.id.depositBtn)
        val withdrawBtn = view.findViewById<Button>(R.id.withdrawBtn)
        val setGoalBtn = view.findViewById<Button>(R.id.setGoalBtn)
        val clearDataBtn = view.findViewById<Button>(R.id.clearDataBtn)

        depositBtn.setOnClickListener {
            showAmountDialog("Пополнить баланс") { amount ->
                val currentBalance = sharedPrefs.getFloat("balance", 0f)
                sharedPrefs.edit().putFloat("balance", currentBalance + amount).apply()
                updateBalanceDisplay()
                Toast.makeText(requireContext(), "Баланс пополнен", Toast.LENGTH_SHORT).show()
            }
        }

        withdrawBtn.setOnClickListener {
            showAmountDialog("Вывести средства") { amount ->
                val currentBalance = sharedPrefs.getFloat("balance", 0f)
                if (amount <= currentBalance) {
                    sharedPrefs.edit().putFloat("balance", currentBalance - amount).apply()
                    updateBalanceDisplay()
                    Toast.makeText(requireContext(), "Средства выведены", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Недостаточно средств", Toast.LENGTH_SHORT).show()
                }
            }
        }

        setGoalBtn.setOnClickListener {
            showAmountDialog("Установить цель") { amount ->
                sharedPrefs.edit().putFloat("goal", amount).apply()
                updateGoalDisplay()
                Toast.makeText(requireContext(), "Цель обновлена", Toast.LENGTH_SHORT).show()
            }
        }

        clearDataBtn.setOnClickListener {
            showClearDataDialog()
        }

        updateBalanceDisplay()
        updateGoalDisplay()
    }

    private fun updateBalanceDisplay() {
        val balance = sharedPrefs.getFloat("balance", 0f)
        balanceText.text = String.format("%.0f ₽", balance)
    }

    private fun updateGoalDisplay() {
        val goal = sharedPrefs.getFloat("goal", 20000f)
        goalText.text = String.format("%.0f ₽", goal)
    }

    private fun showAmountDialog(title: String, onConfirm: (Float) -> Unit) {
        val input = EditText(requireContext())
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.hint = "Введите сумму"

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val text = input.text.toString()
                if (text.isNotEmpty()) {
                    val amount = text.toFloatOrNull()
                    if (amount != null && amount > 0) {
                        onConfirm(amount)
                    } else {
                        Toast.makeText(requireContext(), "Введите корректную сумму", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showClearDataDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Очистить все данные")
            .setMessage("Вы уверены? Все ставки и история будут удалены без возможности восстановления.")
            .setPositiveButton("Да, очистить") { _, _ ->
                sharedPrefs.edit().clear().apply()
                updateBalanceDisplay()
                updateGoalDisplay()
                
                lifecycleScope.launch {
                    BetDatabase.getInstance(requireContext()).betDao().deleteAll()
                    Toast.makeText(requireContext(), "Все данные очищены", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}
