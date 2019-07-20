package com.stocks.cluelesscloset.Model

import com.stocks.cluelesscloset.POKO.ClientResponse
import com.stocks.cluelesscloset.POKO.TokenResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

public interface UserModel {
    @POST("/clients")
    @FormUrlEncoded
    fun getClientId(@Field("name") name: String,
                    @Field("id") id: String,
                    @Field("secret") secret: String,
                    @Field("user_id") userId: String): Call<ClientResponse>

    @GET("/oauth2/authorize")
    fun authorizeUser(@Query("email") email: String,
                      @Query("password") password: String): Call<TokenResponse>

    // post authorization code
    @POST("/oauth2/authorize")
    @FormUrlEncoded
    fun decideUser(@Field("email") email: String,
                   @Field("password") password: String): Call<TokenResponse>

    @POST("/oauth2/token")
    @FormUrlEncoded
    fun getTokenForUser(@Field("email") email: String,
                        @Field("password") password: String): Call<TokenResponse>

    @POST("/users")
    @FormUrlEncoded
    fun registerUser(
            @Field("email") email: String,
            @Field("password") password: String,
            @Field("firstName") firstName: String,
            @Field("lastName") lastName: String): Call<ResponseBody>
}