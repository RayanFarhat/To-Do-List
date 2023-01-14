package com.example.ryantodolist.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.ryantodolist.R
import com.example.ryantodolist.databinding.FragmentHomeBinding
import com.example.ryantodolist.databinding.FragmentToDoDialogBinding
import com.google.android.material.textfield.TextInputEditText

// change to DialogFragment to get the show method
class ToDoDialogFragment : DialogFragment() {

    private lateinit var binding : FragmentToDoDialogBinding
    private var listener : OnDialogNextBtnClickListener? = null

    // to init the reference
    fun setListener(listener: OnDialogNextBtnClickListener) {
        this.listener = listener
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToDoDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerEvents()
    }
    private fun registerEvents(){
        // when we save the new task
        binding.todoNextBtn.setOnClickListener {
            // get the new task
            val todoTask = binding.todoEt.text.toString()
            if (todoTask.isNotEmpty()){
                listener?.saveTask(todoTask , binding.todoEt)
            }else{
                Toast.makeText(context,"Please type something",Toast.LENGTH_SHORT).show()
            }
        }

        // when we exit the task editor
        binding.todoClose.setOnClickListener {
            // remove the fragment
            dismiss()
        }
    }

    // so we dont make firebase object every time we add or edit task,
    // we just add reference to function of home fragment
    interface OnDialogNextBtnClickListener{
        fun saveTask(todoTask:String , todoEdit:TextInputEditText)
    }
}