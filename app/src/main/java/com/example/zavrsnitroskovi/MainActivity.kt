package com.example.zavrsnitroskovi

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Dictionary


class MainActivity : ComponentActivity() {

    private lateinit var pieChart: PieChart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        pieChart = findViewById<PieChart>(R.id.chart)
        val addExpenseButton = findViewById<Button>(R.id.addExpenseButton)
        showPieChart()
        addExpenseButton.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        showPieChart()
    }
    private fun showPieChart(){
        val pieEntries = ArrayList<PieEntry>()
        val label = "type"
        val types = ArrayList<String>()
        val expenses = mutableMapOf<String, Int>()
        // Retrieve data from Firestore
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("expenses") // Replace with your collection name

        collectionRef.get()
            .addOnSuccessListener { documents ->
                // Process the retrieved documents and extract data
                for (document in documents) {
                    val type = document.getString("type") ?: ""
                    if(!types.contains(type)){
                        types.add(type)
                    }
                }
                for(type in types){
                    var typeValueSum = 0f
                    for(document in documents){
                        if(type == document.getString("type") ?: ""){
                            typeValueSum += document.get("amount").toString().toFloat()
                        }
                    }
                    pieEntries.add(PieEntry(typeValueSum, type))
                }


                // Set up the pie chart
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



                pieChart.data = pieData
                pieChart.invalidate()
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur during data retrieval
                // For simplicity, you can log the error message
                Log.e("MainActivity", "Error retrieving data: ${exception.message}", exception)
            }
    }
}


