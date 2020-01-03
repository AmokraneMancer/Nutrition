package com.projet.nutrition

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import java.util.ArrayList

class AlimentActivity : AppCompatActivity() {

    private var cr: ContentResolver? = null
    private var nom: EditText? = null
    private var calorie: EditText? = null
    private var lipide: EditText? = null
    private var glucide: EditText? = null
    private var proteine: EditText? = null
    private var list: ArrayList<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aliment)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        nom = findViewById<View>(R.id.nom) as EditText
        calorie = findViewById<View>(R.id.calorie) as EditText
        lipide = findViewById<View>(R.id.lipide) as EditText
        glucide = findViewById<View>(R.id.glucide) as EditText
        proteine = findViewById<View>(R.id.proteine) as EditText
        cr = contentResolver
    }

    fun Confirmer(view: View) {

        if (TextUtils.isEmpty(nom!!.text)) {
            Toast.makeText(this@AlimentActivity, "Nom ne peut pas être vide!", Toast.LENGTH_LONG).show()
        }

        if (TextUtils.isEmpty(calorie!!.text)) {
            Toast.makeText(this@AlimentActivity, "Calorie ne peut pas être vide!", Toast.LENGTH_LONG).show()
        }

        if (!TextUtils.isEmpty(nom!!.text) && !TextUtils.isEmpty(calorie!!.text)) {

            list = ArrayList()
            val cursor = cr!!.query(
                Uri.parse("$CP_PATH/aliment_table"),
                null, null, null, null)
            while (cursor!!.moveToNext()) {
                list!!.add(cursor.getString(cursor.getColumnIndex("nom")))
            }

            if (list!!.contains(nom!!.text.toString())) {

                Toast.makeText(this@AlimentActivity, nom!!.text.toString() + " a déjà existe!", Toast.LENGTH_LONG).show()
            } else
                insertAliment()

        }

    }

    fun insertAliment() {
        /*cr.insert(Uri.parse(CP_PATH),
				new ContentValues());*/

        val values = ContentValues()
        values.put("nom", nom!!.text.toString())
        values.put("calorie", Integer.parseInt(calorie!!.text.toString()))
        values.put("lipide", lipide!!.text.toString())
        values.put("glucide", glucide!!.text.toString())
        values.put("proteine", proteine!!.text.toString())

        val uri = cr!!.insert(Uri.parse("$CP_PATH/aliment_table"), values)
        val id = ContentUris.parseId(uri)
        //        Toast.makeText(this, "The id of the new insert record:" + id, Toast.LENGTH_LONG).show();
        Toast.makeText(this, nom!!.text.toString() + " a été ajouté!", Toast.LENGTH_LONG).show()
        val intent = Intent(this@AlimentActivity, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // land do nothing is ok
        } else if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // port do nothing is ok
        }
    }

    companion object {
        private val CP_PATH = "content://com.projet.nutrition.myprovider"
    }


}
