package com.projet.nutrition

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import java.lang.UnsupportedOperationException

class MyProvider : ContentProvider() {

    private var helper: DBLiteHelper? = null

    override fun getType(uri: Uri): String? {
        //        switch (mMatcher.match(uri)) {
        //            case Constant.ITEM:
        //                return Constant.CONTENT_TYPE;
        //            case Constant.ITEM_ID:
        //                return Constant.CONTENT_ITEM_TYPE;
        //            default:
        //                throw new IllegalArgumentException("Unknown URI"+uri);
        //        }
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        // TODO Auto-generated method stub
        val db = helper!!.writableDatabase
        val code = matcher.match(uri)
        Log.d(LOG, "Uri=$uri")
        var id: Long = 0
        val path: String
        when (code) {
            ALIMENT -> {
                id = db.insert("aliment_table", null, values)
                path = "aliment_table"
            }
            REPAS -> {
                id = db.insert("repas_table", null, values)
                path = "repas_table"
            }
            else -> throw UnsupportedOperationException("this insert not yet implemented")
        }
        val builder = Uri.Builder()
            .authority(AUTOHORITY)
            .appendPath(path)

        return ContentUris.appendId(builder, id).build()

    }

    override fun onCreate(): Boolean {
        // TODO Auto-generated method stub
        helper = DBLiteHelper(context!!)

        return true

    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        // TODO Auto-generated method stub
        val db = helper!!.readableDatabase
        val code = matcher.match(uri)
        val cursor: Cursor
        when (code) {
            ALIMENT ->

                //                cursor = db.query("aliment_table", projection, selection,
                //                        selectionArgs, null, null, sortOrder);
                cursor = db.rawQuery("select * from aliment_table", null)
            REPAS -> cursor = db.query("repas_table", projection, selection,
                selectionArgs, null, null, sortOrder)
            ALIMENT_CONDITION -> {
                var aliment_sql = "select * from aliment_table"
                if (selection != null) {
                    aliment_sql += " where $selection"
                }
                cursor = db.rawQuery(aliment_sql, selectionArgs)
            }
            REPAS_CONDITION -> {
                var repas_sql = "select * from repas_table"
                if (selection != null) {
                    repas_sql += " where $selection"
                }
                cursor = db.rawQuery(repas_sql, selectionArgs)
            }
            TOUS_LES_JOURS -> {
                val jours_sql = "select distinct jour from repas_table"
                cursor = db.rawQuery(jours_sql, null)
            }
            else -> {
                Log.d("Uri provider =", uri.toString())
                throw UnsupportedOperationException("this query is not yet implemented  $uri")
            }
        }
        return cursor
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        // TODO Auto-generated method stub
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        // TODO Auto-generated method stub
        return 0
    }

    companion object {

        private val LOG = "MyProvider"

        val AUTOHORITY = "com.projet.nutrition.myprovider"
        private val ALIMENT = 1
        private val REPAS = 2
        private val ALIMENT_CONDITION = 3
        private val REPAS_CONDITION = 4
        private val TOUS_LES_JOURS = 5

        private val matcher: UriMatcher

        init {
            matcher = UriMatcher(UriMatcher.NO_MATCH)
            matcher.addURI(AUTOHORITY, "aliment_table", ALIMENT)
            matcher.addURI(AUTOHORITY, "repas_table", REPAS)
            matcher.addURI(AUTOHORITY, "alimentWithCondition", ALIMENT_CONDITION)
            matcher.addURI(AUTOHORITY, "repasWithCondition", REPAS_CONDITION)
            matcher.addURI(AUTOHORITY, "tousLesJours", TOUS_LES_JOURS)
        }
    }

}