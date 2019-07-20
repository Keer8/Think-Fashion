package com.stocks.cluelesscloset.Activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import com.stocks.cluelesscloset.Endpoints.BASEURL
import com.stocks.cluelesscloset.Model.ClothingModel
import com.stocks.cluelesscloset.R
import kotlinx.android.synthetic.main.activity_new_clothes.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File


/**
 * Activity designated for user when they want to add a new article of clothing.
 */
class NewClothesActivity : AppCompatActivity() {
    /**
     * Request code for for image capture.
     */
    private val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    /**
     * Request code needed for permissions.
     */
    private val CODE_ALL = 5
    /**
     * App tag for logging.
     */
    private val APP_TAG = "New Clothes Activity"
    /**
     * File where future photo will be located.
     */
    private var photoFile: File? = null
    /**
     * Photo file name.
     */
    private var photoFileName: String = "img_url"
    /**
     * Bitmap of image.
     */
    private var bitmap: Bitmap? = null
    /**
     * Determine whether or not items can be saved yet.
     */
    private var go = false
    /**
     * Warmth level of clothes. This defaults to 1.
     */
    private var warmthLevel = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_clothes)
        gibePermission()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        warmth_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                warmthLevel = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        val directory = File(Environment.getExternalStorageDirectory().toString() + File.separator + "images")
        directory.mkdirs()

        apparel_category_box.adapter = ArrayAdapter<String>(applicationContext,
                android.R.layout.simple_spinner_dropdown_item,
                arrayOf(getString(R.string.accessories),
                        getString(R.string.tops),
                        getString(R.string.bottom)))

        cancel_button.setOnClickListener {
            finish()
        }

        save_button.setOnClickListener {
            if (go && photoFile != null) {

                val clothingType = apparel_category_box.selectedItem.toString()
                val articleName = new_apparel_box.text.toString()
                val clothingModel = Retrofit.Builder()
                        .baseUrl(BASEURL)
                        .addConverterFactory(MoshiConverterFactory.create())
                        .build()
                        .create(ClothingModel::class.java)

                val preferences = getSharedPreferences(getString(R.string.prefs), Context.MODE_PRIVATE)
                val email = preferences.getString(getString(R.string.save_email), "")

                Log.wtf("fake news", email)

                val reqFile = RequestBody.create(MediaType.parse("image/*"), photoFile as File)
                val body = MultipartBody.Part.createFormData(photoFileName, photoFile?.name, reqFile)
                val name = RequestBody.create(MediaType.parse("text/plain"), "img_url")
                val betterArticleName = RequestBody.create(MediaType.parse("text/plain"), articleName)
                val betterType = RequestBody.create(MediaType.parse("text/plain"), clothingType)
                val betterEmail = RequestBody.create(MediaType.parse("text/plain"), email)
                clothingModel.addArticle(
                        body,
                        name,
                        betterArticleName,
                        betterType,
                        betterEmail,
                        water_resistant_box.isChecked,
                        warmthLevel)
                        .enqueue(object : Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                                // pizza doge
                            }

                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                finish()
                            }

                        })
            } else {
                Toast.makeText(applicationContext, "Finish filling out things!", Toast.LENGTH_LONG).show()
            }
        }

        photo_button.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // Create a File reference to access to future access
            photoFile = getPhotoFileUri(photoFileName)

            // wrap File object into a content provider
            // required for API >= 24
            // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
            val fileProvider = FileProvider.getUriForFile(this, "com.stocks.fileprovider", photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }


        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                photoFile?.let {
                    bitmap = BitmapFactory.decodeFile(it.toString())
                    // RESIZE BITMAP, see section below
                    // Load the taken image into a preview
                    val ivPreview = photo as ImageView
                    ivPreview.setImageBitmap(bitmap)
                    go = true

                }
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Returns the File for a photo stored on disk given the fileName
     * @param fileName Name of file to get URI from.
     * @return File descriptor to URI.
     */
    fun getPhotoFileUri(fileName: String): File? {
        if (isExternalStorageAvailable()) {
            val mediaStorageDir = File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG)

            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d(APP_TAG, "failed to create directory")
            }

            return File(mediaStorageDir.path + File.separator + fileName)
        }
        return null
    }

    /**
     * Returns true if external storage for photos is available.
     * @return True if storage for photos is available.
     */
    private fun isExternalStorageAvailable(): Boolean {
        val state = Environment.getExternalStorageState()
        return state == Environment.MEDIA_MOUNTED
    }

    /**
     * What you yell to the sky when you need all the permissions.
     */
    private fun gibePermission() {
        val permissionsArray = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(
                this,
                permissionsArray,
                CODE_ALL
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        for (permission in grantResults) {
            if (permission != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "We don't like your kind here", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}
