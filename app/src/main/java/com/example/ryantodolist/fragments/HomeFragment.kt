package com.example.ryantodolist.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ryantodolist.databinding.FragmentHomeBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.ryantodolist.utils.ToDoAdapter
import  com.example.ryantodolist.utils.ToDoData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment(), ToDoDialogFragment.OnDialogNextBtnClickListener,
    ToDoAdapter.TaskAdapterInterface {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var navContol : NavController
    private lateinit var binding : FragmentHomeBinding
    // to create the class when we want to add or edit task
    private var frag: ToDoDialogFragment? = null

    // for handling array of tasks
    private lateinit var taskAdapter: ToDoAdapter
    private lateinit var toDoItemList: MutableList<ToDoData>

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

        //get data from firebase
        getTaskFromFirebase()

        registerEvents()
    }

    private fun init(view: View) {
        navContol = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        // get the database of Tasks for the user
        database = FirebaseDatabase.getInstance("https://ryantodolist-edf06-default-rtdb.europe-west1.firebasedatabase.app")
            .reference.child("Tasks").child(auth.currentUser!!.uid)

        // init the array of items
        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)
        toDoItemList = mutableListOf()
        taskAdapter = ToDoAdapter(toDoItemList)
        taskAdapter.setListener(this)
        // now make the mainRecyclerView handle the toDoAdapter class
        binding.mainRecyclerView.adapter = taskAdapter

    }
    private fun registerEvents(){
        binding.addTaskBtn.setOnClickListener {
            frag = ToDoDialogFragment()
            frag!!.setListener(this)
            frag!!.show(childFragmentManager, "ToDoDialogFragment")

        }
    }

    private fun getTaskFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // clear all the old tasks
                toDoItemList.clear()
                // add again all the tasks to the list
                for (taskSnapshot in snapshot.children) {
                    val todoTask =
                        taskSnapshot.key?.let { ToDoData(it, taskSnapshot.value.toString()) }

                    if (todoTask != null) {
                        toDoItemList.add(todoTask)
                    }
                }
                taskAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }


        })
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

    override fun updateTask(toDoData: ToDoData, todoEdit: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[toDoData.taskId] = toDoData.task
        database.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
            frag!!.dismiss()
        }
    }

    override fun onDeleteItemClicked(toDoData: ToDoData, position: Int) {
        database.child(toDoData.taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditItemClicked(toDoData: ToDoData, position: Int) {
        if (frag != null)
            childFragmentManager.beginTransaction().remove(frag!!).commit()

        frag = ToDoDialogFragment.newInstance(toDoData.taskId, toDoData.task)
        frag!!.setListener(this)
        frag!!.show(
            childFragmentManager,
            ToDoDialogFragment.TAG
        )
    }

}