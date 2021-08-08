package com.daml.android.lightapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    //Function for the 3 ImageButtons in activity_main.xml
    fun buttonPressedMain (view: View) {
        when(view.id) {
            R.id.imageButtonfocooff -> {
                val intent = Intent(this, LightsActivity::class.java)
                startActivity(intent)
            }
            R.id.imageButtonemergency -> {
                //Reemplazar SOSActivity por el nombre del archivo .kt de la actividad SOS
                //val intent = Intent(this, SOSActivity::class.java)
                //startActivity(intent)
            }
            R.id.imageButtonpinlocation -> {
                //Reemplazar LocationActivity por el nombre del archivo .kt de la actividad Ubicación
                //val intent = Intent(this, LocationActivity::class.java)
                //startActivity(intent)
            }
        }
    }
}