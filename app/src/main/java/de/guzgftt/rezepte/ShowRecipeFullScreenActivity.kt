package de.guzgftt.rezepte

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.show_recipe_photo_fullscreen.*


class ShowRecipeFullScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_recipe_photo_fullscreen)

        val filePath = intent.getSerializableExtra("bitmap") as String

        val bitmap = BitmapFactory.decodeFile(filePath)

        //photoView (zoomable)
        photo_view_fullscreen.setImageBitmap(bitmap)
    }
}