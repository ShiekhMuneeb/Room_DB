package com.example.roomdb

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var userRepository: UserRepository

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Initialize UserRepository
        userRepository = UserRepository(MyApplication.database.userDao())

        findViewById<Button>(R.id.button).setOnClickListener {
            displayDialog()
        }


        findViewById<Button>(R.id.button2).setOnClickListener {
            // Example: Retrieve all users and display in TextView
            GlobalScope.launch(Dispatchers.Main) {
                val users = withContext(Dispatchers.IO) {
                    userRepository.getAllUsers()
                }
                displayUsers(users)
            }
        }

        findViewById<Button>(R.id.button3).setOnClickListener {
            deleteUsers()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun deleteUsers() {
        // Example: Delete all users
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                userRepository.deleteAllUsers()
            }
        }
        findViewById<TextView>(R.id.tvinfo).text = ""
    }

    private fun displayUsers(users: List<User>) {
        val stringBuilder = StringBuilder()
        for (user in users) {
            stringBuilder.append("ID: ${user.id}, Name: ${user.name}, Age: ${user.age}\n")
        }
        findViewById<TextView>(R.id.tvinfo).text = stringBuilder.toString()
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun displayDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog)

        val nameEditText = dialog.findViewById<EditText>(R.id.nameEditText)
        val ageEditText = dialog.findViewById<EditText>(R.id.ageEditText)
        val submitButton = dialog.findViewById<Button>(R.id.submitButton)
        val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)

        cancelButton?.setOnClickListener {
            dialog.dismiss()
        }
        submitButton?.setOnClickListener {
            val name = nameEditText?.text.toString().trim()
            val ageStr = ageEditText?.text.toString().trim()

            if (name.isEmpty() || ageStr.isEmpty()) {
                Toast.makeText(this, "Please enter both name and age", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val age = ageStr.toInt()
            dialog.dismiss()
            // Example: Insert user
            val user = User(name = name, age = age)
            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    userRepository.insertUser(user)
                }
            }
        }
        dialog.show()
    }
}
