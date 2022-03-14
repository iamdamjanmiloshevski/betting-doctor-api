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

package com.twoplaylabs

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.twoplaylabs.modules.doctorbetting.doctorBettingModule
import com.twoplaylabs.modules.sportsanalyst.sportsAnalystModule
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.twoplaylabs.util.AuthUtil.retrieveFirebaseCredentials
import com.twoplaylabs.util.Constants
import com.twoplaylabs.util.Constants.PORT
import com.twoplaylabs.util.toInputStream
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.routing.*
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.title


fun main() {
    val httpPort = System.getenv(PORT)?.toInt() ?: 8080
    embeddedServer(Netty, port = httpPort) {
        install(CallLogging)
        install(DefaultHeaders)
        healthCheck()
        doctorBettingModule()
        sportsAnalystModule()
    }.start(wait = true)
}

private fun Application.healthCheck() {
    routing {
        get("/") {
            val name = "Betting Doctor"
            call.respondHtml(HttpStatusCode.OK) {
                head {
                    title { +name }
                }
                body {
                    h1 {
                        +"Doctor Betting API health check: Success"
                    }
                }
            }
        }
        get("/health-check") {
            val name = "Betting Doctor"
            call.respondHtml(HttpStatusCode.OK) {
                head {
                    title { +name }
                }
                body {
                    h1 {
                        +"Health check: Success"
                    }
                }
            }
        }
    }
}

fun configureFirebase() {
    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(retrieveFirebaseCredentials().toInputStream()))
        .setStorageBucket(System.getenv(Constants.FIREBASE_STORAGE_BUCKET_URL))
        .build()
    FirebaseApp.initializeApp(options)
}