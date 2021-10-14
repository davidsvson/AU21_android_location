package com.example.au21location

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    val TAG = "!!!"

    private val REQUEST_LOCATION = 1
    var locationRequest : LocationRequest? = null
    lateinit var locationProvider : FusedLocationProviderClient
    lateinit var locationCallback : LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationProvider = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = createLocationRequest()

        // alternativt sätt att skapa vår locationcallback
        //locationCallback = MyCustomLocationCallback()

        // skapa en ny anonym klass som ärver av LocationCallback och skapa ett object av den anonyma klassen
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for ( location in locationResult.locations) {
                    Log.d("!!!", "onLocationResult: lat: ${location.latitude}  lng: ${location.longitude}")
                }
            }
        }

        if( ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED ) {
            Log.d(TAG, "onCreate: permission not granted ")
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION)
        } else {
            Log.d(TAG, "onCreate: permission is already granted")
            locationProvider.lastLocation.addOnSuccessListener { location ->
                if (location != null ) {
                    val lat = location.latitude
                    val lng = location.longitude
                    Log.d(TAG, "onCreate: lat: ${lat}, lng: $lng")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }


    fun startLocationUpdates() {
        if( ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
           == PackageManager.PERMISSION_GRANTED) {
            locationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    fun stopLocationUpdates() {
        locationProvider.removeLocationUpdates(locationCallback)
    }


    fun createLocationRequest() =
       LocationRequest.create().apply {
            interval = 2000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }


    // alternativt sätt att skriva samma sak
//    fun createLocationRequest() : LocationRequest {
//        val request = LocationRequest.create()
//
//        request.interval = 2000
//        request.fastestInterval = 1000
//        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//
//        return request
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
       super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: permission granted!")
                startLocationUpdates()
            } else   {
                Log.d(TAG, "onRequestPermissionsResult: permission denied!")
            }
        }
    }



}