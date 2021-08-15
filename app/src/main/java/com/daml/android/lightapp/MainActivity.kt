package com.daml.android.lightapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.telephony.SmsManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import android.util.Log
import android.view.Gravity
import java.nio.charset.Charset
import java.text.Normalizer


class MainActivity : AppCompatActivity() {
    private lateinit var cameraM: CameraManager
    lateinit var cameraL: Camera
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val objetoSOSUbicacion = SOSClass(this)
    var latitud: String = ""
    var longitud: String = ""
    val REQUEST_ID_MULTIPLE_PERMISSIONS = 7 //7 de la suerte :')
    var permissionsNeeded = mutableListOf<String>()
    private val RQ_SPEECH_REC=102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    //Function for the 3 ImageButtons in activity_main.xml
    fun buttonPressedMain (view: View) {
        when(view.id) {
            R.id.imageButtonFoco -> {
                val intent = Intent(this, LightsActivity::class.java)
                startActivity(intent)
            }
            R.id.imageButtonSOS -> {
                checkPermissions()
            }
            R.id.imageButtonUbicacion -> {
                //Todo: Reemplazar LocationActivity por el nombre del archivo .kt de la actividad Ubicación
                //val intent = Intent(this, LocationActivity::class.java)
                //startActivity(intent)
                //changeStatusIntensity(2,30)
            }
            R.id.imageButtonComandoVoz -> {
                activateVoice()
                //changeStatusOnOff(1, false)
            }
        }
    }

