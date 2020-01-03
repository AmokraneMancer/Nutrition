package com.projet.nutrition

import android.content.ContentResolver
import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class StatistiqueActivity : AppCompatActivity() {

    private var cr: ContentResolver? = null
    private var combinedChart: CombinedChart? = null
    private var sommes: MutableList<Int>? = null
    private var jours: MutableList<String>? = null
    private var dernierJour: String? = null
    private var repas: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistique)
        repas = findViewById(R.id.dernierRepas)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        cr = contentResolver
        //obtenir tous les jours et inverse pour commencer par les derniers jours
        jours = ArrayList()
        run {
            var i = 0
            var j = tousLesjours().size - 1
            while (i < tousLesjours().size) {
                jours!!.add(i, tousLesjours()[j])
                i++
                j--
            }
        }
        //obtenir la sommme de calorie pour chaque jour
        sommes = ArrayList()
        for (i in jours!!.indices) {
            sommes!!.add(sommeDeJour(jours!![i]))
        }

        //le nombre de colone
        var colone = 7
        if (jours!!.size < 7) {
            colone = jours!!.size
        }

        //les attributs de combinedChart
        combinedChart = findViewById(R.id.chart)
        combinedChart!!.setDrawBorders(true) // 显示边界
        combinedChart!!.description.isEnabled = false  // 不显示备注信息
        combinedChart!!.setPinchZoom(true) // 比例缩放
        combinedChart!!.animateY(1500)

        if (jours!!.size > 0) {


            //les attribut de XLabels
            val xAxis = combinedChart!!.xAxis
            xAxis.setDrawGridLines(false)
            xAxis.axisMinimum = -0.5f
            xAxis.axisMaximum = colone - 0.5f

            xAxis.labelCount = colone // 设置X轴标签数量
            xAxis.position = XAxis.XAxisPosition.BOTTOM // 设置X轴标签位置，BOTTOM在底部显示，TOP在顶部显示

            //les derniers jours -> x labels
            val x_labelValues = arrayOfNulls<String>(colone)
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            for (i in 0 until colone) {
                var date = Date()
                try {
                    date = sdf.parse(jours!![i])
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                x_labelValues[colone - 1 - i] = sdf.format(date)
            }

            dernierJour = x_labelValues[x_labelValues.size - 1]
            leDernierRepas(dernierJour)
            //            repas.setText(leDernierRepas(dernierJour));

            val formatter = object : IAxisValueFormatter {

                override fun getFormattedValue(value: Float, axis: AxisBase): String {
                    return x_labelValues[value.toInt()]!!
                }
                override fun getDecimalDigits(): Int {
                    return 0
                }
            }

            xAxis.granularity = 1f
            xAxis.labelRotationAngle = -20f
            xAxis.valueFormatter = formatter

            //Yleft
            val axisLeft = combinedChart!!.axisLeft
            axisLeft.axisMinimum = 0f
            axisLeft.granularity = 10f
            axisLeft.labelCount = 10

            //Yright
            val axisRight = combinedChart!!.axisRight
            axisRight.setDrawGridLines(false)
            axisRight.granularity = 10f
            axisRight.axisMinimum = 0f
            axisRight.labelCount = 20

            //bardata
            val barEntries = ArrayList<BarEntry>()
            for (i in 0 until colone) {
                barEntries.add(BarEntry((colone - 1 - i).toFloat(), java.lang.Float.parseFloat(sommes!![i].toString())))
            }

            //les attributs de bardataset
            val barDataSet = BarDataSet(barEntries, "Calorie")
            barDataSet.setColors(*ColorTemplate.VORDIPLOM_COLORS)
            barDataSet.valueTextColor = Color.parseColor("#0288d1")
            barDataSet.valueTextSize = 13f
            barDataSet.valueTextColor = Color.BLACK
            val barData = BarData()
            barData.addDataSet(barDataSet)

            //obtenir le min et max de l'objectif pour afficher
            val objectif = getSharedPreferences("objectif", Context.MODE_PRIVATE)
            val min = objectif.getInt("min", 0)
            val max = objectif.getInt("max", 0)

            //LineChart de min
            val minEntries = ArrayList<Entry>()
            for (i in 0 until colone) {
                minEntries.add(Entry(i.toFloat(), min.toFloat()))
            }
            val minDataSet = LineDataSet(minEntries, "MIN Calorie")
            minDataSet.color = Color.RED
            minDataSet.valueTextColor = Color.GRAY
            minDataSet.lineWidth = 3f
            minDataSet.setDrawCircles(false)
            minDataSet.valueTextSize = 10f
            minDataSet.isHighlightEnabled = false

            //LineChart de max
            val maxEntries = ArrayList<Entry>()
            for (i in 0 until colone) {
                maxEntries.add(Entry(i.toFloat(), max.toFloat()))
            }


            val maxDataSet = LineDataSet(maxEntries, "MAX Calorie")
            maxDataSet.color = Color.BLUE
            maxDataSet.valueTextColor = Color.GRAY
            maxDataSet.lineWidth = 3f
            maxDataSet.setDrawCircles(false)
            maxDataSet.valueTextSize = 10f
            maxDataSet.isHighlightEnabled = false

            val lineData = LineData()
            lineData.addDataSet(minDataSet)
            lineData.addDataSet(maxDataSet)


            //set tous les donnees pour le combinedChart

            val combinedData = CombinedData()
            combinedData.setData(barData)
            if (min == 0 && max == 0) {

            } else {
                combinedData.setData(lineData)
            }

            combinedChart!!.data = combinedData
        }

    }

    fun sommeDeJour(jour: String): Int {
        val c = cr!!.query(
            Uri.parse("$CP_PATH/repasWithCondition"),
            null,
            "jour = ?",
            arrayOf(jour), null)
        var somme = 0
        while (c!!.moveToNext()) {
            somme += c.getInt(c.getColumnIndex("somme"))
        }
        c.close()
        return somme
    }

    fun leDernierRepas(dernierJour: String?) {
        var calorie: String? = null
        val c = cr!!.query(
            Uri.parse("$CP_PATH/repasWithCondition"), null,
            "jour = ?",
            arrayOf<String>(dernierJour!!), null)
        if (c!!.moveToLast()) {
            calorie = c.getString(c.getColumnIndex("somme"))
        }
        if (calorie == null) {
            repas!!.text = ""
        }
        repas!!.text = "La somme calorique du dernier repas est " + calorie + "Kcal"
    }

    fun tousLesjours(): List<String> {
        val c = cr!!.query(Uri.parse("$CP_PATH/tousLesJours"), null, null, null, null)
        val jours = ArrayList<String>()
        while (c!!.moveToNext()) {
            jours.add(c.getString(c.getColumnIndex("jour")))
        }
        Collections.sort(jours)
        c.close()
        return jours

    }

    companion object {
        private val CP_PATH = "content://com.projet.nutrition.myprovider"
    }

}
