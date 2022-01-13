package com.example.budgetingigo.data

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

private const val LOG_TAG = "BudgetingModelApi"

class BudgetingModelApi {

    private var functions: FirebaseFunctions = Firebase.functions

    //suspend fun getBudgetingModels(): Response<List<BudgetingModel>>

    suspend fun getBugetingModelsAndroid(text: String): Task<String> {
        Log.i(LOG_TAG, "getBugetingModelsAndroid started")
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "text" to text,
            "push" to true
        )

        return functions
            .getHttpsCallable("getBudgetingModelsAndroid")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                Log.i(LOG_TAG, "getBudgetingModelsAndroid before call")
                val result = task.result?.data as String
                Log.i(LOG_TAG, "getBudgetingModelsAndroid after call $result")
                result
            }

    }
}