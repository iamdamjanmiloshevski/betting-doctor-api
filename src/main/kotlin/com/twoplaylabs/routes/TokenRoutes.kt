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

import com.twoplaylabs.common.authorize
import com.twoplaylabs.controllers.TokenController
import com.twoplaylabs.data.RefreshToken
import com.twoplaylabs.data.common.Message
import com.twoplaylabs.resources.Tokens
import com.twoplaylabs.util.Constants
import com.twoplaylabs.util.Constants.TOKEN_REJECT_ROUTE
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*



/*
    Author: Damjan Miloshevski 
    Created on 06/04/2022
    Project: betting-doctor
*/
fun Routing.tokenController(controller: TokenController){
        authenticate(System.getenv(Constants.AUTH_CONFIG_ADMIN)) {
            rejectToken(controller)
            getAllTokens(controller)
        }
}

private fun Route.getAllTokens(controller: TokenController) {
    get<Tokens> {
        call.authorize()
        try {
            val tokens = controller.findAllTokens()
            call.respond(HttpStatusCode.OK,tokens)
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

private fun Route.rejectToken(controller: TokenController) {
    post<Tokens.Reject>{
        val refreshToken = call.receive<RefreshToken>()
        try {
            val userEmail = refreshToken.userEmail
            val token = refreshToken.token
            val modifiedCount = controller.deleteToken(userEmail, token)
            if (modifiedCount > 0) {
                call.respond(
                    HttpStatusCode.OK,
                    Message(
                        Constants.TOKEN_DISABLED_SUCCESS,
                        HttpStatusCode.OK.value
                    )
                )
            } else call.respond(HttpStatusCode.Unauthorized)
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}