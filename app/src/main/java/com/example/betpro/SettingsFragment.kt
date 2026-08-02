package com.example.betpro

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

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

        balanceText = view.findViewById(R.id.settings_balance)
        goalText = view.findViewById(R.id.settings_goal)

        updateBalanceDisplay()
        updateGoalDisplay()

        view.findViewById<MaterialButton>(R.id.btn_deposit).setOnClickListener {
            showAmountDialog("Пополнение баланса", true)
        }

        view.findViewById<MaterialButton>(R.id.btn_withdraw).setOnClickListener {
            showAmountDialog("Вывод средств", false)
        }

        view.findViewById<MaterialButton>(R.id.btn_set_goal).setOnClickListener {
            showGoalDialog()
        }

        view.findViewById<MaterialButton>(R.id.btn_clear_data).setOnClickListener {
            showClearDataDialog()
        }
    }

    private fun updateBalanceDisplay() {
        val balance = sharedPrefs.getFloat("balance", 0f)
        balanceText.text = String.format("%.0f ₽", balance)
        // Обновляем баланс на главном экране через SharedPreferences
        // MainFragment сам подхватит при следующем открытии
    }

    private fun updateGoalDisplay() {
        val goal = sharedPrefs.getFloat("goal", 20000f)
        goalText.text = String.format("%.0f ₽", goal)
    }

    private fun updateBalance(newBalance: Float) {
        sharedPrefs.edit().putFloat("balance", newBalance).apply()
        updateBalanceDisplay()
    }

    private fun updateGoal(newGoal: Float) {
        sharedPrefs.edit().putFloat("goal", newGoal).apply()
        updateGoalDisplay()
    }

    private fun showAmountDialog(title: String, isDeposit: Boolean) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_amount, null)
        val input = dialogView.findViewById<EditText>(R.id.et_amount)
        input.hint = "Сумма в рублях"
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .setPositiveButton("Подтвердить") { _, _ ->
                val amountStr = input.text.toString()
                if (amountStr.isNotEmpty()) {
                    val amount = amountStr.toFloatOrNull() ?: 0f
                    if (amount > 0) {
                        val currentBalance = sharedPrefs.getFloat("balance", 0f)
                        val newBalance = if (isDeposit) {
                            currentBalance + amount
                        } else {
                            (currentBalance - amount).coerceAtLeast(0f)
                        }
                        updateBalance(newBalance)
                        Toast.makeText(requireContext(), "Баланс обновлён", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Введите корректную сумму", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showGoalDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_amount, null)
        val input = dialogView.findViewById<EditText>(R.id.et_amount)
        input.hint = "Цель в рублях"
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        input.setText(sharedPrefs.getFloat("goal", 20000f).toInt().toString())

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .setPositiveButton("Установить") { _, _ ->
                val goalStr = input.text.toString()
                if (goalStr.isNotEmpty()) {
                    val goal = goalStr.toFloatOrNull() ?: 0f
                    if (goal > 0) {
                        updateGoal(goal)
                        Toast.makeText(requireContext(), "Цель обновлена", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Введите корректную сумму", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showClearDataDialog() {
        AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setTitle("Очистить все данные")
            .setMessage("Вы уверены? Все ставки и история будут удалены без возможности восстановления.")
            .setPositiveButton("Да, очистить") { _, _ ->
                // Очищаем SharedPreferences (настройки)
                sharedPrefs.edit().clear().apply()
                updateBalanceDisplay()
                updateGoalDisplay()

                // Очищаем базу данных со ставками
                lifecycleScope.launch {
                    BetDatabase.getInstance(requireContext()).betDao().deleteAll()
                    Toast.makeText(requireContext(), "Все данные очищены", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}