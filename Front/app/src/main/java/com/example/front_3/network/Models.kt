package com.example.front_3.network

// Lo que enviamos al servidor (Request Bodys)
data class RegisterReq(val name: String, val email: String, val password: String)
data class LoginReq(val email: String, val password: String)

// Lo que recibimos del servidor (Response Bodys)
data class GeneralRes(val message: String?, val error: String?)
data class AuthRes(val message: String?, val error: String?, val user: User?)
data class User(val id: Int, val name: String, val email: String)