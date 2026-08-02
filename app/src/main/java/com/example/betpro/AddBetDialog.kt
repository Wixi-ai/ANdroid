package com.example.betpro

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import java.util.Date
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddBetDialog(private val onBetAdded: (amount: Double, odds: Double, bookmaker: String, isWin: Boolean) -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.CustomAlertDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_bet)

        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        val spinnerBookmaker = dialog.findViewById<Spinner>(R.id.spinner_bookmaker)
        val etAmount = dialog.findViewById<EditText>(R.id.et_amount)
        val etOdds = dialog.findViewById<EditText>(R.id.et_odds)
        val btnWin = dialog.findViewById<Button>(R.id.btn_win)
        val btnLose = dialog.findViewById<Button>(R.id.btn_lose)

        var isWin = true

        // Список букмекеров
        val bookmakerNames = BookmakersData.bookmakers.map { it.fullName }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, bookmakerNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBookmaker.adapter = adapter

        val redColor = ContextCompat.getColor(requireContext(), R.color.red_main)
        val grayDarkColor = ContextCompat.getColor(requireContext(), R.color.gray_dark)

        btnWin.setOnClickListener {
            isWin = true
            btnWin.setBackgroundColor(redColor)
            btnLose.setBackgroundColor(grayDarkColor)
        }
        btnLose.setOnClickListener {
            isWin = false
            btnLose.setBackgroundColor(redColor)
            btnWin.setBackgroundColor(grayDarkColor)
        }

        btnWin.setBackgroundColor(redColor)
        btnLose.setBackgroundColor(grayDarkColor)

        val btnSave = dialog.findViewById<Button>(R.id.btn_save)
        val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)

        btnSave.setOnClickListener {
            val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
            val odds = etOdds.text.toString().toDoubleOrNull() ?: 1.0
            val bookmaker = spinnerBookmaker.selectedItem.toString()
            val date = Date()

            if (amount > 0) {
                // Сохраняем в базу данных
                val bet = BetEntity(
                    amount = amount,
                    odds = odds,
                    isWin = isWin,
                    date = date,
                    bookmaker = bookmaker
                )

                CoroutineScope(Dispatchers.IO).launch {
                    BetDatabase.getInstance(requireContext()).betDao().insert(bet)
                }

                onBetAdded(amount, odds, bookmaker, isWin)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Введите корректную сумму", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }
}