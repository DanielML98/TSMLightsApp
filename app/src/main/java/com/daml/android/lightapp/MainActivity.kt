package com.daml.android.lightapp

import android.Manifest
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
import android.telephony.SmsManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.FusedLocationProviderClient
import java.io.IOException

/*import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import android.content.Context*/



class MainActivity : AppCompatActivity() {
    private lateinit var cameraM: CameraManager
    lateinit var cameraL: Camera
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val objetoSOSUbicacion = SOSClass(this)
    var latitud: String = ""
    var longitud: String = ""
    val REQUEST_ID_MULTIPLE_PERMISSIONS = 7 //7 de la suerte :')
    var permissionsNeeded = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    //Function for the 3 ImageButtons in activity_main.xml
    fun buttonPressedMain(view: View) {
        when (view.id) {
            R.id.imageButtonFoco -> {
                val intent = Intent(this, LightsActivity::class.java)
                startActivity(intent)
            }
            R.id.imageButtonSOS -> {
                checkPermissions()
            }
            R.id.imageButtonUbicacion -> {
                //Reemplazar LocationActivity por el nombre del archivo .kt de la actividad Ubicación
                //val intent = Intent(this, Ubicacion::class.java)
                val intent = Intent(this, ubi2::class.java)

                startActivity(intent)
            }

        }
    }

    private fun checkPermissions() {

        permissionsNeeded = mutableListOf()
        if (!objetoSOSUbicacion.checkSOSLocPermission()) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.SEND_SMS)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.CAMERA)
        }
        //Si alguno de los permisos no ha sido aceptado
        if (!permissionsNeeded.isEmpty()) {
            //Pregunta por los permisos faltantes
            requestMissingPermissions()
        }
        //Si todos los permisos han sido aceptados
        else {
            //Prende lamparita
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flashLight()
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1 || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                flashLightL()
            }
            //Obtén ubicación
            //getLastLocation()
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
            val cameraListId = cameraM.cameraIdList[0]
            cameraM.setTorchMode(cameraListId, true)
        }
    }

    private fun alertCameraPermissions() {
        //Si el usuario le dio a denegar permiso
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //Toast.makeText(this,"Los permisos Camara ya han sido rechazados",Toast.LENGTH_SHORT).show()
            //Explica al usuario por qué debe aceptar el permiso
            AlertDialog.Builder(this)
                .setMessage("Se requiere del permiso Cámara para prender la lámpara del dispositivo")
                .setPositiveButton("OK", null).show()
            //Si le dio a no volver a preguntar por el permiso
        } else {
            Toast.makeText(
                this,
                "Por favor, activa el permiso de Cámara en la configuración de tu teléfono",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun alertSendSMSPermissions() {
        //Si el usuario le dio a denegar permiso
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.SEND_SMS
            )
        ) {
            AlertDialog.Builder(this)
                .setMessage("Se requiere del permiso SMS para enviar mensajes a tus contactos de emergencia")
                .setPositiveButton("OK", null).show()
            //Si le dio a no volver a preguntar por el permiso
        } else {
            Toast.makeText(
                this, "Por favor, activa el permiso de SMS en la configuración de tu teléfono",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun alertLocationPermissions() {
        //Si el usuario le dio a denegar permiso
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            AlertDialog.Builder(this)
                .setMessage("Se requiere del permiso Ubicación para enviar la ubicación en la que te encuentras")
                .setPositiveButton("OK", null).show()
            //Si le dio a no volver a preguntar por el permiso
        } else {
            Toast.makeText(
                this,
                "Por favor, activa el permiso de Ubicación en la configuración de tu teléfono",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun requestMissingPermissions() {
        ActivityCompat.requestPermissions(
            this,
            permissionsNeeded.toTypedArray(),
            REQUEST_ID_MULTIPLE_PERMISSIONS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            if (grantResults.isNotEmpty()) {
                var algunPermisoDenegado = false
                for (i in 0..permissionsNeeded.size - 1) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        algunPermisoDenegado = true
                        when (permissionsNeeded[i]) {
                            Manifest.permission.ACCESS_FINE_LOCATION -> alertLocationPermissions()
                            Manifest.permission.SEND_SMS -> alertSendSMSPermissions()
                            Manifest.permission.CAMERA -> alertCameraPermissions()
                        }
                    }
                }
                //Si todos los permisos preguntados fueron otorgados
                if (algunPermisoDenegado == false) {
                    //Prende la lámpara
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        flashLight()
                    } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1 || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                        flashLightL()
                    }
                    //Obtén ubicación y envía SMS
                    //getLastLocation()
                    //Si se denegó algún permiso
                } else {
                    //Ve si el permiso de cámara se otorgó e intenta al menos prender la lámpara
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            flashLight()
                        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1 || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                            flashLightL()
                        }
                    }
                }
            }
        }
    }

    //Función para enviar SMS, esta debe ser llamada después de obtener la ubicación
    private fun sendSMS() {
        val enviarSMS = SmsManager.getDefault()
        val numeroUno = 5521754838
        val numeroDos = 5574044562

        //Formato para enviar mensaje SOS
        val enlaceUbicacion = "https://www.google.com.mx/maps/search/?api=1&query="

        if (latitud != "" && longitud != "") {
            val mensaje1 =
                "Eres mi contacto de emergencia, ayuda, esta es mi ubicacion:\n$enlaceUbicacion$latitud,$longitud"
            val mensaje1_1 = "Mis coordenadas:\n\nLatitud: $latitud \nLongitud: $longitud"

            val mensaje2 = "SOS, esta es mi ubicacion: $enlaceUbicacion$latitud,$longitud"
            println(mensaje1)

            //Sentencia para enviar mensaje a contacto
            enviarSMS.sendTextMessage(numeroUno.toString(), null, mensaje1, null, null)
            //enviarSMS.sendTextMessage(numeroUno.toString(),null, mensaje1_1,null,null)
            enviarSMS.sendTextMessage(numeroDos.toString(), null, mensaje2, null, null)

            //Mensaje en la aplicación de éxito
            Toast.makeText(this, "Mensaje enviado a tus dos contactos", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "Lampara encendida", Toast.LENGTH_SHORT).show()
        } else {
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
}