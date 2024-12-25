package com.devspace.taskbeats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class CreateTaskBottomSheet(
    private val categoryList: List<CategoryUiData>,
    private val onCreateCliked: (TaskUiData) -> Unit

) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_task_bottom_sheet, container, false)


        val btn_CreatTask = view.findViewById<Button>(R.id.btn_create_task)
        val edt_task = view.findViewById<TextInputEditText>(R.id.edt_task)


        var taskCategory: String? = null
        btn_CreatTask.setOnClickListener {
            val task = edt_task.text.toString()
            if (taskCategory != null) {
                onCreateCliked.invoke(

                    TaskUiData(
                        name = task,
                        category = requireNotNull(taskCategory)
                    )
                )
                dismiss()
            } else {
                Snackbar.make(
                    btn_CreatTask,
                    "Por favor Selecione uma Categoria",
                    Snackbar.LENGTH_LONG
                ).show()
            }

        }
        val categoryString: List<String> = categoryList.map { it.name }

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

        return view
    }
}