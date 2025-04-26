package com.example.telephony

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var telephonyManager: TelephonyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        val btnFetch = findViewById<Button>(R.id.btnFetch)
        val tvDeviceName = findViewById<TextView>(R.id.tvDeviceName)
        val tvPhoneType = findViewById<TextView>(R.id.tvPhoneType)
        val tvNetworkISO = findViewById<TextView>(R.id.tvNetworkISO)
        val tvSimISO = findViewById<TextView>(R.id.tvSimISO)
        val tvIMEI = findViewById<TextView>(R.id.tvIMEI)
        val tvSoftwareVersion = findViewById<TextView>(R.id.tvSoftwareVersion)

        btnFetch.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_PHONE_STATE), 1)
            } else {
                val phoneType = when (telephonyManager.phoneType) {
                    TelephonyManager.PHONE_TYPE_GSM -> "GSM"
                    TelephonyManager.PHONE_TYPE_CDMA -> "CDMA"
                    TelephonyManager.PHONE_TYPE_SIP -> "SIP"
                    TelephonyManager.PHONE_TYPE_NONE -> "None"
                    else -> "Unknown"
                }

                val imei = try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                            telephonyManager.imei ?: "Not available"
                        } else {
                            "Permission denied"
                        }
                    } else {
                        "Not supported"
                    }
                } catch (e: Exception) {
                    "Error: ${e.localizedMessage}"
                }

                tvDeviceName.text = "Device Name: ${Build.MODEL}"
                tvPhoneType.text = "Phone Type: $phoneType"
                tvNetworkISO.text = "Network Country ISO: ${telephonyManager.networkCountryIso.uppercase()}"
                tvSimISO.text = "SIM Country ISO: ${telephonyManager.simCountryIso.uppercase()}"
                tvIMEI.text = "IMEI: $imei"
                tvSoftwareVersion.text = "Device Software Version: ${telephonyManager.deviceSoftwareVersion ?: "Unavailable"}"
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findViewById<Button>(R.id.btnFetch).performClick()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}