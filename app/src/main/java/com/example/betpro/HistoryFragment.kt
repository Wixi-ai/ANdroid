package com.example.betpro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: LinearLayout
    private lateinit var adapter: HistoryAdapter
    private var betsList = mutableListOf<Bet>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.history_recycler)
        emptyView = view.findViewById(R.id.empty_history)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = HistoryAdapter(betsList)
        recyclerView.adapter = adapter

        loadBetsFromDatabase()
    }

    override fun onResume() {
        super.onResume()
        loadBetsFromDatabase()
    }

    private fun loadBetsFromDatabase() {
        lifecycleScope.launch {
            BetDatabase.getInstance(requireContext()).betDao().getAllBets().collect { bets ->
                betsList.clear()
                betsList.addAll(bets.map { betEntity ->
                    Bet(
                        amount = betEntity.amount,
                        odds = betEntity.odds,
                        isWin = betEntity.isWin,
                        date = betEntity.date,
                        bookmaker = betEntity.bookmaker
                    )
                })

                adapter.updateList(betsList)

                if (betsList.isEmpty()) {
                    emptyView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
        }
    }
}