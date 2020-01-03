package com.projet.nutrition

import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator

class RepasActivity : AppCompatActivity() {

    private var cr: ContentResolver? = null
    private var txt_calorie: TextView? = null
    private var txt_date: TextView? = null
    private var txt_list: TextView? = null
    private var spinner: Spinner? = null
    private var sp_list: ArrayList<String>? = null
    private var sp_adapter: ArrayAdapter<String>? = null
    private var lv: ListView? = null
    private var lv_list: MutableList<Map<String, Any>>? = null
    private var lv_adapter: SimpleAdapter? = null
    private var calorie_total: Int = 0
    private var calendar: Calendar? = null
    private var btn_confirmer: Button? = null
    private var comparator: java.util.Comparator<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repas)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        comparator = Comparator { o1, o2 ->
            //默认o1大于o2
            if (o1 == "Ajouter un aliment dans un repas") {
                return@Comparator -1//negative o1->o2
            }
            if (o2 == "Ajouter un aliment dans un repas") {
                1//positive o2->o1
            } else o1.compareTo(o2)
//normale
        }

        //spinner
        spinner = findViewById<View>(R.id.spinner) as Spinner


        cr = contentResolver

        //remplir le spinner
        sp_list = ArrayList()
        sp_list!!.add(0, "Ajouter un aliment dans un repas")
        val cursor = cr!!.query(
            Uri.parse("$CP_PATH/aliment_table"),
            null, null, null, null)
        while (cursor!!.moveToNext()) {
            sp_list!!.add(cursor.getString(cursor.getColumnIndex("nom")))
        }
        cursor.close()
        Collections.sort(sp_list!!, comparator)
        //        sp_list = Sort(sp_list);

        txt_calorie = findViewById<View>(R.id.txt_calorie) as TextView
        txt_list = findViewById<View>(R.id.txt_list) as TextView

        //enregistrer les aliments dans un repas
        btn_confirmer = findViewById<View>(R.id.btn_confirmer) as Button

        //listview pour les aliments choisis
        lv = findViewById<View>(R.id.lv) as ListView//得到ListView对象的引用 /*为ListView设置Adapter来绑定数据*/
        lv_list = ArrayList()
        lv_adapter = SimpleAdapter(this, lv_list,
            R.layout.list_item, arrayOf("first", "second", "third"),
            intArrayOf(R.id.first, R.id.second, R.id.third))


        lv!!.onItemLongClickListener = AdapterView.OnItemLongClickListener { arg0, arg1, pos, id ->
            // TODO Auto-generated method stub
            if (pos > 0) {

                val nom = lv_list!![pos]["first"].toString()
                val calorie = lv_list!![pos]["second"].toString()
                val quantite = lv_list!![pos]["third"].toString()
                calorie_total -= Integer.parseInt(calorie) * Integer.parseInt(quantite)
                txt_calorie!!.text = "Somme de calorie : $calorie_total"
                ajouterAlimentSpinner(nom)

                lv_list!!.removeAt(pos)
                lv!!.adapter = lv_adapter

            }

            true
        }
        //choisir le date
        txt_date = findViewById<View>(R.id.txt_date) as TextView

        //transfert le type de donnee de jour à string pour le stocker dans la base de donnees
        val df = SimpleDateFormat("yyyy-MM-dd")
        //aujourd'hui
        calendar = Calendar.getInstance(Locale.FRANCE)
        val str = df.format(calendar!!.time)
        txt_date!!.text = str


    }

    override fun onStart() {
        super.onStart()
        sp_adapter = ArrayAdapter(this, R.layout.spinner_list_item, sp_list!!)
        //style de spinner
        sp_adapter!!.setDropDownViewResource(R.layout.dropdown_list_item)
        spinner!!.adapter = sp_adapter

        //set listener de spinner
        spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (id != 0L) {
                    val intent = Intent()
                    intent.putExtra("nom", sp_adapter!!.getItem(position)!!.toString())
                    intent.setClass(this@RepasActivity, InfoActivity::class.java!!)
                    // pour obtenir les données de infoActivity
                    startActivityForResult(intent, 1)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        lv!!.adapter = lv_adapter

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            RESULT_OK -> {
                val nom = data!!.getStringExtra("nom")
                val calories = data!!.getStringExtra("calorie")
                val quantite = data!!.getIntExtra("quantite", 1)

                supprimerAlimentSpinner(nom)

                if (lv_list!!.size == 0) {
                    val map = HashMap<String, Any>()
                    map["first"] = "Nom"
                    map["second"] = "Calorie"
                    map["third"] = "Quantite"
                    lv_list!!.add(0, map)
                }

                val map = HashMap<String, Any>()
                map["first"] = nom
                map["second"] = calories
                map["third"] = quantite
                lv_list!!.add(map)
                if (lv_list!!.size == 0) {
                    calorie_total = 0
                } else {
                    calorie_total += java.lang.Float.parseFloat(calories.toString()).toInt() * quantite
                }
            }

            else -> {
            }
        }

        if (lv_list!!.size != 0) {
            txt_calorie!!.text = "Somme de calorie : $calorie_total"
            txt_list!!.text = "Les aliments choisis : "
            btn_confirmer!!.visibility = View.VISIBLE
        }
        lv_adapter!!.notifyDataSetChanged()
    }

    fun date(view: View) {

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val y: String
            val m: String
            val d: String
            y = year.toString()
            //de 1 -> 01
            if (month < 9) {
                m = "0" + (month + 1)
            } else {
                m = (month + 1).toString()
            }

            if (dayOfMonth < 10) {
                d = "0$dayOfMonth"
            } else {
                d = dayOfMonth.toString()
            }

            txt_date!!.text = "$y-$m-$d"
        }

        val mCalendar = Calendar.getInstance(Locale.FRANCE)
        val year = mCalendar.get(Calendar.YEAR)
        val month = mCalendar.get(Calendar.MONTH)
        val day = mCalendar.get(Calendar.DAY_OF_MONTH)
        val dialog = DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT,
            dateSetListener, year, month, day)
        dialog.show()

    }

    fun Confirmer(view: View) {

        val jour = txt_date!!.text.toString()
        //2017-12-02

        if (calorie_total == 0) {
            Toast.makeText(this@RepasActivity, "Il n'y pas de aliments dans ce repas!", Toast.LENGTH_LONG).show()
        } else {
            val values = ContentValues()
            values.put("jour", jour)
            values.put("somme", calorie_total)

            val uri = cr!!.insert(Uri.parse("$CP_PATH/repas_table"), values)
            val id = ContentUris.parseId(uri)
            Toast.makeText(this, "The id of the new insert record:$id", Toast.LENGTH_LONG).show()

        }

        val intent = Intent(this@RepasActivity, MainActivity::class.java)
        startActivity(intent)
    }

    fun ajouterAlimentSpinner(nom: String) {

        sp_list!!.add(nom)
        Collections.sort(sp_list!!, comparator)
        sp_adapter!!.notifyDataSetChanged()
    }

    fun supprimerAlimentSpinner(nom: String) {

        sp_adapter!!.notifyDataSetChanged()

        for (i in 0 until sp_list!!.size - 1) {
            if (sp_list!![i] == nom) {
                sp_list!!.removeAt(i)
                Collections.sort(sp_list!!, comparator)
                //                Toast.makeText(RepasActivity.this, "index" + i, Toast.LENGTH_LONG).show();
                sp_adapter!!.notifyDataSetChanged()

            }
        }
    }

    fun Sort(list: ArrayList<String>): ArrayList<String> {


        val newList = ArrayList<String>()
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            val s = iterator.next()
            if (s == "Ajouter un aliment dans un repas") {
                iterator.remove()
            }
        }
        Collections.sort(list)
        newList.add(0, "Ajouter un aliment dans un repas")
        for (i in 1 until list.size + 1) {
            newList.add(i, list[i - 1])
        }
        Toast.makeText(this@RepasActivity, list.size.toString() + "-" + newList.size + "-" + newList[0] + "-" + newList[1] + "-" + newList[2] + "-" + newList[3] + "-" + newList[4], Toast.LENGTH_LONG).show()
        return newList
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            txt_date!!.textSize = 25f
            var lp = txt_date!!.layoutParams as LinearLayout.LayoutParams
            lp.topMargin = 10
            lp = spinner!!.layoutParams as LinearLayout.LayoutParams
            lp.topMargin = 10
            lp = txt_list!!.layoutParams as LinearLayout.LayoutParams
            lp.topMargin = 10


        } else if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // port do nothing is ok
            txt_date!!.textSize = 35f
            var lp = txt_date!!.layoutParams as LinearLayout.LayoutParams
            lp.topMargin = 20
            lp = spinner!!.layoutParams as LinearLayout.LayoutParams
            lp.topMargin = 30
            lp = txt_list!!.layoutParams as LinearLayout.LayoutParams
            lp.topMargin = 20
        }
    }

    companion object {
        val RESULT_OK = 100
        private val CP_PATH = "content://com.projet.nutrition.myprovider"
    }

}
