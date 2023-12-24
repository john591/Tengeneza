package com.example.tengeneza.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.tengeneza.R
import com.example.tengeneza.databinding.FragmentSignupBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class SignupFragment : Fragment(), View.OnFocusChangeListener {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentSignupBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        binding.signupEmail.onFocusChangeListener = this
        binding.signupPassword.onFocusChangeListener = this
        binding.signupConfirmPassword.onFocusChangeListener = this

        // Set up Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        // Set click listener for the Google Sign-In button
        binding.googleSignInButton.setOnClickListener {
            signIn()
        }

        binding.signupButton.setOnClickListener {
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            val confirmPassword = binding.signupConfirmPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                if (password == confirmPassword){

                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                        if (it.isSuccessful){
                            val loginFragment = LoginFragment()
                            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                            fragmentTransaction.replace(R.id.navHostFragmentContainerView,loginFragment)
                            fragmentTransaction.addToBackStack(null) // Optional
                            fragmentTransaction.commit()
                        } else {
                            //Toast.makeText(this, "Your Email or Password is not correct", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    //Toast.makeText(this, "Password does not matched", Toast.LENGTH_SHORT).show()
                }
            } else {
                //Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        binding.loginRedirectText.setOnClickListener{
            it.findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }

        return binding.root
    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // Handle the result of the Google Sign-In
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            // Signed in successfully, get the GoogleSignInAccount
            val account = completedTask.getResult(ApiException::class.java)
            // Navigate to the main screen (change "your_main_screen" to the appropriate destination)
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
            activity?.finish()
        } catch (e: ApiException) {
            // Handle sign-in failure
            // You might want to implement your own error handling here
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private fun validateEmail(): Boolean {
        var errorMessage: String? = null
        val value = binding.signupEmail.text.toString()
        if (value.isEmpty()){
            errorMessage = "L'adresse Email est obligatoire"
        }else if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()){
            errorMessage = "L'adresse Email est invalide"
        }

        if (errorMessage!=null){
            binding.signupEmail.apply{
                //isErrorEnable = true
                error = errorMessage
            }
        }

        return errorMessage == null
    }

    private fun validatePassword(): Boolean {
        var errorMessage: String? = null
        val value = binding.signupPassword.text.toString()
        if (value.isEmpty()){
            errorMessage = "Mot de passe est obligatoire"
        }else if (value.length < 6){
            errorMessage = "Le mot de passe doit avoir 6 caracteres"
        }

        if (errorMessage!=null){
            binding.signupPassword.apply{
                //isErrorEnable = true
                error = errorMessage
            }
        }

        return errorMessage == null
    }
    private fun validateConfirmPassword(): Boolean{
        var errorMessage: String? = null
        val value = binding.signupConfirmPassword.text.toString()
        if (value.isEmpty()){
            errorMessage = "Mot de passe de confirmation est obligatoire"
        }else if (value.length < 6){
            errorMessage = "Le mot de passe de confirmation doit avoir 6 caracteres"
        }

        if (errorMessage!=null){
            binding.signupConfirmPassword.apply{
                //isErrorEnable = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }
    private fun validatePasswordAndConfirmPassword(): Boolean {
        var errorMessage: String? = null
        val password = binding.signupPassword.text.toString()
        val confirmPassword = binding.signupConfirmPassword.text.toString()
        if (password != confirmPassword){
            errorMessage = "Confirmation du mot de passe ne concorde pas avec le mot de passe"
        }

        if (errorMessage!=null){
            binding.signupPassword.apply{
                //isErrorEnable = true
                errorMessage = errorMessage
            }
        }
        return errorMessage == null
    }

    fun onClick(view: View?){

    }

    override fun onFocusChange(view: View?, hasFocus: Boolean){
        if (view != null){
            when(view.id){
                R.id.signupEmail -> {
                    if (hasFocus){

                    }else{
                        if (validateEmail()){

                        }
                        //validateEmail()
                    }
                }
                R.id.signupPassword -> {
                    if (hasFocus){

                    }else{
                        if (validatePassword() && binding.signupPassword.text!!.isNotEmpty() && validateConfirmPassword() && validatePasswordAndConfirmPassword()){
                           // binding.signupPassword.startD = R.drawable.ic_launcher_foreground
                        }
                        //validatePassword()
                    }
                }
                R.id.signupConfirmPassword -> {
                    if (hasFocus){

                    }else{
                        if (validateConfirmPassword() && validatePassword() && validatePasswordAndConfirmPassword()){
                            //
                        }
                        //validateConfirmPassword()
                    }
                }
            }
        }
    }

    fun onKey(view: View?, event: Int, keyEvent: KeyEvent?): Boolean{
        return false
    }
}