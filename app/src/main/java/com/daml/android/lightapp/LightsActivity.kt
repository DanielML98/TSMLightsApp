package com.daml.android.lightapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
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


    // This function changes label text to the returned string in the GET request to the url
    fun getFromUrl(url: String,label: TextView){

        val label = label
        //val label : TextView = findViewById(R.id.label)

        val queue = Volley.newRequestQueue(this)
        val url = url
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                val strResp = response.toString()
                //println(strResp)
                label.text = strResp

            },
            Response.ErrorListener { println("Error") })


        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    // Function to get the initial status
    fun initialUpdateStatus(view: View){

        // The status obtained will be in the value of labels.text
        //val label_1 : TextView = findViewById(R.id.label2)
        //val label_2 : TextView = findViewById(R.id.label3)
        //val label_3 : TextView = findViewById(R.id.label4)
        //val label_4 : TextView = findViewById(R.id.label5)

        // Change to the url to obtain status of each bulb
        val url_get_satus_1 = "https://tsmpjgv9.000webhostapp.com/get.php?status=0&ID=1" //url for bulb_1
        val url_get_satus_2 = "https://tsmpjgv9.000webhostapp.com/get.php?status=0&ID=2"
        val url_get_satus_3 = "https://tsmpjgv9.000webhostapp.com/get.php?status=0&ID=1"
        val url_get_satus_4 = "https://tsmpjgv9.000webhostapp.com/get.php?status=0&ID=2"

        // For each bulb use a label
        //getFromUrl(url_get_satus_1,label_1)
        //getFromUrl(url_get_satus_2,label_2)
        //getFromUrl(url_get_satus_3,label_3)
        //getFromUrl(url_get_satus_4,label_4)


    }

}