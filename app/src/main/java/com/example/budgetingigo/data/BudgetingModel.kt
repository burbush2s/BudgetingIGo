package com.example.budgetingigo.data

import com.squareup.moshi.Json

data class BudgetingModel (
    @Json(name = "name") val name: String = "",
    val concepts: HashMap<String,Float> = HashMap(),
    val description: String= "",
    val imageFile: String="",
    val enabled: Boolean= true

)