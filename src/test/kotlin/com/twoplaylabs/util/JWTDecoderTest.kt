package com.twoplaylabs.util

import com.twoplaylabs.auth.JWTHeader
import com.twoplaylabs.auth.JWTPayload
import com.twoplaylabs.auth.JWTService
import com.twoplaylabs.auth.JWTUser
import junit.framework.Assert.assertEquals
import org.junit.Assert
import org.junit.Test

/*
    Author: Damjan Miloshevski 
    Created on 09/07/2021
    Project: betting-doctor
    © 2Play Technologies  2021. All rights reserved
*/
class JWTDecoderTest {
    private val mockJWTHeader = JWTHeader("HS512", "JWT")
    private val mockPayload = JWTPayload(
        "Mocked Audience",
        2682000,
        "mocked-issuer",
        "06095790-3e99-420b-97d0-2ed9ff2a7854",
        JWTUser("test@example.com", "60e811ed41dc79357a50d745", true)
    )
    private val mockToken =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJNb2NrZWQgQXVkaWVuY2UiLCJpc3MiOiJtb2NrZWQtaXNzdWVyIiwiZXhwIjoyNjgyMDAwLCJ1c2VyIjp7ImVtYWlsIjoidGVzdEBleGFtcGxlLmNvbSIsImlkIjoiNjBlODExZWQ0MWRjNzkzNTdhNTBkNzQ1IiwiaXNBY2NvdW50VmVyaWZpZWQiOnRydWV9LCJqdGkiOiIwNjA5NTc5MC0zZTk5LTQyMGItOTdkMC0yZWQ5ZmYyYTc4NTQifQ.l7ipTXkcVlTqvFTG9gFUiiU2vm3nqeGv3QIM0RwVeA8fGOMD6wQUesQ-TCX9yULHJ80x3Wnwh8gYK8ZOf07uXg"
    private val mockJWTService = JWTService()

    @Test
    fun decodeJWT_tokenCorrect_returnsTrue() {
        assertEquals(Pair(mockJWTHeader, mockPayload), JWTDecoder.decodeJWT(mockJWTService.decodeJWT(mockToken)))
    }

    @Test(expected = Exception::class)
    fun decodeHWT_tokenNull_throwsException() {
        JWTDecoder.decodeJWT(null)
        Assert.fail("Please provide a valid token!")
    }

    @Test(expected = Exception::class)
    fun decodeHWT_tokenIncorrect_throwsException() {
        JWTDecoder.decodeJWT(mockJWTService.decodeJWT("mockToken"))
        Assert.fail("Token is invalid!")
    }
}