package com.example.budgetingigo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.budgetingigo.data.Balances
import com.example.budgetingigo.data.BudgetingModel
import com.example.budgetingigo.data.BudgetingModelRepository
import com.example.budgetingigo.data.Movements
import com.example.budgetingigo.databinding.FragmentIncomeBinding

private const val LOG_TAG = "IncomeFragment"

class IncomeFragment : Fragment() {
    private var viewModel: SharedViewModel? = null
    private var _binding: FragmentIncomeBinding? = null
    private val binding get() = _binding!!
    private var modelSelected: BudgetingModel = BudgetingModel()
    var income = 0F
    var budgetingModelRepository: BudgetingModelRepository = BudgetingModelRepository()
    private val onColorChange: (Boolean) -> Unit = {hasNegativeValue ->
        if(hasNegativeValue){
            binding.buttonSave.isEnabled = false
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
        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.run {
            ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        }

        viewModel?.selectedBudgetingModel?.observe(viewLifecycleOwner, { model ->
            with(model) {

                binding.modelNameText.text = name
                modelSelected = model
            }
        })

        if(binding.addedIncome.text.toString()!="")
            income = binding.addedIncome.text.toString().toFloat()

        viewModel?.getConceptsAndPercentages(income)?.observe(viewLifecycleOwner,{ concepts ->
            binding.conceptsList.adapter = ConceptsAdapter(concepts,onColorChange)
        })


        binding.addedIncome.doAfterTextChanged {
            if(binding.addedIncome.text.toString()!="")
                income = binding.addedIncome.text.toString().toFloat()
            viewModel?.getConceptsAndPercentages(income)
            binding.conceptsList.adapter?.notifyDataSetChanged()
        }

        binding.buttonSave.setOnClickListener {
            binding.buttonSave.isEnabled = false
            val newBalances = Balances()
            val conceptsListPrev : List<Pair<String, Float>>
            var conceptsListNew : MutableList<Pair<String, Float>> = mutableListOf()
            val conceptsListNewValues = viewModel?.conceptsListMutable!!.value!!.toList()
            var hasPreviusData = false
            val previousBal = viewModel?.balancesMutable?.value
            if(previousBal != null){
                Log.d(LOG_TAG, "buttonSave - saveIncome - previousBal != null")
                //values without changes
                newBalances.previousBalance = previousBal.generalBalance
                newBalances.expenseBalance  = previousBal.expenseBalance
                //values with changes
                newBalances.incomeBalance = previousBal.incomeBalance + income
                newBalances.generalBalance = previousBal.generalBalance + income

                conceptsListPrev = previousBal.itemizedBalance.toList()
                for((i, c) in conceptsListPrev.withIndex()){
                    val p= Pair(c.first,c.second+conceptsListNewValues[i].second)
                    conceptsListNew.add(p)
                }
                hasPreviusData = true
            }else{
                newBalances.incomeBalance = income
                newBalances.generalBalance = income
                conceptsListNew = viewModel?.conceptsListMutable?.value?.toList() as MutableList<Pair<String, Float>>
            }
            val movement = Movements(concept = "Income",description = binding.incomeDescription.editableText.toString(),
            amount = income, type = "+")
            newBalances.itemizedBalance = conceptsListNew.associate { Pair(it.first,it.second) }
            budgetingModelRepository.saveMovement(
                modelSelected,
                movement,
                newBalances, hasPreviusData
            ).addOnSuccessListener {
                Log.d(LOG_TAG, "buttonSave - saveIncome - DocumentSnapshot successfully written!")
                Toast.makeText(context, "Income correctly saved!!!",
                    Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_incomeFragment_to_listMovementsFragment)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}