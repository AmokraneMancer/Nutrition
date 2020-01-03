package com.projet.nutrition

import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout

import com.google.android.material.navigation.NavigationView
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.ArrayList


class MainActivity : AppCompatActivity() {

    private var mDrawerLayout: DrawerLayout? = null
    private var mToggle: ActionBarDrawerToggle? = null
    private var navigationView: NavigationView? = null

    private var cr: ContentResolver? = null
    private var c: Cursor? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cr = contentResolver

        mDrawerLayout = findViewById(R.id.drawer)
        navigationView = findViewById(R.id.nav_view)
        mToggle = ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close)
        mDrawerLayout!!.addDrawerListener(mToggle!!)
        mToggle!!.syncState()
        navigationView!!.setNavigationItemSelectedListener { item ->
            val intent: Intent
            val itemId = item.itemId
            when (itemId) {
                R.id.aliment -> {
                    intent = Intent(this@MainActivity, AlimentActivity::class.java)
                    startActivity(intent)
                }
                R.id.repas -> {
                    intent = Intent(this@MainActivity, RepasActivity::class.java)
                    startActivity(intent)
                }
                R.id.objectif -> {
                    intent = Intent(this@MainActivity, ObjectifActivity::class.java)
                    startActivity(intent)
                }
                R.id.statistique -> {
                    intent = Intent(this@MainActivity, StatistiqueActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (mToggle!!.onOptionsItemSelected(item)) {
            return true
        }
        if (item.itemId == R.id.download) {
            ExportToCSV(touslesAliments(), "Aliments")
            Toast.makeText(this@MainActivity, "Le fichier a été téléchargé, merci de vérifier dans le /sdcard/exportData!", Toast.LENGTH_LONG).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }


    fun ExportToCSV(record: List<String>, fileName: String) {

        val file = File(Environment.getExternalStorageDirectory().toString() + "/exportData/" + fileName + ".csv")
        if (!file.exists()) {
            val mkdir = file.parentFile.mkdirs()
            Log.d(TAG, "CSVUtil exportCSV mkdir: $mkdir")
        } else {
            val delete = file.delete()
            Log.d(TAG, "CSVUtil exportCSV delete: $delete")
        }
        var bufferedWriter: BufferedWriter? =
            null
        try {
            bufferedWriter = BufferedWriter(FileWriter(file, true))
            bufferedWriter.append("Nom,Calorie,Lipide,Glucide,Proteine")
            bufferedWriter.newLine()
            for (r in record) {
                bufferedWriter.append(r)
                bufferedWriter.newLine()
            }
            bufferedWriter.flush()

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                bufferedWriter?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

    fun touslesAliments(): List<String> {

        c = cr!!.query(Uri.parse("$CP_PATH/aliment_table"), null, null, null, null)
        val aliments = ArrayList<String>()
        while (c!!.moveToNext()) {
            aliments.add(c!!.getString(1) + ','.toString() + c!!.getString(2) + ','.toString() + c!!.getString(3) + ','.toString() + c!!.getString(4) + ','.toString() + c!!.getString(5))
        }
        //        Collections.sort(aliments);
        c!!.close()
        return aliments

    }

    companion object {
        private val TAG = "info"
        private val CP_PATH = "content://com.projet.nutrition.myprovider"
    }
}
