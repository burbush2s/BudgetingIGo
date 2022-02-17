package com.example.budgetingigo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.budgetingigo.databinding.FragmentDetailBinding

private const val LOG_TAG = "DetailFragment"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailFragment : Fragment() {
    private var viewModel: SharedViewModel? = null
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.run {
            ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        }

        viewModel?.selectedBudgetingModel?.observe(viewLifecycleOwner, { model ->
            with(model) {
                binding.modelImage.load(imageFile)
                binding.modelNameText.text = name
                binding.descriptionText.text = description
            }
        })

        binding.selectModelButton.setOnClickListener {
            Log.i(LOG_TAG, "selectModelButton clicked")
            findNavController().navigate(R.id.action_detailFragment_to_incomeFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}