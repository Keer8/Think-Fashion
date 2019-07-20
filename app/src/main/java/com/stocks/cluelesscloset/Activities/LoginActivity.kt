package com.stocks.cluelesscloset.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.stocks.cluelesscloset.Endpoints.BASEURL
import com.stocks.cluelesscloset.Endpoints.CLIENT_ID
import com.stocks.cluelesscloset.Endpoints.NAME
import com.stocks.cluelesscloset.Endpoints.SECRET
import com.stocks.cluelesscloset.Model.UserModel
import com.stocks.cluelesscloset.POKO.ClientResponse
import com.stocks.cluelesscloset.R
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Activity that allows the user to log in or register.
 */
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (getSharedPreferences(getString(R.string.prefs), Context.MODE_PRIVATE).getString(getString(R.string.save_email), "") != "") {
            startActivity(Intent(this, OutfitActivity::class.java))
            finish()
        }

        auth_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                auth_title_view.text = getString(R.string.register)
                switch_text.text = getString(R.string.login)
                register_details.visibility = View.VISIBLE
            } else {
                auth_title_view.text = getString(R.string.login)
                switch_text.text = getString(R.string.register)
                register_details.visibility = View.GONE
            }
        }

        continue_button.setOnClickListener {
            if (auth_switch.isChecked) {
                registerUser(email_input.text.toString(),
                        password_input.text.toString(),
                        first_name_input.text.toString(),
                        last_name_input.text.toString())
            } else {
//                loginUser(email_input.text.toString(),
//                        password_input.text.toString())
                loginSecure(email_input.text.toString())
            }
        }
    }

    /**
     * @param username User's username.
     * @param password User's password.
     */
    private fun loginUser(username: String, password: String) {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        val userModel = retrofit.create(UserModel::class.java)
        userModel.getClientId(NAME, CLIENT_ID, SECRET, username).enqueue(object : Callback<ClientResponse> {
            override fun onResponse(call: Call<ClientResponse>, response: Response<ClientResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "made it this far", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, response.message().toString(), Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ClientResponse>?, t: Throwable?) {
                Toast.makeText(applicationContext, "webserver err", Toast.LENGTH_LONG).show()
            }

        })

    }

    /**
     * @param username User's username.
     * @param password User's password.
     * @param firstName User's first name.
     * @param lastName User's last name.
     */
    private fun registerUser(username: String, password: String, firstName: String, lastName: String) {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        val userModel = retrofit.create(UserModel::class.java)
        userModel.registerUser(username, password, firstName, lastName).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, "Successful register", Toast.LENGTH_LONG).show()
                    loginUser(username, password)
                } else {
                    Toast.makeText(applicationContext, "Invalid login", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.wtf("nooo", "It's one of those days 😭")
            }
        })
    }

    /**
     * Helper method to securely log in.
     * @param email User email.
     */
    private fun loginSecure(email: String) {
        val preferences = getSharedPreferences(getString(R.string.prefs), Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(getString(R.string.save_email), email.toLowerCase()).apply()

        startActivity(Intent(this, OutfitActivity::class.java))
        finish()
    }
}
