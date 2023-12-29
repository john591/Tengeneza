package com.example.tengeneza.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.tengeneza.R
import com.example.tengeneza.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

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
        val currentUser = FirebaseAuth.getInstance().currentUser
        /*val userInfoSharePref = requireContext().getSharedPreferences("UserInfoSharePref", Context.MODE_PRIVATE)
        val getEmail = userInfoSharePref.getString("Email", "")
        val getUserID = userInfoSharePref.getString("ID","")

        if (getEmail != "" && getUserID != ""){
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }*/

        binding.LOGINButton.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()){

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful){
                        /*val userEmail = currentUser?.email
                        val userID = currentUser?.uid
                        val editor = userInfoSharePref.edit()
                        editor.putString("Email", userEmail)
                        editor.putString("ID", userID)
                        editor.apply()*/

                        val intent = Intent(activity, HomeActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    } else {
                        Toast.makeText(requireContext(), "Votre Email Et ou votre mot de passe est incorrect", Toast.LENGTH_SHORT)
                            .show()
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