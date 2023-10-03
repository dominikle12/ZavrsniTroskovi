package com.example.zavrsnitroskovi

import android.icu.util.CurrencyAmount
import com.google.type.DateTime

data class Expense(
    val amount: Float,
    val date: String,
    val name: String,
    val type: String
)
