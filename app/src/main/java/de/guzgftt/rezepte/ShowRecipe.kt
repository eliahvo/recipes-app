package de.guzgftt.rezepte

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.show_recipe.*
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.ViewGroup
import android.view.WindowManager
import kotlinx.android.synthetic.main.show_recipe_photo_fullscreen.*

class ShowRecipe : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_recipe)

        val recipe = intent.getSerializableExtra("recipe") as Recipe

        recipe_name.text = recipe.name

        val bitmap = BitmapFactory.decodeFile(recipe.picture)

        //photoView (zoomable)
        photo_view.setImageBitmap(bitmap)

        photo_view.setOnClickListener{
            val intent = Intent(this, ShowRecipeFullScreenActivity::class.java)
            intent.putExtra("bitmap", recipe.picture)
            startActivityForResult(intent, 111)
        }
    }
}