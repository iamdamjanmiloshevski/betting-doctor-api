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

import com.auth0.jwt.interfaces.DecodedJWT
import com.twoplaylabs.auth.JWTHeader
import com.twoplaylabs.auth.JWTPayload
import java.util.*

/*
    Author: Damjan Miloshevski 
    Created on 09/07/2021
    Project: betting-doctor
*/
object JWTDecoder {
    fun decodeJWT(decodedJWT: DecodedJWT?): Pair<JWTHeader, JWTPayload> {
        decodedJWT?.let {
            try {
                val decoder = Base64.getDecoder()

                val header = String(decoder.decode(decodedJWT.header))
                val payload = String(decoder.decode(decodedJWT.payload))

                val jwtHeader = GsonUtil.deserialize(JWTHeader::class.java, header)
                val jwtPayload = GsonUtil.deserialize(JWTPayload::class.java, payload)

                return Pair(jwtHeader, jwtPayload)
            } catch (e: Exception) {
                throw Exception("Token is invalid!")
            }
        } ?: throw Exception("Please provide a valid token!")
    }
}