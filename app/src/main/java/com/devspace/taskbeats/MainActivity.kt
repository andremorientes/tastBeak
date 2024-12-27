package com.devspace.taskbeats

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var categories = listOf<CategoryUiData>()
    private var categoriesEntity = listOf<CategoryEntity>()
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


    private lateinit var  rvCategory: RecyclerView
    private lateinit var ctnEmptyView: LinearLayout
    private lateinit var fabCreateTask: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //insertDeafultCategory()
        //insertDefaultTask()

         rvCategory = findViewById(R.id.rv_categories)
        ctnEmptyView= findViewById(R.id.ll_empty_view)
        val rvTask = findViewById<RecyclerView>(R.id.rv_tasks)
         fabCreateTask = findViewById(R.id.fab_create_task)
        val btnCreateEmpty = findViewById<Button>(R.id.btn_createEmpty)



        fabCreateTask.setOnClickListener {
            showCreateUpdateTaskBottomSheet()
        }
        taskAdapter.setOnClicklistner { task ->
            showCreateUpdateTaskBottomSheet(task)
        }

        btnCreateEmpty.setOnClickListener {
            showCreateCategoryBottomSheet()
        }


        categoryAdapter.setOnLongClickListener { categoryTobeDelete ->

            if (categoryTobeDelete.name != "+" && categoryTobeDelete.name != "ALL") {
                val title = this.getString(R.string.category_delete_title)
                val description = this.getString(R.string.category_delete_description)
                val btnInfo = this.getString(R.string.delete)
                showInfoDialog(
                    title,
                    description,
                    btnInfo,
                    onClick = {
                        val categoryEntityToBeDeleted = CategoryEntity(
                            categoryTobeDelete.name,
                            categoryTobeDelete.isSelected

                        )
                        deleteCategory(categoryEntityToBeDeleted)
                    }
                )
            }


        }
        categoryAdapter.setOnClickListener { selected ->
            if (selected.name == "+") {

                showCreateCategoryBottomSheet()

            } else {
                val categoryTemp = categories.map { item ->
                    when {
                        item.name == selected.name && item.isSelected -> item.copy(isSelected = true)
                        item.name == selected.name && !item.isSelected -> item.copy(isSelected = true)
                        item.name != selected.name && item.isSelected -> item.copy(isSelected = false)
                        else -> item
                    }
                }


                if (selected.name != "ALL") {
                    filterTaskByCategoryName(selected.name)
                } else {
                    lifecycleScope.launch(Dispatchers.IO) {
                        getTaskFromDatabase()
                    }

                }

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
        categoriesEntity = categorieFromDb
lifecycleScope.launch (Dispatchers.Main){
    if (categoriesEntity.isEmpty()){
        rvCategory.isVisible=false
        ctnEmptyView.isVisible=true
        fabCreateTask.isVisible=false
    }else{
        rvCategory.isVisible=true
        ctnEmptyView.isVisible=false
        fabCreateTask.isVisible=true
    }
}


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

        val categoryListTemp = mutableListOf(
            CategoryUiData(
                name = "ALL",
                isSelected = true
            )
        )

        categoryListTemp.addAll(categoriesUiData)
        lifecycleScope.launch(Dispatchers.Main) {

            categories = categoryListTemp
            categoryAdapter.submitList(categories)
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

    private fun updateTask(taskEntity: TaskEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            taskDao.update(taskEntity)
            getTaskFromDatabase()
        }
    }

    private fun deleteTask(taskEntity: TaskEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            taskDao.delete(taskEntity)
            getTaskFromDatabase()
        }
    }

    private fun deleteCategory(categoryEntity: CategoryEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            val tasksToBeDeleted = taskDao.getAllByCategoryName(categoryEntity.name)
            taskDao.deleteAll(tasksToBeDeleted)
            categoryDao.delete(categoryEntity)
            getCategoriesFromDatabase()
            getTaskFromDatabase()
        }
    }

    private fun filterTaskByCategoryName(category: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val taskFromDb: List<TaskEntity> = taskDao.getAllByCategoryName(category)
            val taskUiData: List<TaskUiData> = taskFromDb.map {
                TaskUiData(
                    id = it.id,
                    name = it.name,
                    category = it.category
                )
            }

            lifecycleScope.launch(Dispatchers.Main) {

                taskAdapter.submitList(taskUiData)
            }

        }
    }

    private fun showCreateUpdateTaskBottomSheet(task: TaskUiData? = null) {
        val createTaskBottomSheet = CreateOrUpdateTaskBottomSheet(
            task = task,
            categoryList = categoriesEntity,

            onCreateCliked = { taskToBeCreated ->
                val taskEntityToBeInsert = TaskEntity(
                    name = taskToBeCreated.name,
                    category = taskToBeCreated.category
                )
                insertTask(taskEntityToBeInsert)

            }, onUpdateCliked = { taskTobeUpdate ->
                val taskEntityToUpdated = TaskEntity(
                    id = taskTobeUpdate.id,
                    name = taskTobeUpdate.name,
                    category = taskTobeUpdate.category
                )
                updateTask(taskEntityToUpdated)
            }, onDeleteCliked = { tasktoDelete ->
                val taskEntityToDelete = TaskEntity(
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

    private fun showCreateCategoryBottomSheet() {
        val createCategoryBottomSheet = CreateCategoryBottomSheet { categoryName ->

            if (categoryName.isNotEmpty()) {
                val categoryEntity = CategoryEntity(
                    name = categoryName,
                    isSelected = false
                )
                insertCategory(categoryEntity)
            } else {
                Toast.makeText(baseContext, " Por favor Preencha a categoria", Toast.LENGTH_LONG)
                    .show()
            }

        }

        createCategoryBottomSheet.show(supportFragmentManager, "createCategoryBottomSheet")
    }


    private fun showInfoDialog(
        title: String,
        subTitle: String,
        btnInfo: String,
        onClick: () -> Unit
    ) {

        val infoShowBottomDialog = InfoBottomSheet(
            title = title,
            subTitle = subTitle,
            btnInfo = btnInfo,
            onClick
        )

        infoShowBottomDialog.show(
            supportFragmentManager, "InfoBottomSheet"
        )

    }

}


