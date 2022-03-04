/*
 * MIT License
 *
 * Copyright (c) 2021 2Play Technologies Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.twoplaylabs.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.twoplaylabs.data.User
import java.util.*
import java.util.concurrent.TimeUnit
import com.auth0.jwt.interfaces.DecodedJWT
import com.twoplaylabs.util.Constants.AUTHENTICATION
import com.twoplaylabs.util.Constants.ID
import com.twoplaylabs.util.Constants.IS_ACCOUNT_VERIFIED
import com.twoplaylabs.util.Constants.JWT_AUDIENCE
import com.twoplaylabs.util.Constants.JWT_SECRET
import com.twoplaylabs.util.Constants.ROLE
import com.twoplaylabs.util.Constants.USER

/*
    Author: Damjan Miloshevski 
    Created on 23/06/2021
    Project: betting-doctor
*/
class JWTService {
    private val issuer = "2playtech"
    private val realm = "2playtech.betting-doctor"
    private val jwtSecret = System.getenv(JWT_SECRET)
    private val audience = System.getenv(JWT_AUDIENCE)
    private val algorithm = Algorithm.HMAC512(jwtSecret)

    private val verifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .withAudience(audience)
        .build()

    fun generateToken(expiresAt: Date, user: User): String = JWT.create()
        .withSubject(AUTHENTICATION)
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim(ID,user._id)
        .withClaim(ROLE,user.role.name)
        .withExpiresAt(expiresAt)
        .sign(algorithm)

    fun realm() = realm
    fun verifier(): JWTVerifier = verifier
    fun audience(): String = audience

    fun expiresAt(): Date = Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1))

}