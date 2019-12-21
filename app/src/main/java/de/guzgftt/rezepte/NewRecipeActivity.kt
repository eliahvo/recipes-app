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
import android.content.pm.ActivityInfo
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
import android.media.ExifInterface
import kotlinx.android.synthetic.main.show_recipe.*
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import kotlinx.android.synthetic.main.new_genre.*
import java.net.URI


class NewRecipeActivity : AppCompatActivity() {

    var currentPhotoPath: String = ""
    var newPhotoPath : String = ""
    var genre : String = ""
    var imageBitmap : Bitmap? = null

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var storageDir = File(genre)//debugging

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            //check if capture attempt happened already and delete it
            if(currentPhotoPath != ""){
                if(File(currentPhotoPath).exists()) File(currentPhotoPath).delete()
            }

            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private val REQUEST_TAKE_PHOTO = 1

    private fun dispatchTakePictureIntent() {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }

                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "de.guzgftt.rezepte.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)

                    //photo nicht voll
                    val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                    new_recipe_image.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun renameFile(){
        val file = File(currentPhotoPath)

        var lastSlash = 0
        for(i in 0..currentPhotoPath.length-1){
            if(currentPhotoPath.get(i) == '/') lastSlash = i
        }

        var path = currentPhotoPath.substring(0, lastSlash)
        newPhotoPath = path + "/" + new_recipe_name.text.toString() + ".jpg"

        try {
            file.renameTo(File(newPhotoPath))
        }catch (e : Exception){
            Toast.makeText(this, "Rename failed", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_recipe)

        val path = intent.getStringExtra("path") as String
        genre = path

        //OnClickListener for taking photo
        btn_take_photo.setOnClickListener{
            dispatchTakePictureIntent()
        }

        //OnClickListener for rotating photo
        btn_rotateImage.isEnabled = false //enable button if photo was taken
        btn_rotateImage.setOnClickListener{
            val matrix = Matrix()
            matrix.setRotate(90f)

            imageBitmap = Bitmap.createBitmap(imageBitmap as Bitmap, 0, 0, imageBitmap!!.width, imageBitmap!!.height, matrix, true)
            new_recipe_image.setImageBitmap(imageBitmap)
        }


        //setOnClickListener for adding recipe to list
        button_add_recipe.setOnClickListener{
            if(new_recipe_name.text.toString() == ""){
                Toast.makeText(this, "Name fehlt", Toast.LENGTH_SHORT).show()
            }else if(!File(currentPhotoPath).exists()){
                Toast.makeText(this, "Foto fehlt", Toast.LENGTH_SHORT).show()
            }else{
                //override picture file
                try {
                    val out = FileOutputStream(currentPhotoPath)
                    imageBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out)
                }catch (e : IOException){
                    e.printStackTrace()
                }

                //check if file already exists
                renameFile()
                val intent = Intent()
                intent.putExtra("recipe", Recipe(new_recipe_name.text.toString(), newPhotoPath))
                setResult(Activity.RESULT_OK, intent)

                finish()
            }
        }
    }

    override fun onBackPressed() {
        if(currentPhotoPath != "") File(currentPhotoPath).delete()
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            imageBitmap = BitmapFactory.decodeFile(currentPhotoPath)
            new_recipe_image.setImageBitmap(imageBitmap)

            btn_rotateImage.isEnabled = true
        }
    }
}