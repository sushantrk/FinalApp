package com.ud.mylastproject

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ud.mylastproject.databinding.ActivityMainBinding
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.MapFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {


    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_CONTACTS
    )
    val permissionCode = 78

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseFirestore


    private lateinit var NAME: String
    private lateinit var PHONE: String
    private lateinit var EMAIL: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val isLogin = sharedPref.getString("Email", "1")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (isAllPermissionsGranted()) {
            if (isLocationEnabled(this)) {
                setUpLocationListener()
            } else {
                showGPSNotEnabledDialog(this)
            }
        } else {
            askForPermission()
        }

        binding.bottomBar.selectedItemId = R.id.nav_home
        inflateFragment(HomeFragment.newInstance())





        binding.bottomBar.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.nav_map -> {
                    inflateFragment(MapsFragment())
                }

                R.id.nav_home -> {
                    inflateFragment(HomeFragment.newInstance())
                }

                R.id.nav_user -> {

                    if (isLogin == "1") {
                        val email = intent.getStringExtra("email")
                        if (email != null) {

                            with(sharedPref.edit())
                            {
                                putString("Email", email)
                                apply()
                            }
                            setText(email)
                        } else {
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        setText(isLogin)
                    }
//                    inflateFragment(UserFragment.newInstance())
                }

                R.id.nav_dashboard -> {
                    inflateFragment(HomeFragment.newInstance())
                }
            }
            true
        }


    }


    private fun setUpLocationListener() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {
                        Log.d("Location89", "onLocationResult: latitude ${location.latitude}")
                        Log.d("Location89", "onLocationResult: longitude ${location.longitude}")


                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val mail = currentUser?.email.toString()

                        val db = Firebase.firestore

                        val locationData = mutableMapOf<String,Any>(
                            "lat" to location.latitude.toString(),
                            "long" to location.longitude.toString(),
                        )


                        db.collection("users").document(mail).update(locationData)
                            .addOnSuccessListener {

                            }.addOnFailureListener {

                            }


                    }
                }
            },
            Looper.myLooper()
        )
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Function to show the "enable GPS" Dialog box
     */
    fun showGPSNotEnabledDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Enable GPS")
            .setMessage("required_for_this_app")
            .setCancelable(false)
            .setPositiveButton("enable_now") { _, _ ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .show()
    }

    private fun isAllPermissionsGranted(): Boolean {
        for (item in permissions) {
            if (ContextCompat
                    .checkSelfPermission(
                        this,
                        item
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }


    private fun askForPermission() {
        ActivityCompat.requestPermissions(this, permissions, permissionCode)
    }

    private fun setText(email: String?) {
        db = FirebaseFirestore.getInstance()
        if (email != null) {
            db.collection("USERS").document(email).get()
                .addOnSuccessListener { tasks ->
                    val fragment = UserFragment()
                    NAME = tasks.get("Name").toString()
                    PHONE = tasks.get("Phone").toString()
                    EMAIL = tasks.get("email").toString()
                    val bundle = Bundle()
                    bundle.putString("name", NAME)
                    bundle.putString("phone", PHONE)
                    bundle.putString("email", EMAIL)

                    fragment.arguments = bundle
                    supportFragmentManager.beginTransaction().add(R.id.container, fragment).commit()
                }
        }


    }

    private fun inflateFragment(newInstance: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, newInstance)
        transaction.commit()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == permissionCode) {

            if (allPermissionGranted()) {
                //openCamera()

                setUpLocationListener()
            } else {

            }

        }
    }

//    private fun openCamera() {
//        val intent = Intent("android.media.action.IMAGE_CAPTURE")
//        startActivity(intent)
//    }

    private fun allPermissionGranted(): Boolean {
        for (item in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    item
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }

        return true
    }


}