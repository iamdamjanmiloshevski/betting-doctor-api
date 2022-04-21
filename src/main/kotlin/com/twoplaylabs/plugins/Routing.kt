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

package com.twoplaylabs.plugins

import com.twoplaylabs.controllers.*
import com.twoplaylabs.data.common.Message
import com.twoplaylabs.resources.BettingTips
import com.twoplaylabs.routes.*
import com.twoplaylabs.util.Constants
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.doctorBettingService() {
    val controller by inject<BettingTipsController>()
    val userController by inject<UserController>()
    val tokenController by inject<TokenController>()
    routing {
        bettingTipsController(controller)
        userController(userController,tokenController)
        tokenController(tokenController)
    }
}


fun Application.healthCheckService(){
        healthCheckRoutes()
}
fun Application.sportsAnalystService(){
    val controller by inject<TicketController>()
    routing {
        ticketsController(controller)
    }
}






