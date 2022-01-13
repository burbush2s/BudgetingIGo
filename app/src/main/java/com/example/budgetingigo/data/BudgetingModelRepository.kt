package com.example.budgetingigo.data

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

private const val LOG_TAG = "BudgetingModelRepo"

class BudgetingModelRepository() {


    private var functions: FirebaseFunctions = Firebase.functions
    private val db = FirebaseFirestore.getInstance()
    val moshi = Moshi.Builder().build()


     fun getBudgetingModels(): Query {
        return db.collection("BudgetingModels")
            .whereEqualTo("enabled", true)
    }


    /*public fun prevGetBudgetingModels(): List<BudgetingModel> {
        var result: List<BudgetingModel> = emptyList()
        getBugetingModelsAndroid("hi")
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val e = task.exception
                    if (e is FirebaseFunctionsException) {
                        val code = e.code
                        val details = e.details
                    }

                    // [START_EXCLUDE]
                    Log.w(LOG_TAG, "prevGetBudgetingModels:onFailure", e)
                    return@OnCompleteListener
                    // [END_EXCLUDE]
                }

                // [START_EXCLUDE]

                result = task.result!!
                Log.i(LOG_TAG, "prevGetBudgetingModels:onSuccess ${result.size}")
                // [END_EXCLUDE]
            })
        return result


    }

    private fun getBugetingModelsAndroid(text: String): Task<List<BudgetingModel>> {
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
                val result = task.result?.data as List<BudgetingModel>
                Log.i(LOG_TAG, "getBudgetingModelsAndroid after call ${result.size}")
                result
            }

    }

    private fun addMessage(text: String): Task<HashMap<String, String>> {
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "text" to text,
            "push" to true
        )

        return functions
            .getHttpsCallable("addMessage")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data as HashMap<String, String>
                result
            }
    }

    public fun prevAddMessage(inputMessage: String): HashMap<*, *>{
        var result: HashMap<String, String> = HashMap<String, String>()
        addMessage(inputMessage)
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val e = task.exception
                    if (e is FirebaseFunctionsException) {
                        val code = e.code
                        val details = e.details
                    }

                    // [START_EXCLUDE]
                    Log.w(LOG_TAG, "addMessage:onFailure", e)
                    return@OnCompleteListener
                    // [END_EXCLUDE]
                }

                // [START_EXCLUDE]
                Log.i(LOG_TAG, "addMessage:onSuccess $task.result")
                result = task.result!!

                // [END_EXCLUDE]
            })
        return result
    }*/
}