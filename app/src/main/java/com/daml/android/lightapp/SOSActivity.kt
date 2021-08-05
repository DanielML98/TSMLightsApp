package com.daml.android.lightapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SOSActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s_o_s)
        val button: Button = findViewById(R.id.btnEnviarSMS)
        button.setOnClickListener {
            checkPermissions()
        }

    }
    private fun checkPermissions() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            requestSendSMSPermissions()
        }else{
            sendSMS()
        }
    }

    private fun requestSendSMSPermissions() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.SEND_SMS)){
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
    }

    private fun sendSMS() {
        var enviarSMS=SmsManager.getDefault()
        enviarSMS.sendTextMessage("5559462779",null,"Ubicación",null,null)
        enviarSMS.sendTextMessage("5514919708",null,"Ubicación",null,null)
        Toast.makeText(this,"Mensaje enviado",Toast.LENGTH_SHORT).show()
    }
}