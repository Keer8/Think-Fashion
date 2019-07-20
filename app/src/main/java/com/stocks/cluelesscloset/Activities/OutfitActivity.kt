package com.stocks.cluelesscloset.Activities

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import com.stocks.cluelesscloset.Endpoints.BASEURL
import com.stocks.cluelesscloset.Fragments.SearchFragment
import com.stocks.cluelesscloset.Model.ClothingModel
import com.stocks.cluelesscloset.POKO.Outfit
import com.stocks.cluelesscloset.R
import kotlinx.android.synthetic.main.activity_outfit.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


/**
 * Activity the user reaches after they login/register.
 * The user gets a suggested outfit based on what the a number of factors.
 * They also have the option to query for another outfit, inventory new outfits, or specify their
 * tastes in outfits.
 */
class OutfitActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    /**
     * Reference to Google API client.
     */
    private var apiClient: GoogleApiClient? = null
    /**
     * Reference to current location.
     */
    private var currentLocation: Location? = null
    /**
     * Fine location permissions request integer.
     */
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    /**
     * Tag reference to class.
     */
    private val TAG = "OutfitActivity"
    /**
     * Reference to search fragment.
     */
    private val searchFragment: SearchFragment = SearchFragment()

    private var email: String? = ""

    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0

    override fun onConnected(p0: Bundle?) {
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient)
        if (currentLocation != null) {
            latitude = currentLocation?.latitude
            longitude = currentLocation?.longitude
            getMeOneOutfitPls()
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.wtf(TAG, "pls dont suspend thank")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.wtf(TAG, "This is why I cry every night")
    }

    var preferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_outfit)

        preferences = getSharedPreferences(getString(R.string.prefs), Context.MODE_PRIVATE)

        email = preferences?.getString(getString(R.string.save_email), "")

        val permissionsArray = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        setContentView(R.layout.activity_outfit)
        searchFragment.searchCompleteListener = object : SearchFragment.SearchCompleteListener {
            override fun searchCompleted(s: String) {
                getMeOneOutfitPls(s)
            }
        }

        search_outfit_button.setOnClickListener {
            searchFragment.show(supportFragmentManager, "partay")
        }

        gib_new_outfit_button.setOnClickListener {
            getMeOneOutfitPls()
        }

        add_clothes_button.setOnClickListener {
            startActivity(Intent(this, AddClothesActivity::class.java))
        }

        ActivityCompat.requestPermissions(
                this,
                permissionsArray,
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
        )

        buildGoogleApiClient()
        apiClient?.connect()
    }

    /**
     * Builds Google API client.
     */
    private fun buildGoogleApiClient() {
        apiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getMeOneOutfitPls()
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Toast.makeText(this, "hi friend pls let us have location, thank", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "FINE DON'T USE OUR APP", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun getMeOneOutfitPls(custom: String = "") {
        val immutableEmail = getImmutableEmail()

        Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build().create(ClothingModel::class.java).getOutfit(immutableEmail, latitude as Double, longitude as Double, custom)
                .enqueue(object : Callback<Outfit> {
                    override fun onFailure(call: Call<Outfit>?, t: Throwable?) {
                        Log.wtf("no", "we failed again")
                    }

                    override fun onResponse(call: Call<Outfit>, response: Response<Outfit>) {
                        if (response.isSuccessful) {
                            Log.wtf("gteclothes", "image: ${response.body()?.accessory?.image} - name: ${response.body()?.accessory?.name}")
                            accessory_text.text = response.body()?.accessory?.name

                            Picasso.with(applicationContext)
                                    .load("$BASEURL/clothes_images/${response.body()?.accessory?.image}")
                                    .into(accessories_image)

                            top_text.text = response.body()?.tops?.name

                            Picasso.with(applicationContext)
                                    .load("$BASEURL/clothes_images/${response.body()?.tops?.image}")
                                    .into(top_image)

                            bottom_text.text = response.body()?.bottoms?.name

                            Picasso.with(applicationContext)
                                    .load("$BASEURL/clothes_images/${response.body()?.bottoms?.image}")
                                    .into(bottom_image)
                        } else {
                            Toast.makeText(applicationContext, "FAILURE ${response.message()} code: ${response.code()}", Toast.LENGTH_LONG).show()
                        }
                        apiClient?.disconnect()
                    }

                })
    }

    private fun getImmutableEmail(): String {
        return if (email != null) {
            email as String
        } else {
            ""
        }
    }
}



