package com.example.zavrsnitroskovi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class ExpenseListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        recyclerView = findViewById(R.id.expenseList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        var expenses = ArrayList<Expense>()
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection(FirebaseAuth.getInstance().uid.toString())
        Log.d("ExpenseList", "PRIJE DOHVACANJA BAZE")


        collectionRef.get()
            .addOnSuccessListener { documents ->
                val dates = ArrayList<String>()
                for (document in documents) {
                    val dateString = document.getString("date") ?: ""
                    Log.d("ExpenseList", dateString)
                    val documentMonthYear = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dateString)
                    Log.d("ExpenseList", documentMonthYear.toString())
                    val documentFormattedMonthYear = SimpleDateFormat("MM/yyyy").format(documentMonthYear)
                    if (!dates.contains(documentFormattedMonthYear)) {
                        dates.add(documentFormattedMonthYear.toString())
                    }
                }
                val typeSpinner = findViewById<Spinner>(R.id.dateSpinner)
                val spinnerAdapter =
                    CustomSpinnerAdapter(this, android.R.layout.simple_spinner_item, dates)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                typeSpinner.adapter = spinnerAdapter
                Log.d("ExpenseList", "PROSLO SPINER ADAPTER")
                typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selectedOption = dates[position]
                        expenses.clear()
                        Log.d("Spinner", "Selected option: $selectedOption")
                        collectionRef.get()
                            .addOnSuccessListener { documents ->
                                Log.d("ExpenseList", "SUCCESS")
                                for (document in documents) {
                                    val type = document.getString("type") ?: ""
                                    val name = document.getString("name") ?: ""
                                    val date = document.getString("date") ?: ""
                                    val amount = document.getDouble("amount") ?: 0.0

                                    val documentMonthYear = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(date)
                                    val documentFormattedMonthYear = SimpleDateFormat("MM/yyyy").format(documentMonthYear)

                                    if(documentFormattedMonthYear == selectedOption){
                                        expenses.add(Expense(amount.toFloat(), name, date, type))
                                    }

                                }
                                expenseAdapter = ExpenseAdapter(expenses)
                                recyclerView.adapter = expenseAdapter
                            }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }

            }
    }
}