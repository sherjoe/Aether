package com.example.aether

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddImageActivity : AppCompatActivity() ,  SharedPreferences.OnSharedPreferenceChangeListener{
    private lateinit var user_add2: EditText
    private lateinit var desc_add2: EditText
    private lateinit var type_add2: EditText
    private lateinit var time_add2: EditText
    private lateinit var entun_TV2: TextView
    private lateinit var wh_TV2: TextView
    private lateinit var wt_TV2: TextView
    private lateinit var whn_TV2: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var i_add2: Button
    lateinit var clickImageId2: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val themeBoolean = sharedPreferences.getBoolean("theme_switch_preference", false)

        if (themeBoolean) {
            setTheme(R.style.AppTheme_Dark)
        } else {
            setTheme(R.style.AppTheme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_image)

        entun_TV2 = findViewById(R.id.entun_TV2) as TextView
        wh_TV2 = findViewById(R.id.wh_TV2) as TextView
        wt_TV2 = findViewById(R.id.wt_TV2) as TextView
        whn_TV2 = findViewById(R.id.whn_TV2) as TextView


        user_add2 = findViewById(R.id.user_add2) as EditText
        desc_add2 = findViewById(R.id.desc_add2) as EditText
        type_add2 = findViewById(R.id.type_add2) as EditText
        time_add2 = findViewById(R.id.time_add2) as EditText
        i_add2 = findViewById(R.id.i_add2) as Button
        clickImageId2 = findViewById(R.id.clickImageId2) as ImageView
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val textSize = sharedPreferences.getString("text_size_preference", "16")!!.toFloat()

        entun_TV2.textSize = textSize
        wh_TV2.textSize = textSize
        wt_TV2.textSize = textSize
        whn_TV2.textSize = textSize
        user_add2.textSize = textSize
        desc_add2.textSize = textSize
        type_add2.textSize = textSize
        time_add2.textSize = textSize


        val storageRef = FirebaseStorage.getInstance().reference
        val firestore = FirebaseFirestore.getInstance()
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")

                    uploadImageToFirestore(uri)

            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        i_add2.setOnClickListener()
        {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }



        }



    private fun uploadImageToFirestore(imageUri: Uri) {
        // Upload the image to Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}")
        val uploadTask = imageRef.putFile(imageUri)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            // Retrieve the download URL of the uploaded image
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result.toString()
                // Store a reference to the image in a Firestore document
                val firestore = FirebaseFirestore.getInstance()
                val docRef = firestore.collection("Hwk4Aether").document()
                val udata = user_add2.getText().toString()
                val descdata = desc_add2.getText().toString()
                val typedata = type_add2.getText().toString()
                val timedata = time_add2.getText().toString()
                val image = hashMapOf(
                    "Username" to udata,
                    "Dream Desc" to descdata,
                    "Dream Type" to typedata,
                    "Date and Time" to timedata,
                    "url" to downloadUri
                )
                docRef.set(image)
                    .addOnSuccessListener {
                        Toast.makeText(this, resources.getString(R.string.success), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, resources.getString(R.string.failure), Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, resources.getString(R.string.failure), Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onSharedPreferenceChanged(sp: SharedPreferences, key: String) {
// Code that should run when a preference is updated
// Get the shared preference key value and update the app with it
        when (key) {
            "text_size_preference" -> {
                // Retrieve the updated value of the preference key

                val ts = sharedPreferences.getString(key, "7")
                val test = ts?.toFloat()

                // Update the text size of the TextView with ID "latestposts_TV"

                entun_TV2.textSize = test?: 16f
                wh_TV2.textSize = test?: 16f
                wt_TV2.textSize = test?: 16f
                whn_TV2.textSize = test?: 16f
                user_add2.textSize = test?: 16f
                desc_add2.textSize = test?: 16f
                type_add2.textSize = test?: 16f
                time_add2.textSize = test?: 16f



            }
            "theme_switch_preference" -> {
                // Retrieve the updated value of the preference key

                val themeBoolean = sharedPreferences!!.getBoolean(key, false)

                if (themeBoolean == true) {
                    setTheme(R.style.AppTheme_Dark)
                } else {
                    setTheme(R.style.AppTheme_Light)
                }

                recreate()

            }
        }
    }




    companion object {
        private const val pic_id = 123
        private const val PICK_REQUEST = 1889


    }

    }

