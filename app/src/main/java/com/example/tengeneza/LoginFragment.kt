package com.example.tengeneza

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.tengeneza.databinding.FragmentLoginBinding
import com.example.tengeneza.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.example.tengeneza.HomeActivity
import com.example.tengeneza.R


class LoginFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.LOGINButton.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()){

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful){
                        val intent = Intent(activity, HomeActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    } else {
                       // Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        binding.signupRedirectText.setOnClickListener{
            it.findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        return binding.root
    }
}