package com.example.budgetingigo

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.budgetingigo.data.Balances
import com.example.budgetingigo.data.BudgetingModelRepository
import com.example.budgetingigo.databinding.FragmentFirstBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
private const val LOG_TAG = "FirstFragment"

class FirstFragment : Fragment() {
    var budgetingModelRepository: BudgetingModelRepository = BudgetingModelRepository()
    private var viewModel: SharedViewModel? = null
    private lateinit var auth: FirebaseAuth
    private var listener: OnEventListener? = null

    interface OnEventListener {
        fun hideToolbar()
    }

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.run {
            ViewModelProvider(this)[SharedViewModel::class.java]
        }

        listener?.hideToolbar()
        // Initialize Firebase Auth
        auth = Firebase.auth

        // Buttons
        with (binding) {
            emailSignInButton.setOnClickListener {
                val email = binding.fieldEmail.text.toString()
                val password = binding.fieldPassword.text.toString()
                signIn(email, password)
            }

            sigupButton.setOnClickListener{
                findNavController().navigate(R.id.action_FirstFragment_to_signupFragment)
            }
        }



    }


    private fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")

                    budgetingModelRepository.getBalances()
                        .addOnSuccessListener { document ->
                            if (document.data != null) {
                                Log.d(LOG_TAG, "getBalances - FirstFragment - DocumentSnapshot data: ${document.data}")
                                val prevData = document.toObject(Balances::class.java)!!
                                viewModel?.setBalances(prevData)
                                findNavController().navigate(R.id.action_FirstFragment_to_listMovementsFragment)
                            }else
                                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                            (requireActivity() as MainActivity).showHideToolbar(true)
                        }
                        .addOnFailureListener {e ->
                            Log.w(TAG, "signInWithEmail:failure", e)
                            Toast.makeText(context, "Failed to get previous data.",
                                Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }

            }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.fieldEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.fieldEmail.error = "Required."
            valid = false
        } else {
            binding.fieldEmail.error = null
        }

        val password = binding.fieldPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.fieldPassword.error = "Required."
            valid = false
        } else {
            binding.fieldPassword.error = null
        }

        return valid
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}