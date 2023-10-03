package com.example.zavrsnitroskovi

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.Date
import com.google.type.DateTime
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import kotlin.math.log


class MainActivity : ComponentActivity() {

    private lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        pieChart = findViewById<PieChart>(R.id.chart)
        val addExpenseButton = findViewById<Button>(R.id.addExpenseButton)
        val logOutButton = findViewById<Button>(R.id.logOutButton)
        val expenseListButton = findViewById<Button>(R.id.expenseListButton)
        showPieChart()
        addExpenseButton.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }
        logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        expenseListButton.setOnClickListener {
            val intent = Intent(this, ExpenseListActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        showPieChart()
    }

    private fun showPieChart(){
        val pieEntries = ArrayList<PieEntry>()
        val label = "Type"
        val types = ArrayList<String>()
        val expenses = mutableMapOf<String, Float>()

        var currentMonthYear = SimpleDateFormat("MM/yyyy").format(System.currentTimeMillis())
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection(FirebaseAuth.getInstance().uid.toString())
        collectionRef.get()
            .addOnSuccessListener { documents ->
                Log.d("Main","CALLED SUCCESS")
                var totalExpense = 0f
                for (document in documents) {
                    val type = document.getString("type") ?: ""
                    val dateString = document.getString("date") ?: ""
                    val documentMonthYear = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dateString)
                    val documentFormattedMonthYear = SimpleDateFormat("MM/yyyy").format(documentMonthYear)


                    if (currentMonthYear == documentFormattedMonthYear) {
                        if (!types.contains(type)) {
                            types.add(type)
                        }
                        val amount = document.getDouble("amount")?.toFloat() ?: 0f
                        totalExpense += amount
                        expenses[type] = (expenses[type] ?: 0f) + amount
                    }
                }
                if(types.isNotEmpty()){
                    for (type in types) {
                        val typeValueSum = expenses[type] ?: 0f
                        pieEntries.add(PieEntry(typeValueSum, type))
                    }
                }

                val colors = ArrayList<Int>()
                colors.add(Color.parseColor("#304567"))
                colors.add(Color.parseColor("#309967"))
                colors.add(Color.parseColor("#476567"))
                colors.add(Color.parseColor("#890567"))
                colors.add(Color.parseColor("#a35567"))
                colors.add(Color.parseColor("#ff5f67"))
                colors.add(Color.parseColor("#3ca567"))

                val pieDataSet = PieDataSet(pieEntries, label)
                pieDataSet.valueTextSize = 20f
                pieDataSet.colors = colors

                val pieData = PieData(pieDataSet)
                pieData.setDrawValues(true)

                findViewById<TextView>(R.id.totalExpenseValue).text = totalExpense.toString()
                pieChart.data = pieData
                pieChart.invalidate()
            }
            .addOnFailureListener { exception ->
                Log.e("MainActivity", "Error retrieving data: ${exception.message}", exception)
            }
    }
}


