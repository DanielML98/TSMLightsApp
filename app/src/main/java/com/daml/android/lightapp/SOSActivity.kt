package com.daml.android.lightapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

class SOSActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s_o_s)
        val button: Button = findViewById(R.id.btnEnviarSMS)
        button.setOnClickListener {
            var enviarSMS=SmsManager.getDefault()
            enviarSMS.sendTextMessage("5559462779",null,"Ubicación",null,null)
        }

    }
}