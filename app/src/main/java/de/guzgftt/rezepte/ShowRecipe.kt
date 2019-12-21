package de.guzgftt.rezepte

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.show_recipe.*
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.new_recipe.*
import kotlinx.android.synthetic.main.show_recipe_photo_fullscreen.*
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ShowRecipe : AppCompatActivity() {

    private var mealFilePath = ""
    private var mealDirectoryPath = ""
    private var recipe : Recipe? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_recipe)

        recipe = intent.getSerializableExtra("recipe") as Recipe

        //set title to recipename
        supportActionBar!!.title = recipe!!.name


        //check for meal image in path
        mealFilePath = recipe!!.picture!!.substring(0, recipe!!.picture!!.length - (recipe!!.name!!.length + 5)) + "/mealPhotos/" + recipe!!.name + "_mealPic.jpg"
        mealDirectoryPath = recipe!!.picture!!.substring(0, recipe!!.picture!!.length - (recipe!!.name!!.length + 5)) //+5 for '.jpg' and '/'

        if(!File(mealFilePath).exists()) textView_NoMealPhoto.text = "Kein Foto vom Gericht gefunden!"
        else imageViewMeal.setImageBitmap(BitmapFactory.decodeFile(mealFilePath))


        //bitmap of recipe
        val bitmap = BitmapFactory.decodeFile(recipe!!.picture)

        //photoView
        photo_view.setImageBitmap(bitmap)

        photo_view.setOnClickListener{
            val intent = Intent(this, ShowRecipeFullScreenActivity::class.java)
            intent.putExtra("recipeName", recipe!!.name)
            intent.putExtra("bitmap", recipe!!.picture)
            startActivityForResult(intent, 111)
        }
    }


    private fun dispatchTakePictureIntent(file : String) {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = File(file)

                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "de.guzgftt.rezepte.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                    startActivityForResult(takePictureIntent, 1)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            imageViewMeal.setImageBitmap(BitmapFactory.decodeFile(mealFilePath))
            textView_NoMealPhoto.text = ""
        }
    }





    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_show_recipe, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.item_newMealPhoto){
            dispatchTakePictureIntent(mealFilePath)
        }

        return super.onOptionsItemSelected(item)
    }
}