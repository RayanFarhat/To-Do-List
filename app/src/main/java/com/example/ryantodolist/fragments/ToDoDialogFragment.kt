package com.example.ryantodolist.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.ryantodolist.databinding.FragmentToDoDialogBinding
import com.example.ryantodolist.utils.ToDoData
import com.example.ryantodolist.utils.ToDoDataJson
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

// change to DialogFragment to get the show method
class ToDoDialogFragment : DialogFragment() {

    private lateinit var binding : FragmentToDoDialogBinding
    private var listener : OnDialogNextBtnClickListener? = null
    private var toDoData: ToDoData? = null

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

        if (arguments != null){

            toDoData = ToDoData(
                arguments?.getString("taskId").toString() ,
                arguments?.getString("task").toString())
            binding.todoEt.setText(toDoData?.task)
        }

        registerEvents()
    }
    private fun registerEvents(){
        // when we save the new task
        binding.todoNextBtn.setOnClickListener {
            // get the new task
            val todoTask = binding.todoEt.text.toString()
            val todoDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
            // stringify the new task as tododatajson
            val jsonString = Gson().toJson(ToDoDataJson(todoTask,todoDate))  // json string

            if (todoTask.isNotEmpty()){
                if (toDoData == null){
                    listener?.saveTask(jsonString , binding.todoEt)
                }else{
                    toDoData!!.task = jsonString
                    listener?.updateTask(toDoData!!, binding.todoEt)
                }

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
        fun updateTask(toDoData: ToDoData, todoEdit:TextInputEditText)
    }

    companion object {
        const val TAG = "DialogFragment"
        @JvmStatic
        fun newInstance(taskId: String, task: String) =
            ToDoDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("taskId", taskId)
                    putString("task", task)
                }
            }
    }
}