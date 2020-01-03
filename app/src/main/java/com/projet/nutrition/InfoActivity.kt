package com.projet.nutrition

import android.content.ContentResolver
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import java.util.ArrayList

class InfoActivity : AppCompatActivity() {

    private var cr: ContentResolver? = null
    private var sp_quantite: Spinner? = null
    private var nom: TextView? = null
    private var calorie: TextView? = null
    private var lipide: TextView? = null
    private var glucide: TextView? = null
    private var proteine: TextView? = null
    private var quantite: TextView? = null
    private var button: Button? = null
    private var number: Int = 0
    private var adapter: ArrayAdapter<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        button = findViewById<View>(R.id.button) as Button
        nom = findViewById<View>(R.id.nom) as TextView
        calorie = findViewById<View>(R.id.calorie) as TextView
        lipide = findViewById<View>(R.id.lipide) as TextView
        glucide = findViewById<View>(R.id.glucide) as TextView
        proteine = findViewById<View>(R.id.proteine) as TextView
        quantite = findViewById<View>(R.id.quantite) as TextView

        sp_quantite = findViewById<View>(R.id.sp_quantite) as Spinner
        val intent1 = intent
        cr = contentResolver
        val c = cr!!.query(
            Uri.parse("$CP_PATH/alimentWithCondition"),
            null,
            "nom = ?",
            arrayOf(intent1.getStringExtra("nom")), null)
        while (c!!.moveToNext()) {
            nom!!.text = c.getString(c.getColumnIndex("nom"))
            calorie!!.text = c.getString(c.getColumnIndex("calorie"))
            lipide!!.text = c.getString(c.getColumnIndex("lipide"))
            glucide!!.text = c.getString(c.getColumnIndex("glucide"))
            proteine!!.text = c.getString(c.getColumnIndex("proteine"))
        }
        c.close()

        //le spinner de quantite
        val list = ArrayList<String>()
        for (i in 0..9) {
            list.add((i + 1).toString())
        }
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        adapter!!.setDropDownViewResource(android.R.layout.select_dialog_item)
        sp_quantite!!.adapter = adapter

        button!!.setOnClickListener {
            val intent2 = Intent()
            intent2.putExtra("nom", nom!!.text.toString())
            intent2.putExtra("calorie", calorie!!.text.toString())
            intent2.putExtra("quantite", number)
            this@InfoActivity.setResult(RESULT_OK, intent2)
            this@InfoActivity.finish()
        }

        sp_quantite!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                number = Integer.parseInt(adapter!!.getItem(position)!!.toString())

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

                number = 1
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // TODO Auto-generated method stub
        if (item.itemId == android.R.id.home) {

            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
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
        val RESULT_OK = 100
        private val CP_PATH = "content://com.projet.nutrition.myprovider"
    }
}
