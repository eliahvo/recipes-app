package de.guzgftt.rezepte

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main_recipe_list.*
import kotlinx.android.synthetic.main.show_recipe_list.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ShowRecipeList : AppCompatActivity() {

    private var arrayList_recipes = ArrayList<String>()
    private var listAdapter_recipe_list : ArrayAdapter<String>? = null
    private var recipeList = ArrayList<Recipe>()

    private fun loadRecipesFromStorage(path : String){
        val storageDir = File(path)

        if(!storageDir.exists()){
            Log.i("load directory", "load directory not exists")
        }else{
            var allFiles = storageDir.listFiles { dir, name -> name.endsWith(".jpg") }

            if(allFiles.isEmpty()) textViewNoRecipeItems.text = "Tippe auf das + um ein neues Rezept hinzuzufügen"

            Arrays.sort(allFiles)

            for(file in allFiles){
                var lastSlash = 0
                for(i in 0..file.absolutePath.length-1){
                    if(file.absolutePath.get(i) == '/') lastSlash = i
                }

                var name = file.absolutePath.substring(lastSlash+1, file.absolutePath.length-4)

                recipeList.add(Recipe(name, file.absolutePath))
                arrayList_recipes.add(name)
            }
            listAdapter_recipe_list!!.notifyDataSetChanged()
        }
    }

    //deletes a recipe
    private fun delete(position: Int) : Boolean{
        //Delete Dialog
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_delete)
            .setTitle("Löschen?")
            .setMessage("Rezept " + recipeList[position].name + " wirlich löschen?")
            .setPositiveButton("Ja", DialogInterface.OnClickListener(){ dialog, which ->
                arrayList_recipes.remove(arrayList_recipes[position])
                listAdapter_recipe_list!!.notifyDataSetChanged()

                val fileDel = File(recipeList[position].picture)
                fileDel.delete()

                recipeList.remove(recipeList[position])

                if(arrayList_recipes.isEmpty() && recipeList.isEmpty()){
                    textViewNoRecipeItems.text = "Tippe auf das + um ein neues Rezept hinzuzufügen"
                }
            })
            .setNegativeButton("Nein", null)
            .show()

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_recipe_list)

        val genre = intent.getSerializableExtra("genre") as Genre

        //setting up the list
        listAdapter_recipe_list = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList_recipes)
        listRecipes.adapter = listAdapter_recipe_list

        //read existing recipes into lists
        loadRecipesFromStorage(genre.path as String)

        //onClickListener for floating action button to add new recipe
        fab_recipe_list.setOnClickListener {
            val intent = Intent(this, NewRecipeActivity::class.java) //this vorher applicationcontex
            intent.putExtra("path", genre.path)
            startActivityForResult(intent, 999)
        }

        //onClickListener for listView items
        listRecipes.setOnItemClickListener{ parent, view, position, id ->
            val intent = Intent(this, ShowRecipe::class.java)
            intent.putExtra("recipe", recipeList.get(position))

            startActivity(intent)
        }

        //onLongClickListener for listView items
        listRecipes.setOnItemLongClickListener{parent, view, position, id ->
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


            arrayList_recipes.add(recipe.name as String)
            Collections.sort(arrayList_recipes)

            textViewNoRecipeItems.text = ""

            listAdapter_recipe_list!!.notifyDataSetChanged()
        }
    }
}
