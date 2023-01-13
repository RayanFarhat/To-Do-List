package com.example.ryantodolist.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.ryantodolist.R
import com.example.ryantodolist.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment() {

    private lateinit var auth:FirebaseAuth
    private lateinit var navContol : NavController
    private lateinit var binding :FragmentSignUpBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater,container,false)
        // start only in the SignUpFragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }

    private fun init(view: View){
        navContol = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }

    private fun registerEvents(){

        // if the user want to signin then go to the sign in fragment
        binding.textViewSignIn.setOnClickListener {
            navContol.navigate(R.id.action_signUpFragment_to_signInFragment)
        }

        // when the user press the registration btn
        binding.nextBtn.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passEt.text.toString().trim()
            val verifyPass = binding.verifyPassEt.text.toString().trim()

            // check if the inputs are not empty and pass is right
            if(email.isNotEmpty() && pass.isNotEmpty() && verifyPass.isNotEmpty()){
                if(pass == verifyPass){
                    // create the email in firebase
                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                        // check if the email is created or not
                        if (it.isSuccessful) {
                            Toast.makeText(context,"Registered Successfully",Toast.LENGTH_SHORT).show()
                            // send user to home page
                            navContol.navigate(R.id.action_signUpFragment_to_homeFragment)
                        } else {
                            Toast.makeText(context,it.exception?.message,Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}