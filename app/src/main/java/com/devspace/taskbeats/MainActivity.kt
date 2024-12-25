package com.devspace.taskbeats

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            TaskBeatDataBase::class.java, "databas-task-beat"
        ).build()
    }

    private val categoryDao by lazy {
        db.getCategoryDao()
    }

    private val taskDao by lazy {
        db.getTaskDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //insertDeafultCategory()
        //insertDefaultTask()

        val rvCategory = findViewById<RecyclerView>(R.id.rv_categories)
        val rvTask = findViewById<RecyclerView>(R.id.rv_tasks)

        val taskAdapter = TaskListAdapter()
        val categoryAdapter = CategoryListAdapter()

        categoryAdapter.setOnClickListener { selected ->
           /* val categoryTemp = categories.map { item ->
                when {
                    item.name == selected.name && !item.isSelected -> item.copy(isSelected = true)
                    item.name == selected.name && item.isSelected -> item.copy(isSelected = false)
                    else -> item
                }
            }*/

           /* val taskTemp =
                if (selected.name != "ALL") {
                    tasks.filter { it.category == selected.name }
                } else {
                    tasks
                }
            taskAdapter.submitList(taskTemp)

            categoryAdapter.submitList(categoryTemp) */
        }

        rvCategory.adapter = categoryAdapter
        getCategoriesFromDatabase(categoryAdapter)

        rvTask.adapter = taskAdapter
        getTaskFromDatabase(taskAdapter)
        // taskAdapter.submitList(tasks)
    }

   /* @OptIn(DelicateCoroutinesApi::class)
    private fun insertDeafultCategory() {

        val categoriesEntity = categories.map {
            CategoryEntity(
                name = it.name,
                isSelected = it.isSelected
            )
        }

        lifecycleScope.launch(Dispatchers.IO) {
            categoryDao.insertAll(categoriesEntity)
        }

    }*/

   /* @OptIn(DelicateCoroutinesApi::class)
    private fun insertDefaultTask() {
        val taskEntities = tasks.map {
            TaskEntity(
                category = it.category,
                name = it.name
            )
        }

        GlobalScope.launch(Dispatchers.IO) {
            taskDao.insertAll(taskEntities)
        }
    }*/

    @OptIn(DelicateCoroutinesApi::class)
    private fun getTaskFromDatabase(taskAdapter: TaskListAdapter) {

        lifecycleScope.launch(Dispatchers.IO) {
            val taskFromDb: List<TaskEntity> = taskDao.getAll()

            val taskiesUiData = taskFromDb.map {
                TaskUiData(
                    name = it.name,
                    category = it.category
                )
            }
            withContext(Dispatchers.Main){
                taskAdapter.submitList(taskiesUiData)
            }

        }

    }


    private fun getCategoriesFromDatabase(categoryListAdapter: CategoryListAdapter) {
        lifecycleScope.launch(Dispatchers.IO) {
            val categorieFromDb: List<CategoryEntity> = categoryDao.getlAll()

            val categoriesUiData = categorieFromDb.map {
                CategoryUiData(
                    name = it.name,
                    isSelected = it.isSelected
                )
            }.toMutableList()

            //add fake + category

            categoriesUiData.add(
                CategoryUiData(
                    name = "+",
                    isSelected = false
                )
            )
            withContext(Dispatchers.Main){
                categoryListAdapter.submitList(categoriesUiData)
            }


        }
    }
}

/*val categories = listOf(
    CategoryUiData(
        name = "ALL",
        isSelected = false
    ),
    CategoryUiData(
        name = "STUDY",
        isSelected = false
    ),
    CategoryUiData(
        name = "WORK",
        isSelected = false
    ),
    CategoryUiData(
        name = "WELLNESS",
        isSelected = false
    ),
    CategoryUiData(
        name = "HOME",
        isSelected = false
    ),
    CategoryUiData(
        name = "HEALTH",
        isSelected = false
    ),
)*/

