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

package com.twoplaylabs.controllers

import com.twoplaylabs.common.Message
import com.twoplaylabs.data.BettingTip
import com.twoplaylabs.data.Ticket
import com.twoplaylabs.data.User
import com.twoplaylabs.data.UserRole
import com.twoplaylabs.repository.TicketsRepository
import com.twoplaylabs.util.BettingTipManager.fetchTeamLogosAndUpdateBettingTip
import com.twoplaylabs.util.Constants
import com.twoplaylabs.util.Constants.ID_ROUTE
import com.twoplaylabs.util.Constants.PARAM_DATE
import com.twoplaylabs.util.Constants.PARAM_ID
import com.twoplaylabs.util.Constants.SEARCH_ROUTE
import com.twoplaylabs.util.Constants.TICKETS_ROUTE
import com.twoplaylabs.util.toDateFromQueryParam
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.bson.types.ObjectId
import java.util.*

/*
    Author: Damjan Miloshevski 
    Created on 25/12/2021
    Project: betting-doctor
*/
fun Route.bettingTicketController(repository: TicketsRepository) {
    route(TICKETS_ROUTE) {
        with(repository) {
            authenticate(System.getenv(Constants.AUTH_CONFIG_ADMIN)) {
                createTicket(this@with)
                updateTicket(this@with)
                deleteTicket(this@with)
                dropTickets(this@with)
            }
            getTickets(this)
            findTicketByDate(this)
            getTicketById(this)
        }
    }
}

fun Route.createTicket(repository: TicketsRepository) {
    post {
        val principal = call.principal<User>()
        if (principal?.role != UserRole.ADMIN) {
            call.respond(
                HttpStatusCode.Unauthorized,
                Message(Constants.INSUFFICIENT_PERMISSIONS, HttpStatusCode.Unauthorized.value)
            )
        }else{
            val ticket = call.receive<Ticket>()
            try {
                val ticketId = ObjectId()
                val updatedTips = mutableListOf<BettingTip>()
                for (tip in ticket.tips) {
                    fetchTeamLogosAndUpdateBettingTip(tip, callback = {
                        updatedTips.add(it)
                    })
                }
                ticket.tips = updatedTips
                for (tip in ticket.tips) {
                    tip._id = ObjectId().toString()
                    tip.ticketId = ticketId.toString()
                }
                ticket._id = ticketId.toString()
                ticket.date = Date()
                repository.insertTicket(ticket)
                call.respond(HttpStatusCode.Created, ticket)
            } catch (e: Throwable) {
                application.log.error(e.message)
                call.respond(
                    HttpStatusCode.BadRequest,
                    Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
                )
            }
        }
    }
}

fun Route.findTicketByDate(repository: TicketsRepository) {
    get(SEARCH_ROUTE) {
        val dateParam = call.request.queryParameters[PARAM_DATE]
        try {
            dateParam ?: call.respond(
                HttpStatusCode.BadRequest,
                Message("Please provide a valid date", HttpStatusCode.BadRequest.value)
            )
            dateParam?.let { gmtDate ->
                val date = gmtDate.toDateFromQueryParam()
                val ticket = repository.findTicketByDate(date)
                ticket?.let { call.respond(HttpStatusCode.OK, ticket) } ?: call.respond(
                    HttpStatusCode.NotFound,
                    Message("Apologies we have nothing for you", HttpStatusCode.NoContent.value, date)
                )
            } ?: call.respond(HttpStatusCode.InternalServerError)

        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

fun Route.getTicketById(repository: TicketsRepository) {
    get(ID_ROUTE) {
        val id = call.parameters[PARAM_ID] ?: call.respond(
            HttpStatusCode.BadRequest,
            Message("Please provide a valid id", HttpStatusCode.BadRequest.value)
        )
        try {
            val ticket = repository.findTicketById(id.toString())
            ticket?.let { call.respond(it) } ?: call.respond(HttpStatusCode.NotFound)
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

fun Route.getTickets(repository: TicketsRepository) {
    get {
        try {
            val items = repository.findAllTickets()
            call.respond(items)
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

fun Route.updateTicket(repository: TicketsRepository) {
    put {
        val principal = call.principal<User>()
        if (principal?.role != UserRole.ADMIN) {
            call.respond(
                HttpStatusCode.Unauthorized,
                Message(Constants.INSUFFICIENT_PERMISSIONS, HttpStatusCode.Unauthorized.value)
            )
        }else{
            val ticket = call.receive<Ticket>()
            try {
                val updatedTips = mutableListOf<BettingTip>()
                for (tip in ticket.tips) {
                    if(tip._id == null) tip._id = ObjectId().toString()
                    fetchTeamLogosAndUpdateBettingTip(tip, callback = {
                        updatedTips.add(it)
                    })
                }
                ticket.tips = updatedTips
                val updatedCount = repository.updateTicket(ticket)
                application.log.debug("Updated documents $updatedCount")
                if (updatedCount > 0) {
                    call.respond(HttpStatusCode.Accepted, ticket)
                } else call.respond(HttpStatusCode.NoContent)
            } catch (e: Throwable) {
                application.log.error(e.message)
                call.respond(
                    HttpStatusCode.BadRequest,
                    Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
                )
            }
        }
    }
}

fun Route.deleteTicket(repository: TicketsRepository) {
    delete(ID_ROUTE) {
        val id = call.parameters[Constants.PARAM_ID] ?: return@delete call.respond(
            HttpStatusCode.BadRequest,
            Message(Constants.MISSING_ID, HttpStatusCode.BadRequest.value)
        )
        try {
            val deletedCount = repository.deleteTicket(id)
            application.log.debug("Deleted items $deletedCount")
            if (deletedCount > 0) {
                call.respond(HttpStatusCode.OK, Message(Constants.SUCCESS, HttpStatusCode.OK.value))
            } else call.respond(HttpStatusCode.NotFound, Message("Content not found", HttpStatusCode.NotFound.value))
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}

fun Route.dropTickets(repository: TicketsRepository) {
    delete {
        try {
            val deletedCount = repository.deleteAllTickets()
            application.log.debug("Deleted items $deletedCount")
            if (deletedCount > 0) {
                call.respond(HttpStatusCode.OK)
            } else call.respond(HttpStatusCode.NoContent)
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}