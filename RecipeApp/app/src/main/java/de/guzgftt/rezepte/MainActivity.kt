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
import java.io.File
import java.io.FilenameFilter
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var arrayList = ArrayList<String>()
    private var listAdapter : ArrayAdapter<String>? = null
    private var genreList = ArrayList<Genre>()

    private fun loadGenresFromStorage(){
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES) as File

        if(!storageDir.exists()){
            Log.i("load directory", "load directory not exists")
        }else{
            var allFiles = storageDir.list(object : FilenameFilter {
                override fun accept(current: File, name: String): Boolean {
                    return File(current, name).isDirectory
                }
            })

            if(allFiles.isEmpty()) textViewNoListItems.text = "Tippe auf das + um eine neue Kategorie hinzuzufügen"

            for(i in 0..allFiles.size-1){
                allFiles[i] = storageDir.absolutePath + "/" + allFiles[i]
            }

            Arrays.sort(allFiles)

            for(file in allFiles){
                var lastSlash = 0
                for(i in 0..file.length-1){
                    if(file.get(i) == '/') lastSlash = i
                }

                var name = file.substring(lastSlash+1, file.length)

                genreList.add(Genre(name, file))
                arrayList.add(name)
            }
            listAdapter!!.notifyDataSetChanged()
        }
    }

    //deletes a genre
    private fun delete(position: Int) : Boolean{
        //Delete Dialog
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_delete)
            .setTitle("Löschen?")
            .setMessage("Genre " + genreList[position].name + " wirlich löschen?")
            .setPositiveButton("Ja", DialogInterface.OnClickListener(){ dialog, which ->
                arrayList.remove(arrayList[position])
                listAdapter!!.notifyDataSetChanged()

                val dirPath = genreList[position].path
                var dirDel = File(dirPath)

                val contents = dirDel.listFiles()

                var allDeleted = false;
                if(contents.isEmpty()){ //Genre Directory is empty so it can easily be removed
                    dirDel.delete()
                }else{ //Genre Directory is not empty so it must delete its content before deleting the directory
                    for(file in contents){
                        file.delete()
                        allDeleted = !file.exists()
                    }
                }

                if(!allDeleted) Toast.makeText(this, "Löschen schiefgelaufen!", Toast.LENGTH_SHORT).show()
                else {
                    dirDel.delete()

                    genreList.remove(genreList[position])

                    Toast.makeText(this, "Kategorie gelöscht", Toast.LENGTH_SHORT).show()
                }


                if(arrayList.isEmpty() && genreList.isEmpty()){
                    textViewNoListItems.text = "Tippe auf das + um eine neue Kategorie hinzuzufügen"
                }
            })
            .setNegativeButton("Nein", null)
            .show()

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Kategorien"

        //setting up the list
        listAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList)
        list.adapter = listAdapter

        //read existing genres into lists
        loadGenresFromStorage()

        //onClickListener for floating action button to add new genre
        fab.setOnClickListener {
            startActivityForResult(Intent(applicationContext, NewGenreActivity::class.java), 999)
        }

        //onClickListener for listView items
        list.setOnItemClickListener{ parent, view, position, id ->
            val intent = Intent(this, ShowRecipeList::class.java)
            intent.putExtra("genre", genreList.get(position))

            startActivity(intent)
        }

        //onLongClickListener for listView items
        list.setOnItemLongClickListener{parent, view, position, id ->
            delete(position)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //adding new genre to listView
        if(requestCode == 999 && resultCode == Activity.RESULT_OK){
            val bundle : Bundle? = data!!.extras
            val genre = bundle!!.get("genre") as Genre
            genreList.add(genre)
            Collections.sort(genreList, object : Comparator<Genre> {
                override fun compare(v1: Genre, v2: Genre): Int {
                    return v1.name!!.compareTo(v2.name as String)
                }
            })

            arrayList.add(genre.name as String)
            Collections.sort(arrayList)

            textViewNoListItems.text = ""

            listAdapter!!.notifyDataSetChanged()
        }
    }
}
