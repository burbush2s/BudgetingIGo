package com.example.budgetingigo.data

import com.google.firebase.Timestamp
import com.squareup.moshi.Json
import java.util.*

data class Movements (
    @Json(name = "user") val user: String = "",
    val concept: String= "",
    val description: String= "",
    val amount: Float = 0F,
    val date: Timestamp = Timestamp(Date()),
    val type: String =""
)