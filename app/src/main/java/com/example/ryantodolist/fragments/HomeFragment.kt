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
import com.example.ryantodolist.databinding.FragmentHomeBinding
import com.example.ryantodolist.databinding.FragmentSignInBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HomeFragment : Fragment(), ToDoDialogFragment.OnDialogNextBtnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var navContol : NavController
    private lateinit var binding : FragmentHomeBinding
    // to create the class when we want to add or edit task
    private var frag: ToDoDialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        // start only in the HomeFragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }

    private fun init(view: View) {
        navContol = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        // get the database of Tasks for the user
        database = FirebaseDatabase.getInstance("https://ryantodolist-edf06-default-rtdb.europe-west1.firebasedatabase.app")
            .reference.child("Tasks").child(auth.currentUser!!.uid)
    }
    private fun registerEvents(){
        binding.addTaskBtn.setOnClickListener {
            frag = ToDoDialogFragment()
            frag!!.setListener(this)
            frag!!.show(childFragmentManager, "ToDoDialogFragment")

        }
    }

    // add new data element to the database
    override fun saveTask(todoTask: String, todoEdit: TextInputEditText) {

        database.push().setValue(todoTask).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Task Added Successfully", Toast.LENGTH_SHORT).show()
                todoEdit.text = null

            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        frag!!.dismiss()
    }
}