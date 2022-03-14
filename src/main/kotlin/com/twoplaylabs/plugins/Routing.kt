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

import com.twoplaylabs.auth.JWTService
import com.twoplaylabs.controllers.bettingTicketRouter
import com.twoplaylabs.repository.BettingTipsRepository
import com.twoplaylabs.repository.UsersRepository
import com.twoplaylabs.controllers.bettingTipsRouter
import com.twoplaylabs.controllers.usersRouter
import com.twoplaylabs.repository.TicketsRepository
import com.twoplaylabs.repository.TokensRepository
import io.ktor.routing.*
import io.ktor.application.*

fun Application.doctorBettingService(
    bettingTipsRepository: BettingTipsRepository,
    usersRepository: UsersRepository,
    tokensRepository: TokensRepository,
    jwtService: JWTService
) {
    routing {
        bettingTipsRouter(bettingTipsRepository)
        usersRouter(usersRepository, tokensRepository,jwtService)

    }
}

fun Application.sportsAnalystService(
    ticketsRepository: TicketsRepository,
    usersRepository: UsersRepository,
    jwtService: JWTService
){
    routing {
        bettingTicketRouter(ticketsRepository)
    }
}






