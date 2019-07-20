package com.stocks.cluelesscloset.Model

import com.stocks.cluelesscloset.POKO.AllOutfits
import com.stocks.cluelesscloset.POKO.Outfit
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ClothingModel {
    @Multipart
    @POST("/user/add_clothes")
    fun addArticle(@Part image: MultipartBody.Part,
                   @Part("name") fileName: RequestBody,
                   @Part("clothing_name") article_name: RequestBody,
                   @Part("clothing_type") articleType: RequestBody,
                   @Part("email") email: RequestBody,
                   @Part("water_resistant") waterResistant: Boolean,
                   @Part("warmthInputSlider") warmthRating: Int): Call<ResponseBody>

    @GET("/getclothes")
    fun getOutfit(@Query("email") token: String,
                  @Query("latitude") latitude: Double,
                  @Query("longitude") longitude: Double,
                  @Query("custom") custom: String = ""): Call<Outfit>

    @GET("/getallclothes")
    fun getAllOutfits(@Query("email") email: String): Call<AllOutfits>

}