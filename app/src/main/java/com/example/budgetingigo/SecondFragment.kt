package com.example.budgetingigo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.budgetingigo.data.BudgetingModel
import com.example.budgetingigo.databinding.FragmentSecondBinding

private const val LOG_TAG = "SecondFragment"
/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var viewModel: SharedViewModel? = null
    private var _binding: FragmentSecondBinding? = null
    private val onItemClick: (BudgetingModel) -> Unit = {model ->
        Log.i(LOG_TAG, "The selected model: $model")
        viewModel?.selectedBudgetingModel?.value = model
        findNavController().navigate(R.id.action_SecondFragment_to_detailFragment)
    }
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.run {
            ViewModelProvider(this)[SharedViewModel::class.java]
        }

        viewModel?.getModels()?.observe(viewLifecycleOwner,{models ->
            binding.modelList.adapter = BudgetingModelAdapter(models, onItemClick)
        })

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}