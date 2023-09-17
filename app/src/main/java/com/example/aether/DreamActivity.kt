package com.example.aether

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DreamActivity : AppCompatActivity(),  SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var recyclerViewDream: RecyclerView
    private lateinit var dreamdata: ArrayList<Dream>
    private lateinit var DreamAdapter: DreamAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var addr_button: Button
    private lateinit var cr_button: Button
    private lateinit var latestposts_TV : TextView
    private lateinit var sharedPreferences: SharedPreferences
    private var nm: String = ""
    private var showLucid = false




    override fun onCreate(savedInstanceState: Bundle?) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val themeBoolean = sharedPreferences.getBoolean("theme_switch_preference", false)

        if (themeBoolean) {
            setTheme(R.style.AppTheme_Dark)
        } else {
            setTheme(R.style.AppTheme_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dream)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        recyclerViewDream = findViewById<RecyclerView>(R.id.recyclerViewDream)
        recyclerViewDream.layoutManager = LinearLayoutManager(this)
        dreamdata = ArrayList<Dream>()

        DreamAdapter = DreamAdapter(dreamdata)
        recyclerViewDream.adapter = DreamAdapter
        addr_button = findViewById(R.id.addr_button) as Button
        cr_button = findViewById(R.id.cr_button) as Button


        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val textSize = sharedPreferences.getString("text_size_preference", "16")!!.toFloat()

         latestposts_TV = findViewById(R.id.latestposts_TV) as TextView


        latestposts_TV.textSize = textSize


        // Get the values for filtering nightmares and lucid dreams
         nm = sharedPreferences.getString("nm", "") ?: ""
         showLucid = sharedPreferences.getBoolean("showLucid", false)


        loadDB()
        addr_button.setOnClickListener()
        {
            val i = Intent(this@DreamActivity, AddDreamActivity::class.java)
            startActivity(i)
        }

        cr_button.setOnClickListener()
        {
            val i = Intent(this@DreamActivity, AddImageActivity::class.java)
            startActivity(i)
        }


    }

    fun loadDB() {
        db.collection("Hwk4Aether")
            .get()
            .addOnSuccessListener { result ->
                dreamdata.clear()
                for (document in result) {
                    var n = document.get("Username")
                    var dd = document.get("Dream Desc")
                    var t = document.get("Dream Type")
                    var dt = document.get("Date and Time")
                    var u = document.get("url")
                    dreamdata.add(
                        Dream(
                            n as String,
                            dd as String,
                            t as String,
                            dt as String,
                            u as String
                        )
                    )


                }
                DreamAdapter.setData(showN(nm, showLucid))
                DreamAdapter.notifyDataSetChanged()


            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            R.id.mi_ldreams -> {
                showLucid = true
                DreamAdapter.setData(showLD(showLucid))
                true
            }
            R.id.mi_nightmares -> {
                showLucid = false
                val filteredDreams = showN("Nightmare", showLucid)
                DreamAdapter.setData(filteredDreams)
                true
            }
            R.id.mi_alld -> {
                nm = ""
                showLucid = false

                // Reload the data with the new filter values
                loadDB()

                // Update the adapter to show all dreams
                DreamAdapter.setData(dreamdata)
                DreamAdapter.notifyDataSetChanged()
                true

            }
            R.id.mi_settings -> {
                val i = Intent(this@DreamActivity, UserPreferenceActivity::class.java)
                startActivity(i)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showN(nm: String, ld: Boolean): List<Dream> {
        if (nm == "" && !ld) {
            return dreamdata
        } else {
            return dreamdata.filter {
                (nm == "" || it.type.contains(nm)) && (ld == false || it.type.contains("Lucid"))
            }
        }
    }
    private fun showLD(ld: Boolean): List<Dream> {
        if (!ld) {
            return dreamdata
        } else {
            return dreamdata.filter {
                it.type.contains("Lucid")
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

                latestposts_TV.textSize = test?: 16f



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

    override fun onResume() {
        super.onResume()
        //dreamdata.clear()
        loadDB()

        val sp = PreferenceManager.getDefaultSharedPreferences(this@DreamActivity)
        nm = sp.getString("nm", "").toString()

        if (showLucid) {
            DreamAdapter.setData(showLD(showLucid))
            DreamAdapter.notifyDataSetChanged()

        } else {
            DreamAdapter.setData(showN(nm, showLucid))
            DreamAdapter.notifyDataSetChanged()

        }
    }
}

