package com.daml.android.lightapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class LightsActivity : AppCompatActivity() {

    var bulbOneStatus = false
    var bulbTwoStatus = false
    var bulbThreeStatus = false
    var bulbFourStatus = false
    var bulbFiveStatus = false
    var bulbSixStatus = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lights)
    }

    //This is the onClick listener for the three light buttons
    fun buttonPressed (view: View) {
        when(view.id) {
            //R.id.[id del botón luz 1] -> {changeStatus(bulbNumber: "1");bulbOneStatus = !bulbOneStatus }
            //R.id.[id del botón luz 2] -> {changeStatus(bulbNumber: "2"); bulbTwoStatus = !bulbTwoStatus}
            //R.id.[id del botón luz 3] -> {changeStatus(bulbNumber: "3"); bulbThreeStatus = !bulbThreeStatus}
            //R.id.[id del botón luz 4] -> {changeStatus(bulbNumber: "4");bulbOneStatus = !bulbOneStatus }
            //R.id.[id del botón luz 5] -> {changeStatus(bulbNumber: "5"); bulbTwoStatus = !bulbTwoStatus}
            //R.id.[id del botón luz 6] -> {changeStatus(bulbNumber: "6"); bulbThreeStatus = !bulbThreeStatus}
        }
    }

    fun changeStatus(bulbNumber: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "https://danieltsm.000webhostapp.com/ToggleLight.php?Nombre=update&ID=${bulbNumber}&Valor=1" //Placeholder

        val stringRequest = StringRequest(
            Request.Method.POST, url,
            { response ->

            },
            { })

// Add the request to the RequestQueue.
        queue.add(stringRequest)
    }
}