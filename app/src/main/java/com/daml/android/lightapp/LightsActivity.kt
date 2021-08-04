package com.daml.android.lightapp

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.nio.charset.Charset

class LightsActivity : AppCompatActivity() {

    var bulbOneIsLit = false
    var bulbTwoIsLit = false
    var bulbThreeIsLit = false
    var bulbFourIsLit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lights)
    }

    //This is the onClick listener for the three light buttons
    fun buttonPressed (view: View) {
        when(view.id) {
            R.id.imageButton1 -> changeStatus(1, bulbOneIsLit, view)
            R.id.imageButton2 -> changeStatus(2, bulbTwoIsLit, view)
            R.id.imageButton3 -> changeStatus(3, bulbThreeIsLit, view)
            R.id.imageButton4 -> changeStatus(4, bulbFourIsLit, view)
        }
    }

    fun changeStatus(bulbNumber: Int, isLit: Boolean, button: View): Boolean{
        val queue = Volley.newRequestQueue(this)
        val url = "https://appdevops.000webhostapp.com/crud.php"
        val state = if (isLit) 0 else 1
        val image = if (isLit) R.drawable.offbulb else R.drawable.onbulb
        val b = findViewById<ImageButton>(button.id)
        val requestBody = "id=${bulbNumber}" + "&editar=1" + "&intensidad=80" + "&estado=${state}"
        var success = false

        val stringRequest : StringRequest =
            object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    // response
                    var strResp = response.toString()
                    Log.d("API", strResp)
                    b.setImageResource(image)
                    toggleBulb(b)
                    success = true
                },
                Response.ErrorListener { error ->
                    Log.e("API", "error => $error")
                    var correctToast = Toast.makeText(this, R.string.db_error, Toast.LENGTH_SHORT)
                    correctToast.setGravity(Gravity.TOP,0,0)
                    correctToast.show()
                }
            ){
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }

// Add the request to the RequestQueue.
        queue.add(stringRequest)
        return success
    }

    fun toggleBulb(bulb: ImageButton) {
        when(bulb.id) {
            R.id.imageButton1 -> bulbOneIsLit = !bulbOneIsLit
            R.id.imageButton2 -> bulbTwoIsLit = !bulbTwoIsLit
            R.id.imageButton3 -> bulbThreeIsLit = !bulbThreeIsLit
            R.id.imageButton4 -> bulbFourIsLit = !bulbFourIsLit
        }
    }


    /*fun postVolley(bulbNumber: Int) {
        val queue = Volley.newRequestQueue(this)
        val url = "https://appdevops.000webhostapp.com/crud.php"


        val stringReq : StringRequest =
            object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    // response
                    var strResp = response.toString()
                    Log.d("API", strResp)
                },
                Response.ErrorListener { error ->
                    Log.d("API", "error => $error")
                }
            ){
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
        queue.add(stringReq)
    }*/


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