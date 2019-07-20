package com.stocks.cluelesscloset.POKO

import com.squareup.moshi.Json

/**
 * POKO for deserialization of Tokens.
 */
public data class TokenResponse(val token: String)

/**
 * POKO for deserialization of basic response.
 */
public data class BaseResponse(val message: String)

/**
 * POKO for deserialization of mass quantities of full outfits.
 */
@Json(name = "")
public data class AllOutfits(val accessories_list: MutableList<Accessory>, val tops_list: MutableList<Top>, val bottoms_list: MutableList<Bottom>)

/**
 * POKO for deserialization of basic outfit.
 */
public data class Outfit(val accessory: Accessory,
                         val tops: Top,
                         val bottoms: Bottom)

/**
 * POKO for deserialization of accessories.
 */
public data class Accessory(val _id: String, val name: String, val clothing_type: String, val image: String, val owner_email: String, val __v: Int, val rain_resistant: Boolean, val warmth: Int)

/**
 * POKO for deserialization of tops.
 */
public data class Top(val _id: String, val name: String, val clothing_type: String, val image: String, val owner_email: String, val __v: Int, val rain_resistant: Boolean, val warmth: Int)

/**
 * POKO for deserialization of bottoms.
 */
public data class Bottom(val _id: String, val name: String, val clothing_type: String, val image: String, val owner_email: String, val __v: Int, val rain_resistant: Boolean, val warmth: Int)

/**
 * POKO for deserialization of basic response.
 */
public data class ClientResponse(val message: String, val data: String)
