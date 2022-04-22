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
import com.twoplaylabs.controllers.TicketController
import com.twoplaylabs.data.BettingTip
import com.twoplaylabs.data.Ticket
import com.twoplaylabs.data.common.Message
import com.twoplaylabs.resources.Tickets
import com.twoplaylabs.util.BettingTipManager
import com.twoplaylabs.util.Constants
import com.twoplaylabs.util.toDateFromQueryParam
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId
import org.koin.java.KoinJavaComponent.inject

/*
    Author: Damjan Miloshevski 
    Created on 05/04/2022
    Project: betting-doctor
*/
fun Route.ticketsController(controller: TicketController) {
    val bettingTipsManager by inject<BettingTipManager>(BettingTipManager::class.java)
    authenticate(System.getenv(Constants.AUTH_CONFIG_ADMIN)) {
        createTicket(bettingTipsManager, controller)
        updateTicket(bettingTipsManager, controller)
        deleteTicketById(controller)
        deleteAllTickets(controller)
    }
    tickets(controller)
    findTicketById(controller)
}

private fun Route.findTicketById(controller: TicketController) {
    get<Tickets.Id> { ticket ->
        try {
            val ticketInDb = controller.findTicketById(ticket.id)
            ticketInDb?.let { call.respond(it) } ?: call.respond(HttpStatusCode.NotFound)
        } catch (e: Throwable) {
            application.log.error(e.message)
            call.respond(
                HttpStatusCode.BadRequest,
                Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
            )
        }
    }
}


private fun Route.tickets(controller: TicketController) {
    get<Tickets> { ticket ->
        if (ticket.date != null) {

            //get ticket by date
            val dateParam = ticket.date
            println("Getting tickets by date $dateParam")
            try {
                val date = dateParam.toDateFromQueryParam()
                val ticketInDb = controller.findTicketByDate(date)
                ticketInDb?.let { call.respond(HttpStatusCode.OK, ticketInDb) } ?: call.respond(
                    HttpStatusCode.NotFound,
                    Message("Apologies we have nothing for you", HttpStatusCode.NoContent.value, date)
                )
            } catch (e: Throwable) {
                application.log.error(e.message)
                call.respond(
                    HttpStatusCode.BadRequest,
                    Message(Constants.SOMETHING_WENT_WRONG, HttpStatusCode.BadRequest.value)
                )
            }
        } else {
            try {
                println("Getting all tickets")
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
}

private fun Route.deleteAllTickets(controller: TicketController) {
   delete<Tickets> {
       call.authorize()
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
    delete<Tickets.Id> {ticket->
        call.authorize()
        try {
            val deletedCount = controller.deleteTicket(ticket.id)
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
    put<Tickets> {
        call.authorize()
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
                call.respond(HttpStatusCode.Accepted, updatedTicket)
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

private fun Route.createTicket(
    bettingTipsManager: BettingTipManager,
    controller: TicketController
) {
    post<Tickets> {
        call.authorize()
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