package com.example.aether

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Camera
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import android.os.Environment
import android.widget.*
import androidx.preference.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*

class AddDreamActivity : AppCompatActivity(),  SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var user_add: EditText
    private lateinit var desc_add: EditText
    private lateinit var type_add: EditText
    private lateinit var time_add: EditText
    private lateinit var entun_TV: TextView
    private lateinit var wh_TV: TextView
    private lateinit var wt_TV: TextView
    private lateinit var whn_TV: TextView
    private lateinit var photo_add: Button
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var clickImageId: ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val themeBoolean = sharedPreferences.getBoolean("theme_switch_preference", false)

        if (themeBoolean) {
            setTheme(R.style.AppTheme_Dark)
        } else {
            setTheme(R.style.AppTheme_Light)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_dream)

        entun_TV = findViewById(R.id.entun_TV) as TextView
        wh_TV = findViewById(R.id.wh_TV) as TextView
        wt_TV = findViewById(R.id.wt_TV) as TextView
        whn_TV = findViewById(R.id.whn_TV) as TextView

        user_add = findViewById(R.id.user_add) as EditText
        desc_add = findViewById(R.id.desc_add) as EditText
        type_add = findViewById(R.id.type_add) as EditText
        time_add = findViewById(R.id.time_add) as EditText
        photo_add = findViewById(R.id.photo_add) as Button
        clickImageId = findViewById(R.id.clickImageId) as ImageView
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val textSize = sharedPreferences.getString("text_size_preference", "16")!!.toFloat()

        entun_TV.textSize = textSize
        wh_TV.textSize = textSize
        wt_TV.textSize = textSize
        whn_TV.textSize = textSize
        user_add.textSize = textSize
        desc_add.textSize = textSize
        type_add.textSize = textSize
        time_add.textSize = textSize


        val storageRef = FirebaseStorage.getInstance().reference
        val firestore = FirebaseFirestore.getInstance()



        photo_add.setOnClickListener()
        {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST)
            } else {
                // Permission already granted, start the camera intent
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, cam_id)
            }


        }



    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, start the camera intent
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, cam_id)
        } else {
            // Permission denied, show a message to the user
            Toast.makeText(this, resources.getString(R.string.failure), Toast.LENGTH_SHORT).show()
        }

    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
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
                val udata = user_add.getText().toString()
                val descdata = desc_add.getText().toString()
                val typedata = type_add.getText().toString()
                val timedata = time_add.getText().toString()
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



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == cam_id && resultCode == Activity.RESULT_OK) {
            // Image captured successfully, process the image
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            // Do something with the imageBitmap
            clickImageId.setImageBitmap(imageBitmap)

            val imageFile = createImageFile()
            val fos = FileOutputStream(imageFile)
            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()

            // Get the URI of the saved image file
            val imageUri = Uri.fromFile(imageFile)

            // Upload the image to Firestore database
            uploadImageToFirestore(imageUri)

        } else {
            // Image capture failed or cancelled, show a message to the user
            Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show()
        }


    }
    override fun onSharedPreferenceChanged(sp: SharedPreferences, key: String) {
// Code that should run when a preference is updated
// Get the shared preference key value and update the app with it
        when (key) {
            "text_size_preference" -> {

                val ts = sharedPreferences.getString(key, "7")
                val test = ts?.toFloat()

                entun_TV.textSize = test?: 16f
                wh_TV.textSize = test?: 16f
                wt_TV.textSize = test?: 16f
                whn_TV.textSize = test?: 16f
                user_add.textSize = test?: 16f
                desc_add.textSize = test?: 16f
                type_add.textSize = test?: 16f
                time_add.textSize = test?: 16f



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
        private const val cam_id = 123
        private const val CAMERA_REQUEST = 1888



    }



}