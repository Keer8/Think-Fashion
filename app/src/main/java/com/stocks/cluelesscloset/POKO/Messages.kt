package com.stocks.cluelesscloset.POKO

/**
 * POKO for serialization of registration data.
 */
public data class RegisterData(val email: String, val password: String, val first_name: String, val last_name: String)

/**
 * POKO for serialization of login data.
 */
public data class LoginData(val email: String, val password: String)

/**
 * POKO for serialization of article data.
 */
public data class AddArticle(val token: String, val article_name: String, val type: String)

