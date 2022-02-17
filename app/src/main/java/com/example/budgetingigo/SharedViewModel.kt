package com.example.budgetingigo

import android.util.Log
import androidx.lifecycle.*
import com.example.budgetingigo.data.Balances
import com.example.budgetingigo.data.BudgetingModel
import com.example.budgetingigo.data.BudgetingModelRepository
import com.example.budgetingigo.data.Movements
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot

private const val LOG_TAG = "SharedViewModel"

class SharedViewModel : ViewModel() {

    var budgetingModelRepository: BudgetingModelRepository = BudgetingModelRepository()
    var modelsMutable : MutableLiveData<List<BudgetingModel>> = MutableLiveData()
    var conceptsMutable : MutableLiveData<List<Pair<String,Float?>>> = MutableLiveData()
    var conceptsListMutable : MutableLiveData<List<Pair<String, Float>>> = MutableLiveData()
    var selectedBudgetingModel: MutableLiveData<BudgetingModel> =MutableLiveData()
    var movesMutable : MutableLiveData<List<Movements>> = MutableLiveData()
    var balancesMutable : MutableLiveData<Balances> = MutableLiveData()
    var conceptValuesMutable : MutableLiveData<List<Pair<String,Float?>>> = MutableLiveData()
    var conceptValuesListMutable : MutableLiveData<List<Pair<String, Float>>> = MutableLiveData()

    fun getModels(): LiveData<List<BudgetingModel>>{

        val modelsList : MutableList<BudgetingModel> = mutableListOf()
        budgetingModelRepository.getBudgetingModels()
            .addOnSuccessListener { documents ->
                for (document in documents!!) {
                    Log.d(LOG_TAG, "${document.id} => ${document.data}")
                    modelsList.add(document.toObject(BudgetingModel::class.java))
                    Log.d(LOG_TAG, "${modelsList.size}")
                }
                modelsMutable.value = modelsList
            }
            .addOnFailureListener{
                    Log.w(LOG_TAG, "Listen failed.", it)
            }

        return modelsMutable
    }

    fun getConceptsAndPercentages(income: Float):LiveData<List<Pair<String,Float?>>>{
        Log.i(LOG_TAG, "getConceptsAndPercentages()")
        val conceptsPercentages: MutableList<Pair<String, Float?>> = mutableListOf()
        val conceptsListModified : MutableList<Pair<String, Float>> = mutableListOf()
        val concepts : List<Pair<String, Float>>? = selectedBudgetingModel.value?.concepts?.toList()
        if (concepts != null) {
            for (concept in concepts){
                val result = (concept.second * income)/100
                conceptsPercentages.add(Pair(concept.first+ " / "+concept.second + "%: $"+result,null))
                conceptsListModified.add(Pair(concept.first,result))
            }
        }
        conceptsMutable.value =conceptsPercentages
        conceptsListMutable.value = conceptsListModified
        return conceptsMutable
    }

    fun getMovements() : LiveData<List<Movements>>{
        val movesList : MutableList<Movements> = mutableListOf()
        budgetingModelRepository.getMovements()
            .addSnapshotListener(EventListener<QuerySnapshot> { documents, e ->
                if (e != null) {
                    Log.w(LOG_TAG, "Listen failed.", e)
                    modelsMutable.value = null
                    return@EventListener
                }
                for (document in documents!!) {
                    Log.d(LOG_TAG, "getMovements ${document.id} => ${document.data}")
                    movesList.add(document.toObject(Movements::class.java))
                    Log.d(LOG_TAG, "getMovements ${movesList.size}")
                }
                movesMutable.value = movesList
        })
        return movesMutable
    }

    fun getBalances() : LiveData<Balances>{
        var balances = Balances()
        balances.itemizedBalance = mapOf()
        budgetingModelRepository.getBalances()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    Log.d(LOG_TAG, "getBalances - DocumentSnapshot data: ${document.data}")
                    Log.d(LOG_TAG, "${document.id} => ${document.data}")
                    balances = document.toObject(Balances::class.java)!!
                }
                balancesMutable.value = balances
            }
        return balancesMutable
    }

    fun setBalances(balancesConsulted: Balances){
        balancesMutable.value = balancesConsulted
        selectedBudgetingModel.value = balancesConsulted.model
    }

    fun getConceptsValuesForExpenses(expense: Float, concept : String):LiveData<List<Pair<String,Float?>>>{
        Log.i(LOG_TAG, "getConceptsValuesForExpenses()")
        val conceptsPercentages = mutableListOf<Pair<String,Float?>>()
        val conceptsListModified : MutableList<Pair<String, Float>> = mutableListOf()
        val prevConceptValues : List<Pair<String, Float>>? = balancesMutable.value?.itemizedBalance?.toList()
        if (prevConceptValues != null) {
            Log.i(LOG_TAG, "getConceptsValuesForExpenses()")
            for (prevValue in prevConceptValues) {
                val result = prevValue.second - expense
                if (concept.equals(prevValue.first)) {
                    conceptsPercentages.add(Pair(prevValue.first + " = $" + prevValue.second + " - $" + expense + " = $",result))
                    conceptsListModified.add(Pair(prevValue.first, result))
                } else {
                    conceptsPercentages.add(Pair(prevValue.first + " = $" + prevValue.second,null))
                    conceptsListModified.add(Pair(prevValue.first, result))
                }
            }
        }
        conceptValuesMutable.value =conceptsPercentages
        conceptValuesListMutable.value = conceptsListModified
        return conceptValuesMutable
    }
}