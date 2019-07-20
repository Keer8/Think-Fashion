package com.stocks.cluelesscloset.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import com.stocks.cluelesscloset.Adapters.AccessoryAdapter
import com.stocks.cluelesscloset.Adapters.BottomAdapter
import com.stocks.cluelesscloset.Adapters.TopAdapter
import com.stocks.cluelesscloset.Endpoints.BASEURL
import com.stocks.cluelesscloset.Model.ClothingModel
import com.stocks.cluelesscloset.POKO.AllOutfits
import com.stocks.cluelesscloset.R
import kotlinx.android.synthetic.main.activity_add_clothes.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Activity that shows the user their inventoried items and allows them to add more if they'd
 * like to.
 */
class AddClothesActivity : AppCompatActivity() {
    /**
     * Adapter to hold user accessories.
     */
    var accessoriesAdapter: AccessoryAdapter? = null
    /**
     * Adapter to hold user tops.
     */
    var topAdapter: TopAdapter? = null
    /**
     * Adapter to hold user bottoms.
     */
    var bottomAdapter: BottomAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_clothes)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        val retrofit = Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        val preferences = getSharedPreferences(getString(R.string.prefs), Context.MODE_PRIVATE)

        val email = preferences.getString(getString(R.string.save_email), "")

        val clothingModel = retrofit.create(ClothingModel::class.java)
        clothingModel.getAllOutfits(email).enqueue(object : Callback<AllOutfits> {
            override fun onResponse(call: Call<AllOutfits>, response: Response<AllOutfits>) {

                response.body()?.accessories_list?.let {
                    accessoriesAdapter = AccessoryAdapter(it, applicationContext)

                    accessories_list.layoutManager = LinearLayoutManager(applicationContext)
                    accessories_list.adapter = accessoriesAdapter

                    accessories_card.setOnClickListener {
                        if (accessories_list.visibility == GONE) {
                            accessories_list.visibility = VISIBLE
                            rotateAnimationUtils(accessories_arrow, true)
                        } else {
                            accessories_list.visibility = GONE
                            rotateAnimationUtils(accessories_arrow, false)
                        }
                    }
                }

                response.body()?.tops_list?.let {
                    topAdapter = TopAdapter(it, applicationContext)

                    tops_list.layoutManager = LinearLayoutManager(applicationContext)
                    tops_list.adapter = topAdapter

                    tops_card.setOnClickListener {
                        if (tops_list.visibility == GONE) {
                            tops_list.visibility = VISIBLE
                            rotateAnimationUtils(tops_arrow, true)
                        } else {
                            tops_list.visibility = GONE
                            rotateAnimationUtils(tops_arrow, false)
                        }
                    }
                }

                response.body()?.bottoms_list?.let {
                    bottomAdapter = BottomAdapter(it, applicationContext)

                    bottoms_list.layoutManager = LinearLayoutManager(applicationContext)
                    bottoms_list.adapter = bottomAdapter

                    bottoms_card.setOnClickListener {
                        if (bottoms_list.visibility == GONE) {
                            bottoms_list.visibility = VISIBLE
                            rotateAnimationUtils(bottoms_arrow, true)
                        } else {
                            bottoms_list.visibility = GONE
                            rotateAnimationUtils(bottoms_arrow, false)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<AllOutfits>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

        add_accessories.setOnClickListener {
            startActivity(Intent(this, NewClothesActivity::class.java))
        }

        add_tops.setOnClickListener {
            startActivity(Intent(this, NewClothesActivity::class.java))
        }

        add_bottoms.setOnClickListener {
            startActivity(Intent(this, NewClothesActivity::class.java))
        }
    }

    /**
     * Helper method: Allows for quickly animating the rotation arrow.
     * @param view View to rotate.
     * @param reset Boolean to determine if the view needs to be reset or not.
     */
    private fun rotateAnimationUtils(view: ImageView, reset: Boolean) {
        if (reset) {
            view.rotation = 0f
            view.animate().rotationBy(90f).setDuration(150).start()
        } else {
            view.rotation = 90f
            view.animate().rotationBy(-90f).setDuration(150).start()
        }

    }
}
