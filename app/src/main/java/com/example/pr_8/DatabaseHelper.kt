package com.example.pr_8

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Вспомогательный класс для управления локальной реляционной базой данных SQLite
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Имя локального файла базы данных во внутреннем защищенном хранилище приложения
        private const val DATABASE_NAME = "ContactsDirectory.db"
        // Версия схемы базы данных
        private const val DATABASE_VERSION = 1
        // Наименование таблицы контактов
        const val TABLE_NAME = "contacts_table"
        // Названия столбцов таблицы
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "contact_name"
        const val COLUMN_PHONE = "contact_phone"
    }

    // Метод создания структуры таблиц при первом открытии базы данных
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_PHONE TEXT)"
        db?.execSQL(createTableQuery)
    }

    // Метод обновления структуры таблиц при изменении версии базы данных
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Метод добавления новой записи (контакта) в локальную базу данных
    fun insertContact(name: String, phone: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_PHONE, phone)
        }
        val result = db.insert(TABLE_NAME, null, contentValues)
        // Если результат равен -1, произошла ошибка вставки
        return result != -1L
    }

    // Метод чтения всех сохраненных записей из таблицы базы данных
    fun getAllContacts(): List<Pair<String, String>> {
        val contactList = mutableListOf<Pair<String, String>>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_NAME, $COLUMN_PHONE FROM $TABLE_NAME ORDER BY $COLUMN_ID DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE))
                contactList.add(Pair(name, phone))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return contactList
    }

    // Метод полной очистки таблицы контактов
    fun clearDatabase() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
    }
}
