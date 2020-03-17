package dev.mustaq.clipboard.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DB_NAME, factory, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE $DB_NAME($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_CONTENT TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $DB_NAME")
    }

    fun insertRow(clip: Clip) {
        val values = ContentValues()
        values.put(COLUMN_CONTENT, clip.content)
        val db = this.writableDatabase
        db.insert(DB_NAME, null, values)
        db.close()
    }

    fun getList(): Cursor? {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $DB_NAME", null)
    }

    fun showElementsTest() : String?{
        var string = ""
        val cursor = getList()
        cursor?.moveToFirst()
        string = cursor?.getString(cursor.getColumnIndex(COLUMN_CONTENT)) ?: "null"
        while (cursor!!.moveToNext()) {
            string += cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT))
        }
        return string
    }

    companion object {
        const val DB_NAME = "Clipboard_table"
        const val DB_VERSION = 1
        const val COLUMN_ID = "Id"
        const val COLUMN_CONTENT = "Content"
    }
}
