package com.projet.nutrition

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class ObjectifActivity : AppCompatActivity() {

    private var min: EditText? = null
    private var max: EditText? = null
    private var actuel: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_objectif)
        min = findViewById<View>(R.id.min) as EditText
        max = findViewById<View>(R.id.max) as EditText
        actuel = findViewById<View>(R.id.txt_actuel) as TextView
        val objectif = getSharedPreferences("objectif", Context.MODE_PRIVATE)
        val min_cal = objectif.getInt("min", 0)
        val max_cal = objectif.getInt("max", 0)
        actuel!!.text = "Min actuel: $min_cal  Max actuel: $max_cal"


    }

    fun Confirmer(view: View) {

        val min_cal = Integer.parseInt(min!!.text.toString())
        val max_cal = Integer.parseInt(max!!.text.toString())

        if (max_cal < min_cal) {
            Toast.makeText(this@ObjectifActivity, "Max Calories doit être supérieur à Min Calories!", Toast.LENGTH_LONG).show()
        } else {

            val objectif = getSharedPreferences("objectif", Context.MODE_PRIVATE)
            val editor = objectif.edit()
            editor.putInt("min", min_cal)
            editor.putInt("max", max_cal)
            editor.commit()
            Toast.makeText(this@ObjectifActivity, "L'objectif a été sauvegardé!", Toast.LENGTH_LONG).show()

        }
    }

}
