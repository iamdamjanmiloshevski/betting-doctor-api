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
import com.twoplaylabs.data.Ticket
import com.twoplaylabs.repository.TicketsRepository
import com.twoplaylabs.util.Constants
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.date.*
import org.bson.types.ObjectId
import java.text.SimpleDateFormat
import java.util.*

/*
    Author: Damjan Miloshevski 
    Created on 25/12/2021
    Project: betting-doctor
*/
fun Route.bettingTicketController(repository: TicketsRepository) {
    route("/api/v1/betting-tickets") {
        with(repository) {
            getTickets(this)
            getTicketByDate(this)
            getTicketById(this)
            createTicket(this)
        }
    }
}

fun Route.createTicket(repository: TicketsRepository) {
    post {
        val ticket = call.receive<Ticket>()
        try {
            val ticketId = ObjectId()
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

fun Route.getTicketByDate(repository: TicketsRepository) {
    get("/search") {
        val date = call.request.queryParameters["date"]
        try {
            date ?: call.respond(
                HttpStatusCode.BadRequest,
                Message("Please provide a valid date", HttpStatusCode.BadRequest.value)
            )
            val sdf = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())
            date?.let { gmtDate ->
                val dateToSearchBy = sdf.parse(gmtDate)
                println("date $date")
                val ticket = repository.findTicketByDate(dateToSearchBy)
                println("TICKET $ticket")
                ticket?.let { call.respond(HttpStatusCode.OK, ticket) } ?: call.respond(HttpStatusCode.NotFound)
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
    get("/{id}") {
        val id = call.parameters["id"] ?: call.respond(
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
