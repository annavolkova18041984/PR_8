package com.example.pr_8

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

// Основной экран приложения ПР_8, реализующий работу с базой данных SQLite и мультиязычностью
class MainActivity : AppCompatActivity() {

    // Ссылка на экземпляр вспомогательного класса локальной базы данных SQLite
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация объекта помощника базы данных
        dbHelper = DatabaseHelper(this)

        // Привязка визуальных компонентов интерфейса
        val etName: EditText = findViewById(R.id.etName)
        val etPhone: EditText = findViewById(R.id.etPhone)
        val btnSave: Button = findViewById(R.id.btnSave)
        val btnView: Button = findViewById(R.id.btnView)
        val btnClear: Button = findViewById(R.id.btnClear)
        val btnStudent: Button = findViewById(R.id.btnStudent)
        val tvSavedData: TextView = findViewById(R.id.tvSavedData)

        // Обработка кнопки добавления нового контакта в БД
        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            // Валидация полей ввода с получением локализованной строки ошибки
            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_empty), Toast.LENGTH_SHORT).show()
            } else {
                val isInserted = dbHelper.insertContact(name, phone)
                if (isInserted) {
                    Toast.makeText(this, getString(R.string.toast_saved), Toast.LENGTH_SHORT).show()
                    etName.text.clear()
                    etPhone.text.clear()
                    displayContacts(tvSavedData)
                } else {
                    Toast.makeText(this, getString(R.string.toast_error), Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Обработка кнопки отображения всех сохраненных контактов
        btnView.setOnClickListener {
            displayContacts(tvSavedData)
        }

        // Обработка кнопки очистки локальной базы данных
        btnClear.setOnClickListener {
            dbHelper.clearDatabase()
            tvSavedData.text = getString(R.string.empty_db)
            Toast.makeText(this, getString(R.string.toast_cleared), Toast.LENGTH_SHORT).show()
        }

        // Обработка кнопки информации о студенте
        btnStudent.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.btn_student))
            builder.setMessage(getString(R.string.student_info))
            builder.setPositiveButton("ОК") { dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
        }

        // Автоматическая загрузка сохраненных контактов при запуске
        displayContacts(tvSavedData)
    }

    // Вспомогательный метод формирования списка контактов для отображения на экране
    private fun displayContacts(textView: TextView) {
        val contacts = dbHelper.getAllContacts()
        if (contacts.isEmpty()) {
            textView.text = getString(R.string.empty_db)
        } else {
            val stringBuilder = StringBuilder()
            contacts.forEachIndexed { index, pair ->
                stringBuilder.append("${index + 1}. ${pair.first} — ${pair.second}\n")
            }
            textView.text = stringBuilder.toString()
        }
    }
}
