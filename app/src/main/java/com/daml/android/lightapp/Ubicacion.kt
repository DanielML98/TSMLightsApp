package com.daml.android.lightapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.daml.android.lightapp.databinding.ActivityUbicacionBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.*
import kotlin.math.pow
import kotlin.math.sqrt

class Ubicacion : AppCompatActivity() {
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    val PERMISSION_ID = 42
    var permitedDistance = 0.0
    var userLatitude = 0.0
    var userLongitude = 0.0





    // Iniciación tardía del viewBinding
    lateinit var binding : ActivityUbicacionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUbicacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Preguntar si se tiene permisos.
        if (allPermissionsGrantedGPS()){
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        } else {
            // Si no hay permisos solicitarlos al usuario.
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID)
        }

        binding.btndetectar.setOnClickListener {
            leerubicacionactual()
        }
    }

    private fun allPermissionsGrantedGPS() = REQUIRED_PERMISSIONS_GPS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    fun pruebaDistancias(view: View){
        var editLongitude = findViewById(R.id.longitud_input) as EditText
        var editLatitude = findViewById(R.id.latitud_input) as EditText

        var res = isInLocation(editLatitude.text.toString().toDouble(),editLongitude.text.toString().toDouble())

        println(res)

        Toast.makeText(this,res.toString(),Toast.LENGTH_SHORT).show()
    }

    // Returns true if actual distance is in range
    fun isInLocation(latitude:Double,longitude:Double): Boolean{
        //Distancia del punto actual al punto donde se ubico la casa
        var distanceFromLocation = sqrt((userLatitude-latitude).pow(2)+(userLongitude-longitude).pow(2))

        return (distanceFromLocation <= permitedDistance)
    }

    fun setPermitedDistance(view:View){
        var editTextDistance = findViewById(R.id.km_input) as EditText
        val label_distance : TextView = findViewById(R.id.detectKM)

        permitedDistance = editTextDistance.text.toString().toDouble()
        label_distance.text = permitedDistance.toString()
        println(permitedDistance)
    }

    private fun leerubicacionactual(){
        if (checkPermissions()){
            if (isLocationEnabled()){
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient.lastLocation.addOnCompleteListener(this){ task ->
                        var location: Location? = task.result
                        if (location == null){
                            requestNewLocationData()
                        } else {
                            binding.lbllatitud.text = "LATITUD = " + location.latitude.toString()
                            binding.lbllongitud.text = "LONGITUD = " + location.longitude.toString()
                            userLatitude = location.latitude.toDouble()
                            userLongitude = location.longitude.toDouble()
                            println(userLatitude)
                            println(userLongitude)

                        }
                    }
                }
            } else {
                Toast.makeText(this, "Activar ubicación", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                this.finish()
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData(){
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper())
    }

    private val mLocationCallBack = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation : Location = locationResult.lastLocation
            binding.lbllatitud.text = "LATITUD = " + mLastLocation.latitude.toString()
            binding.lbllongitud.text = "LONGITUD = "+ mLastLocation.longitude.toString()
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    companion object {
        private val REQUIRED_PERMISSIONS_GPS= arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    }
}