    private fun checkPermissions() {

        permissionsNeeded = mutableListOf()
        if(!objetoSOSUbicacion.checkSOSLocPermission()){
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            permissionsNeeded.add(Manifest.permission.SEND_SMS)
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            permissionsNeeded.add(Manifest.permission.CAMERA)
        }
        //Si alguno de los permisos no ha sido aceptado
        if(!permissionsNeeded.isEmpty()){
            //Pregunta por los permisos faltantes
            requestMissingPermissions()
        }
        //Si todos los permisos han sido aceptados
        else{
            //Prende lamparita
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flashLight()
            }else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1 || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP){
                flashLightL()
            }
            //Obtén ubicación
            getLastLocation()
        }

    }

    private fun flashLightL() {
        cameraL = Camera.open()
        val p: Camera.Parameters = cameraL.getParameters()
        p.flashMode = Camera.Parameters.FLASH_MODE_TORCH
        cameraL.setParameters(p)

        try {
            cameraL.setPreviewTexture(SurfaceTexture(0))
        } catch (ex: IOException) {
            Toast.makeText(this, "Error ", Toast.LENGTH_SHORT).show()
        }
        cameraL.startPreview()
    }

    private fun flashLight() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraM = getSystemService(CAMERA_SERVICE) as CameraManager
            val  cameraListId = cameraM.cameraIdList[0]
            cameraM.setTorchMode(cameraListId, true)
        }
    }

    private fun alertCameraPermissions() {
        //Si el usuario le dio a denegar permiso
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
            //Toast.makeText(this,"Los permisos Camara ya han sido rechazados",Toast.LENGTH_SHORT).show()
            //Explica al usuario por qué debe aceptar el permiso
            AlertDialog.Builder(this)
                .setMessage("Se requiere del permiso Cámara para prender la lámpara del dispositivo")
                .setPositiveButton("OK", null).show()
        //Si le dio a no volver a preguntar por el permiso
        }else{
            Toast.makeText(this, "Por favor, activa el permiso de Cámara en la configuración de tu teléfono", Toast.LENGTH_SHORT).show()
        }
    }

    private fun alertSendSMSPermissions() {
        //Si el usuario le dio a denegar permiso
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)){
            AlertDialog.Builder(this)
                .setMessage("Se requiere del permiso SMS para enviar mensajes a tus contactos de emergencia")
                .setPositiveButton("OK", null).show()
        //Si le dio a no volver a preguntar por el permiso
        }else{
            Toast.makeText(this, "Por favor, activa el permiso de SMS en la configuración de tu teléfono",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun alertLocationPermissions() {
        //Si el usuario le dio a denegar permiso
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            AlertDialog.Builder(this)
                .setMessage("Se requiere del permiso Ubicación para enviar la ubicación en la que te encuentras")
                .setPositiveButton("OK", null).show()
        //Si le dio a no volver a preguntar por el permiso
        }else{
            Toast.makeText(this, "Por favor, activa el permiso de Ubicación en la configuración de tu teléfono",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestMissingPermissions(){
        ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(),REQUEST_ID_MULTIPLE_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS){
            if(grantResults.isNotEmpty()){
                var algunPermisoDenegado = false
                for(i in 0..permissionsNeeded.size-1){
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED){
                        algunPermisoDenegado = true
                        when(permissionsNeeded[i]){
                            Manifest.permission.ACCESS_FINE_LOCATION -> alertLocationPermissions()
                            Manifest.permission.SEND_SMS -> alertSendSMSPermissions()
                            Manifest.permission.CAMERA -> alertCameraPermissions()
                        }
                    }
                }
                //Si todos los permisos preguntados fueron otorgados
                if(algunPermisoDenegado == false){
                    //Prende la lámpara
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        flashLight()
                    }else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1 || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP){
                        flashLightL()
                    }
                    //Obtén ubicación y envía SMS
                    getLastLocation()
                //Si se denegó algún permiso
                }else {
                    //Ve si el permiso de cámara se otorgó e intenta al menos prender la lámpara
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            flashLight()
                        }else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1 || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP){
                            flashLightL()
                        }
                    }
                }
            }
        }
    }

    //Función para enviar SMS, esta debe ser llamada después de obtener la ubicación
    private fun sendSMS() {
        val enviarSMS= SmsManager.getDefault()
        val numeroUno=5512242480
        val numeroDos=5574044562

        //Formato para enviar mensaje SOS
        val enlaceUbicacion ="https://www.google.com.mx/maps/search/?api=1&query="

        if(latitud != "" && longitud != ""){
            val mensaje1 =
                "Eres mi contacto de emergencia, ayuda, esta es mi ubicacion:\n$enlaceUbicacion$latitud,$longitud"
            val mensaje1_1= "Mis coordenadas:\n\nLatitud: $latitud \nLongitud: $longitud"

            val mensaje2= "SOS, esta es mi ubicacion: $enlaceUbicacion$latitud,$longitud"
            println(mensaje1)

            //Sentencia para enviar mensaje a contacto
            enviarSMS.sendTextMessage(numeroUno.toString(),null, mensaje1,null,null)
            //enviarSMS.sendTextMessage(numeroUno.toString(),null, mensaje1_1,null,null)
            enviarSMS.sendTextMessage(numeroDos.toString(),null, mensaje2,null,null)

            //Mensaje en la aplicación de éxito
            Toast.makeText(this,"Mensaje enviado a tus dos contactos",Toast.LENGTH_SHORT).show()
            Toast.makeText(this,"Lampara encendida",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(
                this,
                "Hubo un error al enviar el SMS, esto depende de tu red, reintenta presionar el botón",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //Función para obtener ubicación
    fun getLastLocation(){
        if(objetoSOSUbicacion.checkSOSLocPermission()){
            if(objetoSOSUbicacion.isLocationEnable()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        var locationRequest = LocationRequest()
                        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        locationRequest.interval = 0
                        locationRequest.fastestInterval = 0
                        locationRequest.numUpdates = 1
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                            this
                        )
                        fusedLocationProviderClient!!.requestLocationUpdates(
                            locationRequest, locationCallback, Looper.myLooper()
                        )
                    } else {
                        //Usar location.longitude y location.latitude
                        latitud = location.latitude.toString()
                        longitud = location.longitude.toString()
                        //Para pruebas directas
                        //Toast.makeText(contextM, "get ${location.latitude}", Toast.LENGTH_SHORT).show()
                        //Toast.makeText(contextM, "get ${location.longitude}", Toast.LENGTH_SHORT).show()
                    }
                    sendSMS()
                }
            }else{
                Toast.makeText(this, "Activa ubicación en tu teléfono", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult){
            val lastLocation: Location = locationResult.lastLocation
            //Usar lastLocation.longitude y lastLocation.latitude
            latitud = lastLocation.latitude.toString()
            longitud = lastLocation.longitude.toString()
            //Para pruebas directas
            //Toast.makeText(contextM, "loc"+lastLocation.latitude.toString(), Toast.LENGTH_SHORT).show()
            //Toast.makeText(contextM, "loc"+lastLocation.longitude.toString(), Toast.LENGTH_SHORT).show()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RQ_SPEECH_REC && resultCode == Activity.RESULT_OK){
            val result:ArrayList<String>? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            Toast.makeText(this,result?.get(0).toString(),Toast.LENGTH_LONG).show()
            val textoReconocido = result?.get(0).toString()  // Aquí se obtiene lo leído de la voz del ususario
            println(textoReconocido)
            separateWords(textoReconocido)
        }
    }


    fun separateWords(str:String){

        var arr: List<String> = str.split(" ")
        var size = arr.size

        if(size in 2..3){
            if(size<3){
                var action = arr[0].toLowerCase()
                val id = arr[1].toLowerCase()
                var numFoco= obtenerID(id)

                val aP = Regex("prend(?:iendo|ido|er|e|a|o)?|[e]?n[csz](?:ie|e)nd(?:iendo|o|a|er|e|ido)?|a[ck]ti[bv](?:o|ando|ar|ado|a|e|)|[h]?a[bv]ilit(?:e|o|ando|ado|ar|a)|[ck]one[ck]t(?:o|e|ando|ar|ado|a)|ini[csz]i(?:e|o|ando|ar|ado|a)|^on\$");
                val aA = Regex("apag(?:ue|e|o|ando|ado|ar|a)|de[sz]a[ck]ti[bv](?:o|e|ando|ado|ar|a)|in[h]?a[bv]ilit(?:o|e|ando|ado|ar|a)|de[sz]a[ck]ti[bv](?:o|e|ando|ado|ar|a)|^of\$|^off\$")

                val coincideActionPrender = aP.matches(action)
                val coincideActionApagar = aA.matches(action)

                if (coincideActionPrender){

                    val estado = changeStatusOnOff(numFoco,false)
                    println(numFoco)
                    if(estado){
                        Toast.makeText(this, "El foco se prendió",Toast.LENGTH_SHORT).show()
                        println("El foco se prendió")
                    }


                }else if (coincideActionApagar){

                    val estado = changeStatusOnOff(numFoco,true)
                    println(numFoco)
                    if(estado){
                        Toast.makeText(this, "El foco se apagó",Toast.LENGTH_LONG).show()
                        println("El foco se apagó")
                    }

                }

            }else{

                var action = arr[0].toLowerCase()
                val id = arr[1].toLowerCase()
                var numFoco= obtenerID(id)
                val intensidad = arr[2]
                val intensidadNum=intensidad.toInt()
                println(intensidadNum)

                val aI = Regex("^pon\$|pon(?:er|iendo)|pue[sz]to|e[sz]ta[bv]le[csz](?:[ck]o|[ck]a|iendo|ido|er|e)|[ck]olo[ckq](?:ue|e|o|ar|ando|ado|a)|[ck]am[bv]i(?:e|o|ando|ar|ado|a)?|[ck]onfigur(?:e|o|ando|ado|ar|a)|inten[csz]idad|ilumin(?:e|o|ando|ado|ar|a)|modifi[ckq](?:ue|e|o|ando|ado|ar|a)")

                val coincideActionIntensidad = aI.matches(action)

                if(coincideActionIntensidad){
                    val estado = changeStatusIntensity(numFoco,intensidadNum)
                    if(estado){
                        Toast.makeText(this, "Foco prendido a ${intensidad}%",Toast.LENGTH_LONG).show()
                        println("Foco prendido a ${intensidad}%")
                    }else{
                        Toast.makeText(this, "Foco no pudo modificar su intensidad",Toast.LENGTH_LONG).show()
                        println("Foco no pudo modificar su intensidad")
                    }

                }else{
                    Toast.makeText(this, "Instrucción no clara, repítelo por favor",Toast.LENGTH_LONG).show()
                }


            }

        }else{
            Toast.makeText(this,"Instrucción no específica",Toast.LENGTH_LONG)
            println("Instruccion no especifica")
        }

    }

    fun obtenerID(foco: String):Int{

        val rege = Regex("re[c|k]amara|[e]?[sz]tan[csz]ia|[sz]ala|[ck]omedo[r]?")
        val valMed = rege.find(foco)
        var Id = 0
        if(valMed != null){
            val rec = Regex("re[c|k]amara")
            val es = Regex("[e]?[sz]tan[csz]ia")
            val sa = Regex("[sz]ala")
            val com = Regex("[ck]omedo[r]?")

            if(rec.matches("foco")){
                Id = 3
            } else if (es.matches("foco")){
                Id = 4
            }else if(sa.matches("foco")){
                Id = 5
            }else if(com.matches("foco")){
                Id = 6
            }
        }else{
            Toast.makeText(this,"Esa habitación no está registrada",Toast.LENGTH_SHORT).show()
            println("Esa habitación no está registrada")
        }
        return Id

    }


    fun activateVoice() {

        if(!SpeechRecognizer.isRecognitionAvailable(this)){
            Toast.makeText(this, "Reconocimiento de voz no disponoble", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"¿Qué deseas?")
            startActivityForResult(intent,RQ_SPEECH_REC)
        }

    }


    fun changeStatusOnOff(bulbNumber: Int, isLit: Boolean): Boolean{
        val queue = Volley.newRequestQueue(this)
        val url = "https://appdevops.000webhostapp.com/crud.php"
        val state = if (isLit) 0 else 1
        val requestBody = "id=${bulbNumber}" + "&editar=1" + "&intensidad=100" + "&estado=${state}"
        var success = false

        val stringRequest : StringRequest =
            object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    // response
                    var strResp = response.toString()
                    Log.d("API", strResp)
                    //b.setImageResource(image)
                    //toggleBulb(b)
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

    fun changeStatusIntensity(bulbNumber: Int, Intensity: Int): Boolean{
        val queue = Volley.newRequestQueue(this)
        val url = "https://appdevops.000webhostapp.com/crud.php"
        //val state = if (isLit) 0 else 1
        val requestBody = "id=${bulbNumber}" + "&editar=1" + "&intensidad=$Intensity" + "&estado=0"
        var success = false

        val stringRequest : StringRequest =
            object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    // response
                    var strResp = response.toString()
                    Log.d("API", strResp)
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
}