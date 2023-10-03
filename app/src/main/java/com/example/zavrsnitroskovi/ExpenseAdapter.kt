package com.example.zavrsnitroskovi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(private val expenses: ArrayList<Expense>) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.nameTextView.text = "Expense name: " + expense.name
        holder.amountTextView.text = "Expense amount: " + expense.amount.toString()
        holder.typeTextView.text = "Expense type: " + expense.type
        holder.dateTextView.text = "Expense date: " + expense.date
    }

    override fun getItemCount(): Int {
        return expenses.size
    }

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textViewName)
        val amountTextView: TextView = itemView.findViewById(R.id.textViewAmount)
        val typeTextView: TextView = itemView.findViewById(R.id.textViewType)
        val dateTextView: TextView = itemView.findViewById(R.id.textViewDate)
    }
}