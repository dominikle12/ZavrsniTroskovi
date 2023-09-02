package com.example.zavrsnitroskovi

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date

class ScannedReceiptActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanned_receipt)

        val nameOfExpenseInput = findViewById<TextInputEditText>(R.id.nameOfCustomExpense)
        val expenseValueInput = findViewById<TextInputEditText>(R.id.expense)

        val customExpenseButton = findViewById<Button>(R.id.addCustomExpenseButton)
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("expenses")

        val data = intent.getStringExtra("barcodeData")

        if(data != null){
            val expenseData = barcodeDataToList(data)
            nameOfExpenseInput.setText(expenseData[1])
            expenseValueInput.setText(expenseData[0])
        }

        collectionRef.get()
            .addOnSuccessListener { documents ->
                val types = ArrayList<String>()
                for (document in documents) {
                    val type = document.getString("type") ?: ""
                    if (!types.contains(type)) {
                        types.add(type)
                    }
                }
                types.add(types.size, "Add a new type")
                val typeSpinner = findViewById<Spinner>(R.id.typeSpinner2)
                val spinnerAdapter = CustomSpinnerAdapter(this, android.R.layout.simple_spinner_item, types)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                typeSpinner.adapter = spinnerAdapter

                typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedOption = types[position]
                        if (selectedOption == "Add a new type") {
                            showAddTypeDialog(types, spinnerAdapter)
                        }

                        Log.d("Spinner", "Selected option: $selectedOption")
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }
                customExpenseButton.setOnClickListener {
                    if(expenseValueInput.text?.isNotEmpty() == true || expenseValueInput.text?.isNotEmpty() == true){
                        val intent = Intent(this, MainActivity::class.java)
                        val amount = expenseValueInput.text.toString().toDouble()
                        val type = typeSpinner.selectedItem.toString()
                        val name = nameOfExpenseInput.text.toString()
                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                        val data = hashMapOf("amount" to amount, "type" to type, "name" to name, "date" to sdf.format(
                            Date()
                        ))

                        collectionRef.document()
                            .set(data)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Data updated successfully!", Toast.LENGTH_SHORT).show()
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error updating data: $e", Toast.LENGTH_SHORT).show()
                            }
                    }else{
                        Toast.makeText(this, "The fields cant be empty.", Toast.LENGTH_SHORT).show()
                    }

                }

            }
            .addOnFailureListener { exception ->
                Log.e("AddExpenseActivity", "Error retrieving data: ${exception.message}", exception)
            }
    }
    private fun showAddTypeDialog(types: ArrayList<String>, adapter: ArrayAdapter<String>) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_type)

        val editText = dialog.findViewById<EditText>(R.id.editTextType)
        val addButton = dialog.findViewById<Button>(R.id.addButtonDialog)

        addButton.setOnClickListener {
            val newType = editText.text.toString()
            if (newType.isNotEmpty()) {
                types.add(types.size - 1, newType)
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter a type", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun barcodeDataToList(barcodeData : String) : ArrayList<String>{
        val returnList : ArrayList<String> = ArrayList()
        val list = barcodeData.lines()

        val sb = StringBuilder(list[2])
        sb.insert(list[2].count() - 2, ".")

        val value = sb.toString().toDouble()
        returnList.add(value.toString())
        val desc = list.last()
        returnList.add(desc)
        return returnList
    }

}
