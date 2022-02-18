package com.example.budgetingigo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.budgetingigo.data.Balances
import com.example.budgetingigo.data.BudgetingModel
import com.example.budgetingigo.data.BudgetingModelRepository
import com.example.budgetingigo.data.Movements
import com.example.budgetingigo.databinding.FragmentExpenseBinding

private const val LOG_TAG = "ExpenseFragment"

class ExpenseFragment : Fragment() {
    private var viewModel: SharedViewModel? = null
    private var _binding: FragmentExpenseBinding? = null
    private val binding get() = _binding!!
    private var modelSelected: BudgetingModel = BudgetingModel()
    var expense = 0F
    var budgetingModelRepository: BudgetingModelRepository = BudgetingModelRepository()

    private val onColorChange: (Boolean) -> Unit = {hasNegativeValue ->
        if(hasNegativeValue){
            binding.buttonSave.isEnabled = false
            Toast.makeText(context, "Please select a lesser amount or a different concept.",
                Toast.LENGTH_SHORT).show()
            Log.i(LOG_TAG, "Has a negative value.")
        }else{
            binding.buttonSave.isEnabled = true
            Log.i(LOG_TAG, "Positive values.")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


            _binding = FragmentExpenseBinding.inflate(inflater, container, false)
            return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.run {
            ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        }

        val spinner = binding.spinnerConcepts

        val itemsSpinner: MutableList<String> = mutableListOf()

        Log.d(LOG_TAG, "concepts: "+ viewModel?.selectedBudgetingModel?.value )

        for(i in viewModel?.selectedBudgetingModel?.value?.concepts?.toList()!!){
            itemsSpinner.add(i.first)
        }


        val spinnerAdapter = activity?.let{
            ArrayAdapter(it, android.R.layout.simple_spinner_item,itemsSpinner)
        }

        spinner.adapter = spinnerAdapter

        viewModel?.selectedBudgetingModel?.observe(viewLifecycleOwner, { model ->
            with(model) {

                binding.modelNameText.text = name
                modelSelected = model
            }
        })

        if(binding.addedExpense.text.toString()!="")
            expense = binding.addedExpense.text.toString().toFloat()

        viewModel?.getConceptsValuesForExpenses(expense,spinner.selectedItem.toString())?.observe(viewLifecycleOwner,{ concepts ->
            binding.conceptValuesList.adapter = ConceptsAdapter(concepts, onColorChange)
        })

        binding.addedExpense.doAfterTextChanged {
            updateView(spinner.selectedItem.toString())
        }

        binding.spinnerConcepts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateView(spinner.selectedItem.toString())
            }

        }

        binding.buttonSave.setOnClickListener {
            binding.buttonSave.isEnabled = false
            val newBalances = Balances()
            val conceptsListPrev : List<Pair<String, Float>>
            var conceptsListNew : MutableList<Pair<String, Float>> = mutableListOf()
            val conceptsListNewValues = viewModel?.conceptValuesListMutable!!.value!!.toList()
            var hasPreviusData = false
            val previousBal = viewModel?.balancesMutable?.value
            if(previousBal != null){
                Log.d(LOG_TAG, "buttonSave - saveIncome - previousBal != null")
                //values without changes
                newBalances.previousBalance = previousBal.generalBalance
                newBalances.incomeBalance = previousBal.incomeBalance

                //values with changes
                newBalances.expenseBalance = previousBal.expenseBalance + expense
                newBalances.generalBalance = previousBal.generalBalance - expense

                conceptsListPrev = previousBal.itemizedBalance.toList()
                for((i, c) in conceptsListPrev.withIndex()){
                    val p= Pair(c.first,c.second+conceptsListNewValues[i].second)
                    conceptsListNew.add(p)
                }
                hasPreviusData = true
            }else{
                newBalances.expenseBalance = expense
                newBalances.generalBalance = - expense
                conceptsListNew = viewModel?.conceptsListMutable?.value?.toList() as MutableList<Pair<String, Float>>
            }

            newBalances.itemizedBalance = conceptsListNew.associate { Pair(it.first,it.second) }
            val movement = Movements(concept = spinner.selectedItem.toString(),
                description = binding.expenseDescription.editableText.toString(),
                amount = expense, type = "-")
            newBalances.itemizedBalance = conceptsListNew.associate { Pair(it.first,it.second) }
            budgetingModelRepository.saveMovement(
                modelSelected,
                movement,
                newBalances, hasPreviusData
            ).addOnSuccessListener {
                Log.d(LOG_TAG, "buttonSave - saveIncome - DocumentSnapshot successfully written!")
                Toast.makeText(context, "Income correctly saved!!!",
                    Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_expenseFragment_to_listMovementsFragment)
            }
                .addOnFailureListener { e ->
                    Log.w(
                        LOG_TAG,
                        "buttonSave - saveIncome - Error writing document",
                        e
                    )
                    Toast.makeText(context, "Something went wrong, try it later.",
                        Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun updateView(selectedItem : String){
        if(binding.addedExpense.text.toString()!="")
            expense = binding.addedExpense.text.toString().toFloat()
        viewModel?.getConceptsValuesForExpenses(expense,selectedItem)
        binding.conceptValuesList.adapter?.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}