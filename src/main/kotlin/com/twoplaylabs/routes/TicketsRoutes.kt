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

import com.twoplaylabs.controllers.*
import com.twoplaylabs.data.*
import com.twoplaylabs.data.common.Message
import com.twoplaylabs.util.BettingTipManager
import com.twoplaylabs.util.Constants
import com.twoplaylabs.util.toDateFromQueryParam
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.bson.types.ObjectId
import org.koin.ktor.ext.inject

/*
    Author: Damjan Miloshevski 
    Created on 05/04/2022
    Project: betting-doctor
*/
fun Route.ticketsController(controller: TicketController){
    val bettingTipsManager by inject<BettingTipManager>()
    route(Constants.TICKETS_ROUTE) {
        authenticate(System.getenv(Constants.AUTH_CONFIG_ADMIN)) {
            createTicket(bettingTipsManager, controller)
            updateTicket(bettingTipsManager, controller)
            deleteTicketById(controller)
            deleteAllTickets(controller)
        }
        getAllTickets(controller)
        searchTicketByDate(controller)
        findTicketById(controller)
    }
}

private fun Route.findTicketById(controller: TicketController) {
    get(Constants.ID_ROUTE) {
        val id = call.parameters[Constants.PARAM_ID] ?: call.respond(
            HttpStatusCode.BadRequest,
            Message("Please provide a valid id", HttpStatusCode.BadRequest.value)
        )
        try {
            val ticket = controller.findTicketById(id.toString())
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

private fun Route.searchTicketByDate(controller: TicketController) {
    get(Constants.SEARCH_ROUTE) {
        val dateParam = call.request.queryParameters[Constants.PARAM_DATE]
        try {
            dateParam ?: call.respond(
                HttpStatusCode.BadRequest,
                Message("Please provide a valid date", HttpStatusCode.BadRequest.value)
            )
            dateParam?.let { gmtDate ->
                val date = gmtDate.toDateFromQueryParam()
                val ticket = controller.findTicketByDate(date)
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

private fun Route.getAllTickets(controller: TicketController) {
    get {
        try {
            val items = controller.findAllTickets()
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

private fun Route.deleteAllTickets(controller: TicketController) {
    delete {
        try {
            val deletedCount = controller.deleteAllTickets()
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

private fun Route.deleteTicketById(controller: TicketController) {
    delete(Constants.ID_ROUTE) {
        val id = call.parameters[Constants.PARAM_ID] ?: return@delete call.respond(
            HttpStatusCode.BadRequest,
            Message(Constants.MISSING_ID, HttpStatusCode.BadRequest.value)
        )
        try {
            val deletedCount = controller.deleteTicket(id)
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

private fun Route.updateTicket(
    bettingTipsManager: BettingTipManager,
    controller: TicketController
) {
    put {
        val principal = call.principal<User>()
        if (principal?.role != UserRole.ADMIN) {
            call.respond(
                HttpStatusCode.Unauthorized,
                Message(Constants.INSUFFICIENT_PERMISSIONS, HttpStatusCode.Unauthorized.value)
            )
        } else {
            val ticket = call.receive<Ticket>()
            try {
                val updatedTips = mutableListOf<BettingTip>()
                for (tip in ticket.tips) {
                    bettingTipsManager.fetchTeamLogosAndUpdateBettingTip(tip, callback = {
                        updatedTips.add(it)
                    })
                }
                val updatedTicket = ticket.copy(tips = updatedTips)
                val updatedCount = controller.updateTicket(updatedTicket)
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

private fun Route.createTicket(
    bettingTipsManager: BettingTipManager,
    controller: TicketController
) {
    post {
        val principal = call.principal<User>()
        if (principal?.role != UserRole.ADMIN) {
            call.respond(
                HttpStatusCode.Unauthorized,
                Message(Constants.INSUFFICIENT_PERMISSIONS, HttpStatusCode.Unauthorized.value)
            )
        } else {
            val ticket = call.receive<Ticket>()
            try {
                val ticketId = ObjectId()
                val updatedTips = mutableListOf<BettingTip>()
                for (tip in ticket.tips) {
                    val alteredTip = tip.copy(_id = ObjectId().toString(), ticketId = ticketId.toString())
                    bettingTipsManager.fetchTeamLogosAndUpdateBettingTip(alteredTip, callback = {
                        updatedTips.add(it)
                    })
                }
                val updatedTicket = ticket.copy(_id = ticketId.toString(), tips = updatedTips)
                controller.insertTicket(updatedTicket)
                call.respond(HttpStatusCode.Created, updatedTicket)
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