package com.example.ryantodolist.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
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
import com.example.ryantodolist.utils.ToDoDataJson
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

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

    // to handle the filter
    private var filter: Int = 0


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
        // when press add task
        binding.addTaskBtn.setOnClickListener {
            frag = ToDoDialogFragment()
            frag!!.setListener(this)
            frag!!.show(childFragmentManager, "ToDoDialogFragment")

        }

        // handle filtering
        binding.filter.setOnCheckedChangeListener { _, checkedId ->
             if(checkedId == binding.all.id){
                 this.filter = 0
             }else if (checkedId == binding.done.id){
                 this.filter = 1
             }else{
                 this.filter = 2

             }
            getTaskFromFirebase()
        }
    }

    private fun getTaskFromFirebase() {
        // update the list when the database has changed
        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                Log.i("aaaaaaaaaaaaaa","a")
                // clear all the old tasks
                toDoItemList.clear()
                // add again all the tasks to the list
                for (taskSnapshot in snapshot.children) {
                    val todoTask =
                        taskSnapshot.key?.let { ToDoData(it, taskSnapshot.value.toString()) }

                    // turn the string of data to data class
                    val todoData = Gson().fromJson(taskSnapshot.value.toString(), ToDoDataJson::class.java)

                    if (todoTask != null) {
                        // if done filter is checked
                        if(filter == 1){
                            if(todoData.isDone == "1")
                                toDoItemList.add(todoTask)
                        // if undone filter is checked
                        }else if(filter == 2){
                            if(todoData.isDone == "0")
                                toDoItemList.add(todoTask)
                        }else{
                        toDoItemList.add(todoTask)
                        }
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

    override fun onDelete(toDoData: ToDoData, position: Int) {
        database.child(toDoData.taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEdit(toDoData: ToDoData, position: Int) {
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