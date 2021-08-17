package com.daml.android.lightapp

import android.graphics.drawable.Drawable
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.accessibility.AccessibilityEventSource
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.nio.charset.Charset




class LightsActivity : AppCompatActivity() {
    class Foco(id: Int, status: Boolean, intensity: Int){
        val iD: Int = id
        var sTatus : Boolean = status
        var iNtensity : Int = intensity
            get() = field
            set(value) {
                field = value
            }
    }
    var foco1 = Foco(1,false,0)
    var foco2 = Foco(2,false,0)
    var foco3 = Foco(3,false,20)
    var foco4 = Foco(4,false,0)
    var foco5 = Foco(5,false,0)
    var foco6 = Foco(6,false,0)

    var focos : Array<Foco> = arrayOf(foco1,foco2,foco3,foco4,foco5,foco6)

    var PlugOneIsLit = false
    var PlugTwoIsLit = false
    var bulbOneIsLit = false
    var bulbTwoIsLit = false
    var bulbThreeIsLit = false
    var bulbFourIsLit = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lights)
        //initialUpdateStatus()
    }

    override fun onStart() {
        super.onStart()
        initialUpdateStatus()
        variablesSeeksBars()

    }

    //This is the onClick listener for the three light buttons
    fun buttonPressed (view: View) {
        when(view.id) {
            R.id.plusButton -> addingBulbs()
            R.id.changeNameBtn1 -> changingName(1)
            R.id.changeNameBtn2 -> changingName(2)
            R.id.changeNameBtn3 -> changingName(3)
            R.id.changeNameBtn4 -> changingName(4)
            R.id.imagePlug1 -> changeStatus(1, PlugOneIsLit, view)
            R.id.imagePlug2 -> changeStatus(2, PlugTwoIsLit, view)
            R.id.imageButton1 -> changeStatus(3, bulbOneIsLit, view)
            R.id.imageButton2 -> changeStatus(4, bulbTwoIsLit, view)
            R.id.imageButton3 -> changeStatus(5, bulbThreeIsLit, view)
            R.id.imageButton4 -> changeStatus(6, bulbFourIsLit, view)

        }
    }

    fun addingBulbs(){

        var layout : LinearLayout = findViewById(R.id.layoutBulbOne)
        var layout2 : LinearLayout = findViewById(R.id.layoutBulbTwo)
        var layout3 : LinearLayout = findViewById(R.id.layoutBulbThre)
        var layout4 : LinearLayout = findViewById(R.id.layoutBulbFour)
        if(!layout.isVisible) {

            layout.isVisible = true
        }
        else if(!layout2.isVisible) layout2.isVisible = true
        else if(!layout3.isVisible) layout3.isVisible = true
        else if(!layout4.isVisible) layout4.isVisible = true

    }
    fun changingName(id: Int){
        var boton : Button = findViewById(R.id.changeNameBtn1)
        var editext : EditText = findViewById(R.id.editNameTxt1)
        var textBulb: TextView = findViewById(R.id.textView1)

        when (id){
            2 -> {
                boton = findViewById(R.id.changeNameBtn2)
                editext = findViewById(R.id.editNameTxt2)
                textBulb = findViewById(R.id.textView2)
            }
            3 -> {
                boton = findViewById(R.id.changeNameBtn3)
                editext = findViewById(R.id.editNameTxt3)
                textBulb = findViewById(R.id.textView3)
            }
            4 -> {
                boton = findViewById(R.id.changeNameBtn4)
                editext = findViewById(R.id.editNameTxt4)
                textBulb = findViewById(R.id.textView4)
            }
        }

        if(boton.text == "Cambiar Nombre"){
            boton.setText("Cambiar")
            editext.isVisible = true
        }
        else{
            boton.setText("Cambiar Nombre")
            textBulb.text = editext.getText()
            editext.isVisible = false
        }
    }

    fun changeStatus(bulbNumber: Int, isLit: Boolean, button: View): Boolean{
        //var intensity = variablesSeeksBars()
        //var intensity1 = intensity.split("/").toTypedArray()
        variablesSeeksBars()
        val queue = Volley.newRequestQueue(this)
        val url = "https://appdevops.000webhostapp.com/crud.php"
        val state = if (isLit) 0 else 1
        var image = 0
        if(bulbNumber>2) {image = if (isLit) R.drawable.offbulb else R.drawable.onbulb}
        else{image = if (isLit) R.drawable.tomacorrienteoff else R.drawable.tomacorrienteon}
        val b = findViewById<ImageButton>(button.id)
        var requestBody = "0"
        if(bulbNumber>2){requestBody="id=${bulbNumber}" + "&editar=1" + "&intensidad=${focos[bulbNumber-1].iNtensity}"+ "&estado=${state}"}
        else{requestBody ="id=${bulbNumber}" + "&editar=1" + "&intensidad=100 "+ "&estado=${state}"}
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
            R.id.imagePlug1 -> PlugOneIsLit = !PlugOneIsLit
            R.id.imagePlug2 -> PlugTwoIsLit = !PlugTwoIsLit
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
    fun getFromUrl(url: String,label: ImageButton,id:Int){

        val label = label
        //val label : TextView = findViewById(R.id.label)

        val queue = Volley.newRequestQueue(this)
        val url = url
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                var strResp = response.toString()
                var strFirstClean = strResp.split(">").toTypedArray()
                val strSecondClean1 = strFirstClean[0].split("-").toTypedArray()
                val strSecondClean2 = strFirstClean[1].split("-").toTypedArray()
                val strSecondClean3 = strFirstClean[2].split("-").toTypedArray()
                val strSecondClean4 = strFirstClean[3].split("-").toTypedArray()
                val strSecondClean5 = strFirstClean[4].split("-").toTypedArray()
                val strSecondClean6 = strFirstClean[5].split("-").toTypedArray()

                var img: Drawable

                if(id ==1){
                    strResp = strSecondClean1[1]
                    PlugOneIsLit = true
                }
                else if(id ==2){
                    strResp = strSecondClean2[1]
                    PlugTwoIsLit = true
                }
                else if(id ==3){
                    strResp = strSecondClean3[1]
                    bulbOneIsLit = true
                    foco3.iNtensity=strSecondClean3[2].toInt()
                }
                else if(id ==4){
                    strResp = strSecondClean4[1]
                    bulbTwoIsLit= true
                    foco4.iNtensity=strSecondClean4[2].toInt()
                }
                else if(id ==5){
                    strResp = strSecondClean5[1]
                    bulbThreeIsLit= true
                    foco5.iNtensity=strSecondClean5[2].toInt()
                }
                else if(id ==6){
                    strResp = strSecondClean6[1]
                    bulbFourIsLit= true
                    foco6.iNtensity=strSecondClean6[2].toInt()
                }

                println(strResp)
                if (strResp == "1") {
                    if (id !=1 && id !=2){
                        label.setImageResource(R.drawable.onbulb)
                    }
                    else{
                        label.setImageResource(R.drawable.tomacorrienteon)
                    }
                }
                variablesSeeksBars()

            },
            Response.ErrorListener { println("Error") })


        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }
    fun variablesSeeksBars(){

        var intensidadFocoUno: TextView = findViewById(R.id.txtvIntensidadFocoUno)
        var intensidadFocoDos: TextView = findViewById(R.id.txtvIntensidadFocoDos)
        var intensidadFocoTres: TextView = findViewById(R.id.txtvIntensidadFocoTres)
        var intensidadFocoCuatro: TextView = findViewById(R.id.txtvIntensidadFocoCuatro)

        //Variables para la barra de desplazamiento
        val barraFocoUno: SeekBar = findViewById(R.id.sBfocoUno)
        val barraFocoDos: SeekBar = findViewById(R.id.sBfocoDos)
        val barraFocoTres: SeekBar = findViewById(R.id.sBfocoTres)
        val barraFocoCuatro: SeekBar = findViewById(R.id.sBfocoCuatro)

        barraFocoUno.setProgress(foco3.iNtensity)
        intensidadFocoUno.text="Intensidad: "+barraFocoUno.progress
        barraFocoDos.setProgress(foco4.iNtensity)
        intensidadFocoDos.text="Intensidad: "+barraFocoDos.progress
        barraFocoTres.setProgress(foco5.iNtensity)
        intensidadFocoTres.text="Intensidad: "+barraFocoTres.progress
        barraFocoCuatro.setProgress(foco6.iNtensity)
        intensidadFocoCuatro.text="Intensidad: "+barraFocoCuatro.progress

        barraFocoUno.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                foco3.iNtensity=progress
                intensidadFocoUno.text ="Intensidad: "+ foco3.iNtensity.toString()

            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

                changeStatus1(3,bulbOneIsLit,foco3)
            }
        })

        barraFocoDos.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                foco4.iNtensity=progress
                intensidadFocoDos.text ="Intensidad: "+ foco4.iNtensity.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        barraFocoTres.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                foco5.iNtensity=progress
                intensidadFocoTres.text ="Intensidad: "+ foco5.iNtensity.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        barraFocoCuatro.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                foco6.iNtensity=progress
                intensidadFocoCuatro.text ="Intensidad: "+ foco6.iNtensity.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //changeStatus1(6,bulbFourIsLit,foco6)
            }
        })
    }

    fun changeStatus1(bulbNumber: Int, isLit: Boolean, bulbo: Foco): Boolean{
        val queue = Volley.newRequestQueue(this)
        val url = "https://appdevops.000webhostapp.com/crud.php"
        val state = if(isLit) 1 else 0
        var requestBody = "0"
        requestBody="id=${bulbNumber}" + "&editar=1" + "&intensidad=${bulbo.iNtensity}"+ "&estado=${state}"
        var success = false

        val stringRequest : StringRequest =
            object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    // response
                    var strResp = response.toString()
                    Log.d("API", strResp)
                    success = true
                    println("Hola 1")
                    println(bulbo.iNtensity.toString())
                    println("Hola 2")
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

    // Function to get the initial status
    fun initialUpdateStatus(){

        // The status obtained will be in the value of labels.text
        val label_1 : ImageButton = findViewById(R.id.imagePlug1)
        val label_2 : ImageButton = findViewById(R.id.imagePlug2)
        val label_3 : ImageButton = findViewById(R.id.imageButton1)
        val label_4 : ImageButton = findViewById(R.id.imageButton2)
        val label_5 : ImageButton = findViewById(R.id.imageButton3)
        val label_6 : ImageButton = findViewById(R.id.imageButton4)

        // Change to the url to obtain status of each bulb
        val url_get_satus_1 = "https://appdevops.000webhostapp.com/ConsultaESP32.php" //url for bulb_1
        val url_get_satus_2 = "https://appdevops.000webhostapp.com/ConsultaESP32.php"
        val url_get_satus_3 = "https://appdevops.000webhostapp.com/ConsultaESP32.php"
        val url_get_satus_4 = "https://appdevops.000webhostapp.com/ConsultaESP32.php"
        val url_get_satus_5 = "https://appdevops.000webhostapp.com/ConsultaESP32.php"
        val url_get_satus_6 = "https://appdevops.000webhostapp.com/ConsultaESP32.php"

        // For each bulb use a label
        getFromUrl(url_get_satus_1,label_1,1)
        getFromUrl(url_get_satus_2,label_2,2)
        getFromUrl(url_get_satus_3,label_3,3)
        getFromUrl(url_get_satus_4,label_4,4)
        getFromUrl(url_get_satus_5,label_5,5)
        getFromUrl(url_get_satus_6,label_6,6)

    }

}