package com.devspace.taskbeats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class CreateOrUpdateTaskBottomSheet(
    private val categoryList: List<CategoryUiData>,
    private val task: TaskUiData?= null,
    private val onCreateCliked: (TaskUiData) -> Unit,
    private val onUpdateCliked: (TaskUiData) -> Unit,
    private val onDeleteCliked: (TaskUiData) -> Unit

) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_or_update_task_bottom_sheet, container, false)


        val tv_title_task= view.findViewById<TextView>(R.id.tv_create_task_title)
        val btn_CreatTask = view.findViewById<Button>(R.id.btn_create_task)
        val btn_deleteTask = view.findViewById<Button>(R.id.btn_delete_task)
        val edt_task = view.findViewById<TextInputEditText>(R.id.edt_task)


        val categoryString: List<String> = categoryList.map { it.name }
        var taskCategory: String? = null
        val spinner: Spinner = view.findViewById(R.id.categoryList_spinner)

        ArrayAdapter(
            requireActivity().baseContext,
            android.R.layout.simple_spinner_item,
            categoryString.toList()
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                taskCategory = categoryString[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        if (task== null){
            btn_deleteTask.isVisible=false
            tv_title_task.setText(R.string.create_task_title)
            btn_CreatTask.setText(R.string.create)

        }else{
            btn_deleteTask.isVisible=true
            tv_title_task.setText(R.string.update_task_title)
            btn_CreatTask.setText(R.string.update)
            edt_task.setText(task.name)

            val currentCategory= categoryList.first { it.name== task.category }
           val index=  categoryList.indexOf(currentCategory)
            spinner.setSelection(index)

        }


        btn_CreatTask.setOnClickListener {
            val name = edt_task.text.toString().trim()
            if (taskCategory != null|| name.trim().isEmpty()) {

                if (task== null){
                    onCreateCliked.invoke(

                        TaskUiData(
                            id=0,
                            name = name,
                            category = requireNotNull(taskCategory)
                        )
                    )
                }else{

                    onUpdateCliked.invoke(
                        TaskUiData(
                            id=task.id,
                            name=name,
                            category = requireNotNull(taskCategory)
                        )
                    )
                }

                dismiss()
            } else {
                Snackbar.make(
                    btn_CreatTask,
                    "Por favor Selecione uma Categoria",
                    Snackbar.LENGTH_LONG
                ).show()
            }

        }

        btn_deleteTask.setOnClickListener {
            if (task!= null){
                onDeleteCliked.invoke(task)

                dismiss()

            }else{
Log.d("CreateOrUpdateTaskBottomSheet","Task not found")
            }
        }


        return view
    }
}