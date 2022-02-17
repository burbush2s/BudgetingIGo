package com.example.budgetingigo

import android.app.ActionBar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.budgetingigo.data.BudgetingModelRepository
import com.example.budgetingigo.databinding.FragmentMovementsListBinding

private const val LOG_TAG = "ListMovementsFragment"

class ListMovementsFragment : Fragment() {

    private var viewModel: SharedViewModel? = null
    private var _binding: FragmentMovementsListBinding? = null
    private val binding get() = _binding!!
    var budgetingModelRepository: BudgetingModelRepository = BudgetingModelRepository()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentMovementsListBinding.inflate(inflater, container, false)

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

                }
            })

            viewModel?.getBalances()?.observe(viewLifecycleOwner, { balances ->
                with(balances) {

                    binding.incomesTV.text = balances.incomeBalance.toString()
                    binding.prevBalance.text = balances.previousBalance.toString()
                    binding.expensesTV.text = balances.expenseBalance.toString()
                    binding.currentBalance.text = balances.generalBalance.toString()

                }
            })

            viewModel?.getMovements()?.observe(viewLifecycleOwner,{ moves ->
                binding.movementsList.adapter = MovementsAdapter(moves)
            })

            binding.incomeBtn.setOnClickListener{
                findNavController().navigate(R.id.action_listMovementsFragment_to_incomeFragment)
            }

            binding.expenseBtn.setOnClickListener{
                findNavController().navigate(R.id.action_listMovementsFragment_to_expenseFragment)
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
}
