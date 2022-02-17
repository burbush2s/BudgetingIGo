package com.example.budgetingigo.data

import java.util.*

data class Balances (
    var expenseBalance: Float = 0F,
    var generalBalance: Float= 0F,
    var incomeBalance: Float= 0F,
    var previousBalance: Float= 0F,
    var itemizedBalance: Map<String,Float> = HashMap(),
    val model: BudgetingModel = BudgetingModel()
)