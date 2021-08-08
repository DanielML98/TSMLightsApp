package com.daml.android.lightapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.telephony.SmsManager
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

/*import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import android.content.Context*/



class MainActivity : AppCompatActivity() {
    private lateinit var cameraM: CameraManager
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val objetoSOSUbicacion = SOSClass(this)
    var latitud: String = ""
    var longitud: String = ""
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
                //Reemplazar LocationActivity por el nombre del archivo .kt de la actividad Ubicación
                //val intent = Intent(this, LocationActivity::class.java)
                //startActivity(intent)
            }
        }
    }

    private fun checkPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            requestSendSMSPermissions()
        }else{
            getLastLocation()
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestCameraPermissions()
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                flashLight()
            }
        }
        objetoSOSUbicacion.RequestPermissionLocation()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun flashLight() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraM = getSystemService(CAMERA_SERVICE) as CameraManager
            val  cameraListId = cameraM.cameraIdList[0]
            cameraM.setTorchMode(cameraListId,true)
        }

        Toast.makeText(this,"Linterna encendida",Toast.LENGTH_SHORT).show()
    }

    private fun requestCameraPermissions() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
            Toast.makeText(this,"Los permisos ya han sido rechazados",Toast.LENGTH_SHORT).show()

        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),2)
        }
    }

    private fun requestSendSMSPermissions() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)){
            Toast.makeText(this,"Los permisos ya han sido rechazados",Toast.LENGTH_SHORT).show()

        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS),1)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                sendSMS()
            }else{
                Toast.makeText(this,"Permisos rechazados",Toast.LENGTH_SHORT).show()
            }
        }
        if(requestCode == 2){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    flashLight()
                }
            }else{
                Toast.makeText(this,"Permisos rechazados",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendSMS() {
        val enviarSMS= SmsManager.getDefault()
        val numeroUno=5571959003
        val numeroDos=5548698086

        //Formato para enviar mensaje SOS
        val enlaceUbicacion ="https://www.google.com.mx/maps/search/?api=1&query"

        if(latitud != "" && longitud != ""){
            val mensaje =
                "Eres mi contacto de emergencia, ¡ayuda!, esta es mi ubicacion:\n\n$enlaceUbicacion$latitud,$longitud\n\nLatitud: $latitud\nLongitud: $longitud"
            println(mensaje)

            //Sentencia para enviar mensaje a contacto
            enviarSMS.sendTextMessage("$numeroUno",null,
                "SOS, esta es mi ubicacion: $enlaceUbicacion$latitud,$longitud",null,null)
            //enviarSMS.sendTextMessage("+$numeroDos.toString()",null,
            //mensaje,null,null)

            //Mensaje en la aplicación de éxito
            Toast.makeText(this,"Mensaje enviado a tus dos contactos",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this,"Hubo un error, revisa permisos de ubicación y que tengas la ubicación activada",Toast.LENGTH_SHORT).show()
        }
    }

    fun getLastLocation(){
        if(objetoSOSUbicacion.checkPermission()){
            if(objetoSOSUbicacion.isLocationEnable()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        var locationRequest = LocationRequest()
                        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        locationRequest.interval = 0
                        locationRequest.fastestInterval = 0
                        locationRequest.numUpdates = 1
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
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
        }else{
            objetoSOSUbicacion.RequestPermissionLocation()
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
}