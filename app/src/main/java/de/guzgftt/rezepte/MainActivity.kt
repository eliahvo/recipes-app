package de.guzgftt.rezepte

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var arrayList = ArrayList<String>()
    private var listAdapter : ArrayAdapter<String>? = null
    private var recipeList = ArrayList<Recipe>()

    private fun loadRecipesFromStorage(){
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES) as File

        if(!storageDir.exists()){
            Log.i("load directory", "load directory not exists")
        }else{
            var allFiles = storageDir.listFiles { dir, name -> name.endsWith(".jpg") }

            if(allFiles.isEmpty()) textViewNoListItems.text = "Tippe auf das + um ein neues Rezept hinzuzufügen"

            Arrays.sort(allFiles)

            for(file in allFiles){
                var lastSlash = 0
                for(i in 0..file.absolutePath.length-1){
                    if(file.absolutePath.get(i) == '/') lastSlash = i
                }

                var name = file.absolutePath.substring(lastSlash+1, file.absolutePath.length-4)

                recipeList.add(Recipe(name, file.absolutePath))
                arrayList.add(name)
            }
            listAdapter!!.notifyDataSetChanged()
        }
    }

    //deletes a recipe
    private fun delete(position: Int) : Boolean{
        arrayList.remove(arrayList[position])
        listAdapter!!.notifyDataSetChanged()

        val fileDel = File(recipeList[position].picture)
        fileDel.delete()

        recipeList.remove(recipeList[position])

        if(arrayList.isEmpty() && recipeList.isEmpty()){
            textViewNoListItems.text = "Tippe auf das + um ein neues Rezept hinzuzufügen"
        }

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //setting up the list
        listAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList)
        list.adapter = listAdapter

        //read existing recipes into lists
        loadRecipesFromStorage()

        //onClickListener for floating action button to add new recipe
        fab.setOnClickListener {
            startActivityForResult(Intent(applicationContext, NewRecipeActivity::class.java), 999)
        }

        //onClickListener for listView items
        list.setOnItemClickListener{ parent, view, position, id ->
            val intent = Intent(this, ShowRecipe::class.java)
            intent.putExtra("recipe", recipeList.get(position))

            startActivity(intent)
        }

        //onLongClickListener for listView items
        list.setOnItemLongClickListener{parent, view, position, id ->
            delete(position)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //adding new recipe to listView
        if(requestCode == 999 && resultCode == Activity.RESULT_OK){
            val bundle : Bundle? = data!!.extras
            val recipe = bundle!!.get("recipe") as Recipe
            recipeList.add(recipe)
            Collections.sort(recipeList, object : Comparator<Recipe> {
                override fun compare(v1: Recipe, v2: Recipe): Int {
                    return v1.name!!.compareTo(v2.name as String)
                }
            })


            arrayList.add(recipe.name as String)
            Collections.sort(arrayList)

            textViewNoListItems.text = ""

            listAdapter!!.notifyDataSetChanged()
        }
    }
}
