package com.example.budgetingigo.data

import com.squareup.moshi.Json

data class BudgetingModel (
    @Json(name = "name") val name: String = "",
    val concepts: Map<String,Int> = emptyMap(),
    val description: String= "",
    val imageFile: String="",
    val enabled: Boolean= true

)