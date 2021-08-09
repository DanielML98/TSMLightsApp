package com.daml.android.lightapp

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat

class SOSClass (context: Activity){
    var contextM = context
    val PERMISSION_ID : Int = 28

    //Clase encargada de solicitar permisos de ubicación para SOS, se podrá editar o quitar luego

    //Función que revisa si a la app se le han dado permisos para ubicación
    fun checkSOSLocPermission(): Boolean {
        if(ActivityCompat.checkSelfPermission(contextM, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ||ActivityCompat.checkSelfPermission(contextM, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    //Solicita permiso de ubicación al usuario si no se ha otorgado
    fun RequestPermissionLocation(){
        ActivityCompat.requestPermissions(
            contextM, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    //Checa si el usuario activó ubicación
    fun isLocationEnable():Boolean{
        var locationManager = contextM.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

}