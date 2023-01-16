package com.example.ryantodolist.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ryantodolist.databinding.EachTodoItemBinding
import com.google.gson.Gson

// get he list of tododata objects
class ToDoAdapter(private val list:MutableList<ToDoData>)
    : RecyclerView.Adapter<ToDoAdapter.TaskViewHolder>(){

    private var listener:TaskAdapterInterface? = null

    fun setListener(listener: TaskAdapterInterface){
        this.listener = listener
    }

    inner class TaskViewHolder(val binding:EachTodoItemBinding ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding =
            EachTodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                // turn the string to data class
                val todo = Gson().fromJson(this.task, ToDoDataJson::class.java)
                // update the card
                if(todo.desc.length > 22)
                    binding.todoTask.text = todo.desc.substring(0, 22) + "..."
                else
                    binding.todoTask.text = todo.desc

                binding.todoDate.text = todo.date

                if(todo.isDone == "1") {
                    binding.todoIsDone.text = "Done"
                }
                else {
                    binding.todoIsDone.text = "Undone"
                }

                binding.editTask.setOnClickListener {
                    listener?.onEdit(this , position)
                }

                binding.deleteTask.setOnClickListener {
                    listener?.onDelete(this , position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    // add reference to home fragment
    interface TaskAdapterInterface{
        fun onDelete(toDoData: ToDoData, position : Int)
        fun onEdit(toDoData: ToDoData, position: Int)
    }
}