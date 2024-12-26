package com.devspace.taskbeats

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var categories = listOf<CategoryUiData>()
    private var tasks = listOf<TaskUiData>()
    private val categoryAdapter = CategoryListAdapter()

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
    private val taskAdapter by lazy {
        TaskListAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //insertDeafultCategory()
        //insertDefaultTask()

        val rvCategory = findViewById<RecyclerView>(R.id.rv_categories)
        val rvTask = findViewById<RecyclerView>(R.id.rv_tasks)
        val fabCreateTask = findViewById<FloatingActionButton>(R.id.fab_create_task)




        fabCreateTask.setOnClickListener {
            showCreateUpdateTaskBottomSheet()
        }
        taskAdapter.setOnClicklistner {task->
            showCreateUpdateTaskBottomSheet(task)
        }


        categoryAdapter.setOnLongClickListener {categoryTobeDelete->

            val categoryEntityToBeDeleted= CategoryEntity(
                categoryTobeDelete.name,
                categoryTobeDelete.isSelected

            )
            deleteCategory(categoryEntityToBeDeleted)

        }
        categoryAdapter.setOnClickListener { selected ->
            if (selected.name == "+") {
                val createCategoryBottomSheet = CreateCategoryBottomSheet { categoryName ->

                    val categoryEntity = CategoryEntity(
                        name = categoryName,
                        isSelected = false
                    )
                    insertCategory(categoryEntity)
                }

                createCategoryBottomSheet.show(supportFragmentManager, "createCategoryBottomSheet")

            } else {
                val categoryTemp = categories.map { item ->
                    when {
                        item.name == selected.name && !item.isSelected -> item.copy(isSelected = true)
                        item.name == selected.name && item.isSelected -> item.copy(isSelected = false)
                        else -> item
                    }
                }

                val taskTemp =
                    if (selected.name != "ALL") {
                        tasks.filter { it.category == selected.name }
                    } else {
                        tasks
                    }
                taskAdapter.submitList(taskTemp)
                categoryAdapter.submitList(categoryTemp)
            }


        }

        rvCategory.adapter = categoryAdapter
        lifecycleScope.launch(Dispatchers.IO) {
            getCategoriesFromDatabase()
        }

        rvTask.adapter = taskAdapter



        lifecycleScope.launch(Dispatchers.IO) {
            getTaskFromDatabase()
        }

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
    private fun getTaskFromDatabase() {

        val taskFromDb: List<TaskEntity> = taskDao.getAll()
        val taskiesUiData: List<TaskUiData> = taskFromDb.map {
            TaskUiData(
                id = it.id,
                name = it.name,
                category = it.category
            )
        }

        lifecycleScope.launch(Dispatchers.Main) {
            tasks = taskiesUiData
            taskAdapter.submitList(taskiesUiData)
        }


    }


    private fun getCategoriesFromDatabase() {
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

        lifecycleScope.launch(Dispatchers.Main) {
            categories = categoriesUiData
            categoryAdapter.submitList(categoriesUiData)
        }


    }

    private fun insertCategory(categoryEntity: CategoryEntity) {

        lifecycleScope.launch(Dispatchers.IO) {
            categoryDao.insert(categoryEntity)
            getCategoriesFromDatabase()
        }

    }

    private fun insertTask(taskEntity: TaskEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            taskDao.insert(taskEntity)
            getTaskFromDatabase()
        }
    }

    private fun updateTask(taskEntity: TaskEntity){
        lifecycleScope.launch(Dispatchers.IO) {
            taskDao.update(taskEntity)
            getTaskFromDatabase()
        }
    }

    private fun deleteTask(taskEntity: TaskEntity){
        lifecycleScope.launch(Dispatchers.IO) {
            taskDao.delete(taskEntity)
            getTaskFromDatabase()
        }
    }

    private fun deleteCategory(categoryEntity: CategoryEntity){
        lifecycleScope.launch(Dispatchers.IO) {
            categoryDao.delete(categoryEntity)
            getCategoriesFromDatabase()
        }
    }

    private fun showCreateUpdateTaskBottomSheet(task: TaskUiData?=null) {
        val createTaskBottomSheet = CreateOrUpdateTaskBottomSheet(
            task = task,
           categoryList =  categories,

            onCreateCliked = {
                    taskToBeCreated ->
                val taskEntityToBeInsert = TaskEntity(
                    name = taskToBeCreated.name,
                    category = taskToBeCreated.category
                )
                insertTask(taskEntityToBeInsert)

            }, onUpdateCliked = {
                taskTobeUpdate->
                val taskEntityToUpdated= TaskEntity(
                    id = taskTobeUpdate.id,
                    name = taskTobeUpdate.name,
                    category = taskTobeUpdate.category
                )
                updateTask(taskEntityToUpdated)
            }, onDeleteCliked = {tasktoDelete->
                val taskEntityToDelete= TaskEntity(
                    id = tasktoDelete.id,
                    name = tasktoDelete.name,
                    category = tasktoDelete.category
                )
                deleteTask(taskEntityToDelete)

            }
        )
        createTaskBottomSheet.show(
            supportFragmentManager,
            "createTaskBottomSheet"
        )
    }

}


