package com.twoplaylabs.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.twoplaylabs.data.User
import java.util.*
import java.util.concurrent.TimeUnit
import com.auth0.jwt.interfaces.DecodedJWT

/*
    Author: Damjan Miloshevski 
    Created on 23/06/2021
    Project: betting-doctor
    © 2Play Technologies  2021. All rights reserved
*/
class JWTService {
    private val issuer = "2playtech"
    private val realm = "2playtech.betting-doctor"
    private val jwtSecret = "mocked-jwt-secret"
    private val algorithm = Algorithm.HMAC512(jwtSecret)

    private val verifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build()

    fun generateToken(expiresAt: Date, user: User): String = JWT.create()
        .withSubject("Authentication")
        .withAudience("mocked-jwt-audience")
        .withIssuer(issuer)
        .withClaim("_id", user._id)
        .withExpiresAt(expiresAt)
        .sign(algorithm)

    fun generateRefreshToken(expiresAt: Date, user: User): String = JWT.create()
        .withIssuer(issuer)
        .withAudience("mocked-jwt-audience")
        .withJWTId("06095790-3e99-420b-97d0-2ed9ff2a7854")
        .withClaim(
            "user", mapOf(
                "id" to user._id,
                "isAccountVerified" to user.isAccountVerified
            )
        )
        .withExpiresAt(expiresAt)
        .sign(algorithm)

    fun realm() = realm
    fun verifier(): JWTVerifier = verifier

    fun expiresAt(): Date = Date(TimeUnit.HOURS.toMillis(1))

    fun decodeJWT(token: String): DecodedJWT = JWT.decode(token)
}