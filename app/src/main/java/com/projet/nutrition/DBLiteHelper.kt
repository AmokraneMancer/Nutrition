package com.projet.nutrition

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.lang.Exception

class DBLiteHelper(private val myContext: Context) : SQLiteOpenHelper(myContext, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(SQL_CREATE_ALIMENT)
        db.execSQL(SQL_CREATE_REPAS)
        //insert quelques données

        importCSV(db)

        db.execSQL("insert into repas_table(jour,somme) values('2017-01-02','435')")
        db.execSQL("insert into repas_table(jour,somme) values('2017-01-02','600')")
        db.execSQL("insert into repas_table(jour,somme) values('2017-01-03','456')")
        db.execSQL("insert into repas_table(jour,somme) values('2017-01-03','1367')")
        db.execSQL("insert into repas_table(jour,somme) values('2017-03-04','1532')")
        db.execSQL("insert into repas_table(jour,somme) values('2017-03-20','2237')")
        db.execSQL("insert into repas_table(jour,somme) values('2017-01-06','2753')")

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    fun importCSV(db: SQLiteDatabase) {


        val `is` = myContext.resources.openRawResource(R.raw.data)
        var isr: InputStreamReader? = null
        try {
            isr = InputStreamReader(`is`, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        var count = -1
        var bufferedReader: BufferedReader? = null
        try {
            bufferedReader = BufferedReader(isr!!)
            var line = ""
            //while ((line = bufferedReader.readLine()) != null) {
            bufferedReader.forEachLine {
                val datas = it.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                //第一次存入的是列名而不是数据，需要排除
                println("datas" + datas.size)
                if (count >= 0) {
                    //重新存入数据库
                    db.execSQL("insert into aliment_table(nom,calorie,lipide,glucide,proteine) values(?,?,?,?,?)", arrayOf(datas[0], datas[1], datas[2], datas[3], datas[4]))

                }
                count++
            }
        } catch (e: Exception) {
            e.printStackTrace()

        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close()
                    isr!!.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

    }

    companion object {

        private val TAG = "info"
        val DB_VERSION = 1
        val DB_NAME = "Nutrition.db"

        /* les attributs */
        //Aliment
        val TABLE_ALIMENT = "aliment_table"
        val _ID = "_id"
        val NOM = "nom"
        val CALORIE = "calorie"
        val LIPIDE = "lipide"
        val GLUCIDE = "glucide"
        val PROTEINE = "proteine"

        //Repas
        val TABLE_REPAS = "repas_table"
        val JOUR = "jour"
        val SOMME = "somme"


        private val SQL_CREATE_ALIMENT = "CREATE TABLE " + TABLE_ALIMENT + " (" +
                _ID + " INTEGER AUTO_INCREMENT," +
                NOM + " VARCHAR(255) NOT NULL PRIMARY KEY, " +
                CALORIE + " VARCHAR(255) NOT NULL, " +
                LIPIDE + " VARCHAR(255) DEFAULT '', " +
                GLUCIDE + " VARCHAR(255) DEFAULT '', " +
                PROTEINE + " VARCHAR(255) DEFAULT '' " +
                " )"

        private val SQL_CREATE_REPAS = "CREATE TABLE " + TABLE_REPAS + " (" +
                _ID + " INTEGER AUTO_INCREMENT," +
                JOUR + " VARCHAR(255), " +
                SOMME + " VARCHAR(255)" +
                " )"
    }
}