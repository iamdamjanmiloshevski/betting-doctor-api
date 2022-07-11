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


import com.twoplaylabs.modules.doctorbetting.installDoctorBettingModule
import com.twoplaylabs.modules.sportsanalyst.installSportsAnalystModule
import com.twoplaylabs.plugins.configureDependencyInjection
import com.twoplaylabs.plugins.healthCheckService
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.twoplaylabs.util.Constants.PORT
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.resources.*


fun main() {
    val httpPort = System.getenv(PORT)?.toInt() ?: 8080
    embeddedServer(Netty, port = httpPort) {
        install(CallLogging)
        install(Resources)
        configureDependencyInjection()
        healthCheckService()
        installDoctorBettingModule()
        installSportsAnalystModule()
    }.start(wait = true)
}

