package de.guzgftt.rezepte

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.new_recipe.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import android.R.attr.bitmap
import java.nio.file.Files.exists
import android.content.ContextWrapper
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.BitmapFactory
import android.graphics.Matrix
import kotlinx.android.synthetic.main.show_recipe.*
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import kotlinx.android.synthetic.main.new_genre.*


class NewGenreActivity : AppCompatActivity() {

    private var genreDirectory = ""

    @Throws(IOException::class)
    private fun createDirectory() : String{
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES) as File
        val genreDir = File(storageDir.absolutePath + "/" + new_genre_name.text.toString())

        if(genreDir.exists()){
            Toast.makeText(this, "Kategorie existiert bereits", Toast.LENGTH_SHORT).show()
            return ""
        }

        if(genreDir.mkdir()) Toast.makeText(this, "Kategorie erstellt", Toast.LENGTH_SHORT).show()

        genreDirectory = genreDir.absolutePath
        return genreDir.absolutePath
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_genre)
        supportActionBar!!.title = "Neue Kategorie"

        btn_add_genre.setOnClickListener{
            if(new_genre_name.text.toString() == ""){
                Toast.makeText(this, "Name fehlt", Toast.LENGTH_SHORT).show()
            }else{
                if(createDirectory() != ""){
                    val intent = Intent()
                    intent.putExtra("genre", Genre(new_genre_name.text.toString(), genreDirectory))
                    setResult(Activity.RESULT_OK, intent)

                    finish()
                }
            }
        }
    }
}