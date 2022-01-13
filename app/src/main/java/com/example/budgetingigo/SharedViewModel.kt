package com.example.budgetingigo

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.budgetingigo.data.BudgetingModel
import com.example.budgetingigo.data.BudgetingModelRepository
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot

private const val LOG_TAG = "SharedViewModel"

class SharedViewModel : ViewModel() {

    var budgetingModelRepository: BudgetingModelRepository = BudgetingModelRepository()
    var modelsMutable : MutableLiveData<List<BudgetingModel>> = MutableLiveData()
    val selectedBudgetingModel: MutableLiveData<BudgetingModel> =MutableLiveData()

    fun getModels(): LiveData<List<BudgetingModel>>{

        var modelsList : MutableList<BudgetingModel> = mutableListOf()
        val ref = budgetingModelRepository.getBudgetingModels().addSnapshotListener(EventListener<QuerySnapshot> { documents, e ->
            if (e != null) {
                Log.w(LOG_TAG, "Listen failed.", e)
                modelsMutable.value = null
                return@EventListener
            }
            for (document in documents!!) {
                Log.d(LOG_TAG, "${document.id} => ${document.data}")
                modelsList.add(document.toObject(BudgetingModel::class.java))
                Log.d(LOG_TAG, "${modelsList.size}")
            }
            modelsMutable.value = modelsList
        })
        return modelsMutable
    }
}