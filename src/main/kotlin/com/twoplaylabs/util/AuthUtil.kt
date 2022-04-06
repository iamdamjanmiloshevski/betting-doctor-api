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

package com.twoplaylabs.util

import com.twoplaylabs.data.auth.AccessToken
import com.twoplaylabs.auth.JWTService
import com.twoplaylabs.data.User
import com.twoplaylabs.util.Constants.FIREBASE_AUTH_PROVIDER_x509_CERT_URL
import com.twoplaylabs.util.Constants.FIREBASE_AUTH_URI
import com.twoplaylabs.util.Constants.FIREBASE_CLIENT_EMAIL
import com.twoplaylabs.util.Constants.FIREBASE_CLIENT_ID
import com.twoplaylabs.util.Constants.FIREBASE_CLIENT_x509_CERT_URL
import com.twoplaylabs.util.Constants.FIREBASE_PRIVATE_KEY
import com.twoplaylabs.util.Constants.FIREBASE_PRIVATE_KEY_ID
import com.twoplaylabs.util.Constants.FIREBASE_PROJECT_ID
import com.twoplaylabs.util.Constants.FIREBASE_TOKEN_URI
import com.twoplaylabs.util.Constants.FIREBASE_TYPE
import org.koin.java.KoinJavaComponent.inject
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import java.util.*

/*
    Author: Damjan Miloshevski 
    Created on 15/09/2021
    Project: betting-doctor
*/
object AuthUtil {
    fun String.generateWelcomeUrl() = System.getenv(Constants.API_BASE_URL).plus("/users/verify/${this}")
    private val jwtService by inject<JWTService>(JWTService::class.java)

    fun generateAccessToken(
        user: User,
        expiresAt: Date = jwtService.expiresAt()
    ): AccessToken {
        val expiresAtInMillis = expiresAt.time
        return AccessToken(
            jwtService.generateToken(expiresAt, user),
            UUID.randomUUID().toString(),
            expiresAtInMillis
        )
    }
}