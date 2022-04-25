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

package com.twoplaylabs.routes

import com.twoplaylabs.controllers.BettingTipsController
import com.twoplaylabs.data.BettingTip
import com.twoplaylabs.data.Status
import com.twoplaylabs.data.Team
import com.twoplaylabs.dummyResponse
import com.twoplaylabs.main
import com.twoplaylabs.modules.doctorbetting.installDoctorBettingModule
import com.twoplaylabs.util.Constants.BETTING_TIPS_ROUTE
import com.twoplaylabs.util.GsonUtil
import com.twoplaylabs.util.GsonUtil.deserializeListToJson
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.resources.*
import io.ktor.server.testing.*
import io.mockk.mockk
import org.bson.types.ObjectId
import org.junit.Test
import java.text.DateFormat
import java.util.*
import kotlin.test.assertEquals
import io.ktor.server.routing.*

/*
    Author: Damjan Miloshevski 
    Created on 24/04/2022
    Project: betting-doctor
*/
class BettingTipsRoutesKtTest {
    val controller = mockk<BettingTipsController>()

    @Test
    fun `get all betting tips returns success`() {
        testApplication {
            val client = createClient {
                install(ContentNegotiation) {
                    gson {
                        serializeNulls()
                        setDateFormat(DateFormat.FULL)
                        setPrettyPrinting()
                    }
                }
            }
            application { main() }
            routing {
               get("/api/v1/"){

               }
            }
            val response = client.get("/api/v1/")
            assertEquals(HttpStatusCode.OK, response.status)
           // assertEquals(dummyResponse,deserializeListToJson<List<BettingTip>>(response.body()) as List<BettingTip>)
        }
    }
}