package com.example.zavrsnitroskovi

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

class AddExpenseActivity : ComponentActivity() {

    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)
        val amountTextInput = findViewById<TextInputEditText>(R.id.amountEditText)
        val nameOfExpense = findViewById<TextInputEditText>(R.id.nameOfExpense)
        val addButton = findViewById<Button>(R.id.addButton)
        val scanButton = findViewById<Button>(R.id.scanReceiptButton)
        auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection(auth.uid.toString())

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
                val typeSpinner = findViewById<Spinner>(R.id.typeSpinner)
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
                addButton.setOnClickListener {
                    if(amountTextInput.text?.isNotEmpty() == true || nameOfExpense.text?.isNotEmpty() == true){
                        val intent = Intent(this, MainActivity::class.java)
                        val amount = amountTextInput.text.toString().toFloat()
                        val type = typeSpinner.selectedItem.toString()
                        val name =  nameOfExpense.text.toString()
                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                        val data = hashMapOf("amount" to amount, "type" to type, "name" to name, "date" to sdf.format(
                            Date()))

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
                scanButton.setOnClickListener {
                    val intent = Intent(this, ScanReceiptActivity::class.java)
                    startActivity(intent)
                    finish()
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



}