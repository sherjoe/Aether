package com.example.aether

/**
 * Aether - This app was supposed to replicate a basic social media app where
 * users could share their dreams and accompany it with a photo. The photo could be
 * just where they woke up in a sweat or if they have a drawing from their image
 * gallery that they can upload to the feed.
 *
 * 1. Login ~ the user is expected to type hwk4@example.com as the email and abcdef as the password
 * 2. Feed ~ From there the user can either capture a dream or choose a dream
 * capturing a dream will enable the camera while choosing a dream will make them go to the gallery to choose an image
 * 3. ~ The Return - it will take a couple of seconds but they will be directed back to the live feed afterwards
 * 4. ~ Menu ~ The user can filter the dreams if their type specifically has "Nightmare" or "Lucid", or if they want to get
 * rid of those filters
 * 5. ~ Settings ~ The user can change the font size of any of the words that are not part of the feed.
 * In order to not mess with the feed layout cropping words, only the actvities without the feed will obey the text size configuration.
 * They can also switch the theme to dark mode.
 *6. ~ The user can also use the app in Spanish if they wish.
 *
 * Luny, the dream mascot of Aether, oversees everything.
 */

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var db : FirebaseFirestore
    private lateinit var mAuth : FirebaseAuth
    private lateinit var un_field : EditText
    private lateinit var ps_field : EditText
    private lateinit var login : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        un_field = findViewById(R.id.un_field) as EditText
        ps_field = findViewById(R.id.ps_field) as EditText
        login = findViewById(R.id.login) as Button

        val email = un_field.text
        val password = ps_field.text

        login.setOnClickListener()
        {
            mAuth.signInWithEmailAndPassword(email.toString(), password.toString())
                .addOnCompleteListener(
                    this
                ) { task ->

                    if (task.isSuccessful) {

                        val intent = Intent(this@MainActivity, DreamActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@MainActivity, resources.getString(R.string.loginerr), Toast.LENGTH_SHORT).show()
                    }
                }

        }

    }
}