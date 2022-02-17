package com.example.budgetingigo.data

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.util.*

private const val LOG_TAG = "BudgetingModelRepo"

class BudgetingModelRepository() {

    private val db = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getBudgetingModels(): Task<QuerySnapshot> {
        return db.collection("BudgetingModels")
            .whereEqualTo("enabled", true).get()
    }

    fun saveMovement(model:BudgetingModel, movement : Movements, balances: Balances, hasPreviusData: Boolean): Task<Void>{
        val user = auth.currentUser!!.uid
        Log.d(LOG_TAG, "auth: " + auth + " user : " + user)
        val movement = hashMapOf(
            "user" to user,
            "concept" to movement.concept,
            "description" to movement.description,
            "amount" to movement.amount,
            "date" to Timestamp(Date()),
            "type" to movement.type
        )

        val modelObj = hashMapOf(
            "name" to model.name,
            "concepts" to model.concepts
        )

        val userObj = hashMapOf(
            "generalBalance" to balances.generalBalance,
            "itemizedBalance" to balances.itemizedBalance,
            "incomeBalance" to balances.incomeBalance,
            "expenseBalance" to balances.expenseBalance,
            "previousBalance" to balances.previousBalance,
            "model" to modelObj
        )


        val balanceRef = db.collection("balances").document(user)
        val movesRef = db.collection("movements").document()
        return db.runBatch { batch ->
            if(hasPreviusData)
                batch.update(balanceRef, userObj)
            else
                batch.set(balanceRef, userObj)
            batch.set(movesRef, movement)
        }
    }

    fun getBalances() : Task<DocumentSnapshot>{
        val docRef = db.collection("balances").document(auth.currentUser!!.uid)
        return docRef.get()
    }

    fun getMovements(): Query {
        return db.collection("movements")
            .whereEqualTo("user", auth.currentUser!!.uid)
            .orderBy("date",Query.Direction.DESCENDING)
            .limit(10)

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